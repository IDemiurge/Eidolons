package eidolons.entity.active;

import eidolons.ability.ActionGenerator;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.spaces.FeatSpaceInitializer;
import eidolons.entity.active.spaces.IFeatSpaceInitializer;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.attach.DC_PassiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.RuleEnums;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.combat.attack.extra_attack.ExtraAttacksRule;
import eidolons.game.core.game.DC_Game;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.enums.entity.ActionEnums.HIDDEN_ACTIONS;
import main.content.enums.entity.ActionEnums.STD_ACTIONS;
import main.content.enums.entity.ItemEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.StringMap;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Active;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.game.logic.battle.player.Player;
import main.game.logic.generic.ActionManager;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.threading.Weaver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DC_ActionManager implements ActionManager {

    protected static ArrayList<ObjType> stdObjTypes;
    protected static ArrayList<ObjType> hiddenActions;
    protected static ArrayList<ObjType> modeObjTypes;
    protected static ArrayList<ObjType> orderObjTypes;
    protected GenericGame game;
    protected HashMap<Entity, Map<String, ActiveObj>> actionsCache = new HashMap<>();
    private IFeatSpaceInitializer spaceManager;

    public DC_ActionManager(GenericGame game) {
        this.game = game;
        spaceManager = new FeatSpaceInitializer();
    }

    public static void init() {
        stdObjTypes = new ArrayList<>();
        for (STD_ACTIONS name : STD_ACTIONS.values()) {
            if (!(DataManager.getType(StringMaster
                    .format(name.name()), DC_TYPE.ACTIONS) instanceof ObjType)) {
                continue;
            }
            ObjType type = DataManager.getType(StringMaster
                    .format(name.name()), DC_TYPE.ACTIONS);

            stdObjTypes.add(type);
        }

        modeObjTypes = new ArrayList<>();
        for (STD_MODE_ACTIONS name : STD_MODE_ACTIONS.values()) {
            ObjType type = DataManager.getType(StringMaster
                    .format(name.name()), DC_TYPE.ACTIONS);

            modeObjTypes.add(type);
        }

        orderObjTypes = new ArrayList<>();
        for (STD_ORDER_ACTIONS type : STD_ORDER_ACTIONS.values()) {
            ObjType actionType = DataManager.getType(StringMaster
                    .format(type.toString()), DC_TYPE.ACTIONS);
            orderObjTypes.add(actionType);
        }

        hiddenActions = new ArrayList<>();
        for (HIDDEN_ACTIONS name : HIDDEN_ACTIONS.values()) {
            ObjType type = DataManager.getType(StringMaster
                    .format(name.name()), DC_TYPE.ACTIONS);

            hiddenActions.add(type);
        }
    }

    public static List<DC_ActiveObj> filterActionsByCanBePaid(List<DC_ActiveObj> actions) {
        List<DC_ActiveObj> list = new ArrayList<>();
        for (DC_ActiveObj action : actions) {
            if (action.canBeActivated()) {
                list.add(action);
            }
        }
        // ++ can be targeted
        return list;
    }

    public static ACTION_TYPE_GROUPS getStdObjType(DC_ActiveObj activeObj) {
        for (STD_ACTIONS action : STD_ACTIONS.values()) {
            if (action.toString().equals(activeObj.getType().getName())) {
                switch (action) {
                    case Attack:
                        return ACTION_TYPE_GROUPS.ATTACK;
                    case Move:
                        return ACTION_TYPE_GROUPS.MOVE;
                    case Turn_Anticlockwise:
                    case Turn_Clockwise:
                        return ACTION_TYPE_GROUPS.TURN;

                    default:
                        break;

                }
            }
        }
        for (STD_ACTIONS action : STD_ACTIONS.values()) {
            if (activeObj.getType().getName().contains(action.toString())) {
                switch (action) {
                    case Attack:
                        return ACTION_TYPE_GROUPS.ATTACK;
                    case Move:
                        return ACTION_TYPE_GROUPS.MOVE;
                    case Turn_Anticlockwise:
                    case Turn_Clockwise:
                        return ACTION_TYPE_GROUPS.TURN;
                    default:
                        break;

                }
            }
        }

        return null;
    }

    protected void checkSetFeatRef(DC_UnitAction action) {
        DequeImpl<DC_PassiveObj> skills = new DequeImpl<>(action.getOwnerUnit().getSkills());
        skills.addAll(action.getOwnerUnit().getClasses());
        for (DC_PassiveObj s : skills) {
            if (StringMaster.contains(s.getProperty(G_PROPS.ACTIVES), action.getName())) {
                action.getRef().setID(KEYS.SKILL, s.getId());
                return;
            }
        }

    }

    @Override
    public DC_UnitAction newAction(ObjType type, Ref ref, Player owner, GenericGame game) {
        DC_UnitAction action = new DC_UnitAction(type, owner, game, ref);
        game.getState().addObject(action);
        // to graveyard
        checkSetFeatRef(action);

        Map<String, ActiveObj> map = actionsCache.get(ref.getSourceObj());
        if (map == null) {
            map = new StringMap<>();
            actionsCache.put(ref.getSourceObj(), map);
        }
        map.put(action.getName(), action);
        return action;
    }

    public Spell newSpell(ObjType type, Ref ref, Player owner, GenericGame game) {
        Spell action = new Spell(type, owner, (DC_Game) game, ref);
        game.getState().addObject(action);
        Map<String, ActiveObj> map = actionsCache.get(ref.getSourceObj());
        if (map == null) {
            map = new StringMap<>();
            actionsCache.put(ref.getSourceObj(), map);
        }
        map.put(action.getName(), action);
        return action;
    }

    public boolean activateAction(Obj target, Obj source, Active action) {
        Ref ref = source.getRef().getCopy();
        ref.setTarget(target.getId());
        ref.setTriggered(true);
        return action.activatedOn(ref);
    }

    public boolean activateAttackOfOpportunity(ActiveObj action, Obj countering, boolean free) {
        return true;
    }


    @Override
    public DC_ActiveObj findCounterAttack(ActiveObj action, Obj _countering) {
        Unit target = (Unit) action.getOwnerUnit();
        Unit source = (Unit) _countering;
        return (DC_ActiveObj) getCounterAttackAction(target, source,
                (DC_ActiveObj) action);
    }


    public Active getCounterAttackAction(Unit countered, Unit countering,
                                         DC_ActiveObj active) {
        DC_ActiveObj counterAttack = countering.getPreferredCounterAttack();
        if (counterAttack != null) {
            if (counterAttack.canBeActivatedAsCounter()) {
                if (counterAttack.canBeTargeted(active.getOwnerUnit().getId())) {
                    return counterAttack;
                }
            }
        }
        for (DC_ActiveObj attack : ExtraAttacksRule.getCounterAttacks(active, countering)) {
            //            if (AnimMaster.isTestMode())

            if (attack.canBeActivatedAsCounter()) {
                if (attack.canBeTargeted(active.getOwnerUnit().getId())) {
                    return attack;
                }
            }
        }
        return null;
    }

    private Spell newSpell(String typeName, Entity entity) {
        return (Spell) newActive(typeName, entity, true);
    }

    @Override
    public DC_UnitAction newAction(String typeName, Entity entity) {
        return (DC_UnitAction) newActive(typeName, entity, false);
    }

    public DC_ActiveObj newActive(String typeName, Entity entity, boolean spell) {
        Map<String, ActiveObj> map = actionsCache.get(entity);
        if (map == null) {
            map = new StringMap<>();
            actionsCache.put(entity, map);
        }
        if (map.get(typeName) != null) {
            return (DC_UnitAction) map.get(typeName);
        }

        ObjType type = DataManager.getType(typeName, spell ? DC_TYPE.SPELLS : DC_TYPE.ACTIONS);
        if (type == null) {
            LogMaster.log(1, "no such active: " + typeName);
            if (DUMMY_ACTION_TYPE == null) {
                DUMMY_ACTION_TYPE = DataManager.getType(DUMMY_ACTION, DC_TYPE.ACTIONS);
            }
            type = new ObjType(typeName, DUMMY_ACTION_TYPE);
        }
        Ref ref = Ref.getCopy(entity.getRef());
        if (spell) {
            return newSpell(type, ref, entity.getOwner(), game);
        }
        return newAction(type, ref, entity.getOwner(), game);
    }

    @Override
    public DC_ActiveObj getAction(String typeName, Entity entity) {
        return getAction(typeName, entity, true);
    }

    public DC_UnitAction getOrCreateAction(String typeName, Entity entity) {
        return (DC_UnitAction) getAction(typeName, entity, false);
    }

    public DC_ActiveObj getOrCreateActionOrSpell(String typeName, Entity entity) {
        return getAction(typeName, entity, false);
    }

    public DC_ActiveObj getAction(String typeName, Entity entity, boolean onlyIfAlreadyPresent) {

        typeName = typeName.trim();
        if (actionsCache.get(entity) == null) {
            actionsCache.put(entity, new StringMap<>());
        }
        ActiveObj action = actionsCache.get(entity).get(typeName);
        if (action == null) {

            if (entity instanceof Unit) {
                action = ((Unit) entity).getSpell(typeName);
                if (action != null) {
                    return (DC_ActiveObj) action;
                }
            }

            if (onlyIfAlreadyPresent) {
                if (!StringMaster.contains(entity.getProperty(G_PROPS.ACTIVES), typeName)) {
                    return null;
                }
            }

            //            action =new SearchMaster<ActiveObj>().find(typeName, entity.getActives());
            //            if (action != null)
            //                return (DC_ActiveObj) action;

            if (DataManager.isTypeName(typeName, DC_TYPE.SPELLS)) {
                // spell = true;
                action = newSpell(typeName, entity);
            } else {
                action = newAction(typeName, entity);
            }
            actionsCache.get(entity).put(typeName, action);
        }
        return (DC_ActiveObj) action;
    }


    @Override
    public void resetActions(Entity entity) {

    }

    public List<DC_UnitAction> getOrCreateWeaponActions(DC_WeaponObj weapon) {
        if (weapon == null) {
            return new ArrayList<>();
        }
        Unit obj = weapon.getOwnerObj();
        if (obj == null) {
            return new ArrayList<>();
        }
        List<DC_UnitAction> list = weapon.getAttackActions();
        if (list != null) {
            return new ArrayList<>(list);
        }
        list = new ArrayList<>();
        for (String typeName : ContainerUtils.open(weapon.getProperty(PROPS.WEAPON_ATTACKS))) {
            if (!weapon.isMainHand()) {
                typeName = ActionGenerator.getOffhandActionName(typeName);
            }
            DC_UnitAction action = getOrCreateAction(typeName, obj);

            // action.setObjTypeGroup(ACTION_TYPE_GROUPS.HIDDEN);
            // TODO so it's inside normal attack then?
            if (action == null) {
                continue;
            }
            list.add(action);
        }
        if (checkAddThrowAction(weapon.getOwnerObj(), weapon)) {
            DC_UnitAction throwAction = getOrCreateAction(weapon.isMainHand() ? THROW_MAIN : THROW_OFFHAND,
                    obj);
            throwAction.setName(getThrowName(weapon.getName()));
            list.add(throwAction);
        }
        weapon.setAttackActions(new ArrayList<>(list));
        return list;
    }

    public static String getThrowName(String itemName) {
        return "Throw " + itemName;
    }


    protected ActiveObj getDisarmAction(final Unit hero, final Trap trap) {
        DC_UnitAction action = new DC_UnitAction(DataManager.getType(DISARM, DC_TYPE.ACTIONS),
                hero.getOwner(), game, hero.getRef().getCopy()) {
            public boolean resolve() {
                //                animate(); // pass std icon as param?
                TrapMaster.tryDisarmTrap(trap);
                // false if dead as result?
                game.getManager().reset();
                game.getManager().refresh(ownerObj.getOwner().isMe());
                return true;
            }
        };
        actionsCache.get(hero).put(DISARM, action);
        return action;
    }

    protected ActiveObj getUnlockAction(final Unit hero, final Entity e) {
        DC_UnitAction action = new DC_UnitAction(DataManager.getType(UNLOCK, DC_TYPE.ACTIONS),
                hero.getOwner(), game, hero.getRef().getCopy()) {
            public boolean resolve() {
                //                animate(); // pass std icon as param?
                //                LockMaster.tryUnlock(e, hero);
                game.getManager().reset();
                game.getManager().refresh(ownerObj.getOwner().isMe());
                return true;
            }
        };
        actionsCache.get(hero).put(UNLOCK, action);
        return action;
    }

    protected boolean checkAddThrowAction(Unit unit, DC_WeaponObj weapon) {
        if (!RuleKeeper.checkFeature(RuleEnums.FEATURE.THROW_WEAPON))
            return false;
        if (weapon == null) {
            return false;
        }

        if (weapon.getName().contains("Battle Spear")) {
            return true;
        }
        if (weapon.isShield()) {
            if (weapon.getWeaponGroup() == ItemEnums.WEAPON_GROUP.BUCKLERS) {
                if (unit.checkBool(GenericEnums.STD_BOOLS.BUCKLER_THROWER)) {
                    return false;
                }
            }
        }
        if (!weapon.isWeapon()) {
            return false;
        }
        if (weapon.isRanged()) {
            return false;
        }
        if (weapon.isNatural()) {
            return false;
        }
        Integer bonus = unit.getIntParam(PARAMS.THROW_SIZE_BONUS);

        if (bonus > -2) {
            if (weapon.getWeaponSize() == ItemEnums.WEAPON_SIZE.TINY) {
                return true;
            }
        }
        if (bonus > -1) {
            if (weapon.getWeaponSize() == ItemEnums.WEAPON_SIZE.SMALL) {
                return true;
            }
        }
        if (bonus > 2) {
            if (weapon.getWeaponSize() == ItemEnums.WEAPON_SIZE.HUGE) {
                return true;
            }
        }
        if (bonus > 1) {
            if (weapon.getWeaponSize() == ItemEnums.WEAPON_SIZE.LARGE) {
                return true;
            }
        }
        if (bonus > 0) {
            return weapon.getWeaponSize() == ItemEnums.WEAPON_SIZE.MEDIUM;
        }
        return false;
    }

    public void constructActionMaps(Unit unit) {
        for (ACTION_TYPE sub : unit.getActionMap().keySet()) {
            unit.getActionMap().put(sub, new DequeImpl<>());
        }
        for (ActiveObj active : unit.getActives()) {
            DC_UnitAction action = (DC_UnitAction) active;
            ACTION_TYPE type = action.getActionType();
            if (type == null) {
                type = action.getActionType();
            }
            DequeImpl<DC_UnitAction> list = unit.getActionMap().get(type);

            if (!hiddenActions.contains(action.getType())) {
                list.add(action);
            }

        }
    }

    protected List<DC_UnitAction> getObjTypes(List<? extends ObjType> actionTypes,
                                              Unit unit) {
        List<DC_UnitAction> list = new ArrayList<>();
        for (ObjType type : actionTypes) {
            if (type == null)
                continue;
            // Ref ref = Ref.getCopy(unit.getRef());
            DC_UnitAction action = getOrCreateAction(type.getName(), unit);
            // = getOrCreateAction(type, ref, unit.getOwner(), game);
            list.add(action);
        }
        return list;
    }

    @Override
    public void resetCostsInNewThread() {
        Weaver.inNewThread(() -> {
            DC_Game game = (DC_Game) this.game;
            if (game.getManager().getActiveObj() == null) {
                return;
            }
            for (ACTION_TYPE key : game.getManager().getActiveObj().getActionMap()
                    .keySet()) {
                for (DC_ActiveObj active : game.getManager().getActiveObj()
                        .getActionMap().get(key)) {
                    active.initCosts();
                }
            }

            for (DC_ActiveObj active : game.getManager().getActiveObj().getSpells()) {
                active.initCosts();
            }

            for (DC_QuickItemObj item : game.getManager().getActiveObj().getQuickItems()) {
                if (item.getActive() != null) {
                    item.getActive().initCosts();
                }
            }
        });
    }

    public void clearCache() {
        actionsCache.clear();
    }


    public IFeatSpaceInitializer getSpaceManager() {
        return spaceManager;
    }
}
