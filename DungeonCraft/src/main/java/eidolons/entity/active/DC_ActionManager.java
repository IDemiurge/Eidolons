package eidolons.entity.active;

import eidolons.ability.ActionGenerator;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.active.spaces.FeatSpaceInitializer;
import eidolons.entity.active.spaces.IFeatSpaceInitializer;
import eidolons.entity.item.QuickItem;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.unit.attach.DC_PassiveObj;
import eidolons.entity.unit.Unit;
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
import main.entity.obj.IActiveObj;
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
    protected HashMap<Entity, Map<String, IActiveObj>> actionsCache = new HashMap<>();
    private IFeatSpaceInitializer spaceManager;

    public DC_ActionManager(GenericGame game) {
        this.game = game;
        spaceManager = new FeatSpaceInitializer();
    }

    public static void init() {
        hiddenActions = new ArrayList<>();
        for (HIDDEN_ACTIONS name : HIDDEN_ACTIONS.values()) {
            ObjType type = DataManager.getType(StringMaster
                    .format(name.name()), DC_TYPE.ACTIONS);

            hiddenActions.add(type);
        }
    }

    public static List<ActiveObj> filterActionsByCanBePaid(List<ActiveObj> actions) {
        List<ActiveObj> list = new ArrayList<>();
        for (ActiveObj action : actions) {
            if (action.canBeActivated()) {
                list.add(action);
            }
        }
        // ++ can be targeted
        return list;
    }

    public static ACTION_TYPE_GROUPS getStdObjType(ActiveObj activeObj) {
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

    protected void checkSetFeatRef(UnitAction action) {
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
    public UnitAction newAction(ObjType type, Ref ref, Player owner, GenericGame game) {
        UnitAction action = new UnitAction(type, owner, game, ref);
        game.getState().addObject(action);
        // to graveyard
        checkSetFeatRef(action);

        Map<String, IActiveObj> map = actionsCache.get(ref.getSourceObj());
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
        Map<String, IActiveObj> map = actionsCache.get(ref.getSourceObj());
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

    public boolean activateAttackOfOpportunity(IActiveObj action, Obj countering, boolean free) {
        return true;
    }


    @Override
    public ActiveObj findCounterAttack(IActiveObj action, Obj _countering) {
        Unit target = (Unit) action.getOwnerUnit();
        Unit source = (Unit) _countering;
        return (ActiveObj) getCounterAttackAction(target, source,
                (ActiveObj) action);
    }


    public Active getCounterAttackAction(Unit countered, Unit countering,
                                         ActiveObj active) {
        for (ActiveObj attack : ExtraAttacksRule.getCounterAttacks(active, countering)) {
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
    public UnitAction newAction(String typeName, Entity entity) {
        return (UnitAction) newActive(typeName, entity, false);
    }

    public ActiveObj newActive(String typeName, Entity entity, boolean spell) {
        Map<String, IActiveObj> map = actionsCache.get(entity);
        if (map == null) {
            map = new StringMap<>();
            actionsCache.put(entity, map);
        }
        if (map.get(typeName) != null) {
            return (UnitAction) map.get(typeName);
        }

        ObjType type = DataManager.getType(typeName, spell ? DC_TYPE.SPELLS : DC_TYPE.ACTIONS);
        if (type == null) {
            LogMaster.log(1, "no such active: " + typeName);
            throw new RuntimeException("no such active: " + typeName);
        }
        Ref ref = Ref.getCopy(entity.getRef());
        if (spell) {
            return newSpell(type, ref, entity.getOwner(), game);
        }
        return newAction(type, ref, entity.getOwner(), game);
    }

    @Override
    public ActiveObj getAction(String typeName, Entity entity) {
        return getAction(typeName, entity, true);
    }

    public UnitAction getOrCreateAction(String typeName, Entity entity) {
        return (UnitAction) getAction(typeName, entity, false);
    }

    public ActiveObj getOrCreateActionOrSpell(String typeName, Entity entity) {
        return getAction(typeName, entity, false);
    }

    public ActiveObj getAction(String typeName, Entity entity, boolean onlyIfAlreadyPresent) {
        typeName = typeName.trim();
        if (actionsCache.get(entity) == null) {
            actionsCache.put(entity, new StringMap<>());
        }
        IActiveObj action = actionsCache.get(entity).get(typeName);
        if (action == null) {

            if (entity instanceof Unit) {
                action = ((Unit) entity).getSpell(typeName);
                if (action != null) {
                    return (ActiveObj) action;
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
        return (ActiveObj) action;
    }


    @Override
    public void resetActions(Entity entity) {

    }

    public List<UnitAction> getOrCreateWeaponActions(WeaponItem weapon) {
        if (weapon == null) {
            return new ArrayList<>();
        }
        Unit obj = weapon.getOwnerObj();
        if (obj == null) {
            return new ArrayList<>();
        }
        List<UnitAction> list = weapon.getAttackActions();
        if (list != null) {
            return new ArrayList<>(list);
        }
        list = new ArrayList<>();
        for (String typeName : ContainerUtils.open(weapon.getProperty(PROPS.WEAPON_ATTACKS))) {
            if (!weapon.isMainHand()) {
                typeName = ActionGenerator.getOffhandActionName(typeName);
            }
            UnitAction action = getOrCreateAction(typeName, obj);

            // action.setObjTypeGroup(ACTION_TYPE_GROUPS.HIDDEN);
            // TODO so it's inside normal attack then?
            if (action == null) {
                continue;
            }
            list.add(action);
        }
        if (checkAddThrowAction(weapon.getOwnerObj(), weapon)) {
            //TODO
            // DC_UnitAction throwAction = getOrCreateAction(weapon.isMainHand() ? THROW_MAIN : THROW_OFFHAND,
            //         obj);
            // throwAction.setName(getThrowName(weapon.getName()));
            // list.add(throwAction);
        }
        weapon.setAttackActions(new ArrayList<>(list));
        return list;
    }

    public static String getThrowName(String itemName) {
        return "Throw " + itemName;
    }

    protected boolean checkAddThrowAction(Unit unit, WeaponItem weapon) {
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
        for (IActiveObj active : unit.getActives()) {
            UnitAction action = (UnitAction) active;
            ACTION_TYPE type = action.getActionType();
            if (type == null) {
                type = action.getActionType();
            }
            DequeImpl<UnitAction> list = unit.getActionMap().get(type);

            if (!hiddenActions.contains(action.getType())) {
                list.add(action);
            }

        }
    }

    protected List<UnitAction> getObjTypes(List<? extends ObjType> actionTypes,
                                           Unit unit) {
        List<UnitAction> list = new ArrayList<>();
        for (ObjType type : actionTypes) {
            if (type == null)
                continue;
            // Ref ref = Ref.getCopy(unit.getRef());
            UnitAction action = getOrCreateAction(type.getName(), unit);
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
                for (ActiveObj active : game.getManager().getActiveObj()
                        .getActionMap().get(key)) {
                    active.initCosts();
                }
            }

            for (ActiveObj active : game.getManager().getActiveObj().getSpells()) {
                active.initCosts();
            }

            for (QuickItem item : game.getManager().getActiveObj().getQuickItems()) {
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
