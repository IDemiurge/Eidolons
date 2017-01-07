package main.rules;

import main.ability.ActionGenerator;
import main.content.CONTENT_CONSTS.*;
import main.content.CONTENT_CONSTS2.STD_ACTION_MODES;
import main.content.ContentManager;
import main.content.OBJ_TYPES;
import main.content.PARAMS;
import main.content.PROPS;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.ActionManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.*;
import main.entity.obj.top.DC_ActiveObj;
import main.entity.type.ActionType;
import main.entity.type.ObjType;
import main.game.MicroGame;
import main.game.logic.dungeon.DungeonLevelMaster;
import main.game.logic.dungeon.Entrance;
import main.game.logic.dungeon.special.LockMaster;
import main.game.logic.dungeon.special.Trap;
import main.game.logic.dungeon.special.TrapMaster;
import main.game.player.Player;
import main.rules.generic.UnitAnalyzer;
import main.rules.mechanics.ExtraAttacksRule;
import main.rules.mechanics.FleeRule;
import main.rules.mechanics.RuleMaster;
import main.rules.mechanics.RuleMaster.FEATURE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.DequeImpl;

import java.util.*;

public class DC_ActionManager implements ActionManager {

    public static final int STD_ACTION_N = 5;
    public static final int MODES_N = 5;
    public static final String OFFHAND_ATTACK = StringMaster
            .getWellFormattedString(STD_SPEC_ACTIONS.OFFHAND_ATTACK.name());
    public static final String DUAL_ATTACK = StringMaster
            .getWellFormattedString(STD_SPEC_ACTIONS.DUAL_ATTACK.name());
    public static final String ATTACK_OF_OPPORTUNITY = StringMaster
            .getWellFormattedString(HIDDEN_ACTIONS.Attack_of_Opportunity.name());
    public static final String FREE_ATTACK_OF_OPPORTUNITY = StringMaster
            .getWellFormattedString(HIDDEN_ACTIONS.Free_Attack_of_Opportunity.name());
    public static final String COUNTER_ATTACK = StringMaster
            .getWellFormattedString(HIDDEN_ACTIONS.Counter_Attack.name());
    public static final String FREE_COUNTER_ATTACK = StringMaster
            .getWellFormattedString(HIDDEN_ACTIONS.Free_Counter_Attack.name());
    public static final G_PROPS ACTIVES = G_PROPS.ACTIVES;
    public static final String ATTACK = StringMaster.getWellFormattedString(STD_ACTIONS.Attack
            .name());
    public static final String RELOAD = "Reload";
    public static final String THROW = "Throw";
    public static final String CLUMSY_LEAP = "Clumsy Leap";
    public static final String MOVE_RIGHT = "Move Right";
    public static final String MOVE_LEFT = "Move Left";
    public static final String MOVE_BACK = "Move Back";
    public static final String FLEE = "Flee";
    public static final String SEARCH_MODE = "Search Mode";
    public static final String THROW_MAIN = "Throw Main Hand Weapon";
    public static final String THROW_OFFHAND = "Throw Off Hand Weapon";
    public static final String TOSS_ITEM = "Toss Item";
    public static final String ENTER = "Enter";
    public static final String DISARM = "Disarm";
    public static final String UNLOCK = "Unlock";
    private static final String DUMMY_ACTION = "Dummy Action";
    private static final String USE_INVENTORY = StringMaster
            .getWellFormattedString(STD_SPEC_ACTIONS.Use_Inventory.toString());
    private static final String DIVINATION = "Divination";
    private static final String PICK_UP = "Pick Up Items";
    private static LinkedList<ActionType> stdActionTypes;
    private static LinkedList<ActionType> hiddenActions;
    private static LinkedList<ActionType> modeActionTypes;
    private MicroGame game;
    private HashMap<Entity, Map<String, ActiveObj>> actionsCache = new HashMap<>();
    public DC_ActionManager(MicroGame game) {
        this.game = game;
    }

    public static void init() {
        stdActionTypes = new LinkedList<ActionType>();
        for (STD_ACTIONS name : STD_ACTIONS.values()) {
            ActionType type = (ActionType) DataManager.getType(StringMaster
                    .getWellFormattedString(name.name()), OBJ_TYPES.ACTIONS);

            stdActionTypes.add(type);
        }

        modeActionTypes = new LinkedList<ActionType>();
        for (STD_MODE_ACTIONS name : STD_MODE_ACTIONS.values()) {
            ActionType type = (ActionType) DataManager.getType(StringMaster
                    .getWellFormattedString(name.name()), OBJ_TYPES.ACTIONS);

            modeActionTypes.add(type);
        }
        hiddenActions = new LinkedList<ActionType>();
        for (HIDDEN_ACTIONS name : HIDDEN_ACTIONS.values()) {
            ActionType type = (ActionType) DataManager.getType(StringMaster
                    .getWellFormattedString(name.name()), OBJ_TYPES.ACTIONS);

            hiddenActions.add(type);
        }
    }

    public static List<DC_ActiveObj> filterActionsByCanBePaid(List<DC_ActiveObj> actions) {
        List<DC_ActiveObj> list = new ArrayList<>();
        for (DC_ActiveObj action : actions)
            if (action.canBeActivated())
                list.add(action);
        // ++ can be targeted
        return list;
    }

    public static ACTION_TYPE_GROUPS getStdActionType(DC_ActiveObj activeObj) {
        for (STD_ACTIONS action : STD_ACTIONS.values()) {
            if (action.toString().equals(activeObj.getType().getName()))
                switch (action) {
                    case Attack:
                        return ACTION_TYPE_GROUPS.ATTACK;
                    case Move:
                        return ACTION_TYPE_GROUPS.MOVE;
                    case Turn_Anticlockwise:
                        return ACTION_TYPE_GROUPS.TURN;
                    case Turn_Clockwise:
                        return ACTION_TYPE_GROUPS.TURN;
                    case Wait:
                        return ACTION_TYPE_GROUPS.MODE;
                    default:
                        break;

                }
        }
        for (STD_ACTIONS action : STD_ACTIONS.values()) {
            if (activeObj.getType().getName().contains(action.toString()))
                switch (action) {
                    case Attack:
                        return ACTION_TYPE_GROUPS.ATTACK;
                    case Move:
                        return ACTION_TYPE_GROUPS.MOVE;
                    case Turn_Anticlockwise:
                        return ACTION_TYPE_GROUPS.TURN;
                    case Turn_Clockwise:
                        return ACTION_TYPE_GROUPS.TURN;
                    case Wait:
                        return ACTION_TYPE_GROUPS.MODE;
                    default:
                        break;

                }
        }

        return null;
    }

    private void checkSetFeatRef(DC_UnitAction action) {
        DequeImpl<DC_FeatObj> skills = new DequeImpl<>(action.getOwnerObj().getSkills());
        skills.addAll(action.getOwnerObj().getClasses());
        for (DC_FeatObj s : skills) {
            if (StringMaster.contains(s.getProperty(G_PROPS.ACTIVES), action.getName())) {
                action.getRef().setID(KEYS.SKILL, s.getId());
                return;
            }
        }

    }

    @Override
    public DC_UnitAction newAction(ActionType type, Ref ref, Player owner, MicroGame game) {

        DC_UnitAction action = new DC_UnitAction(type, owner, game, ref);
        game.getState().addObject(action);
        // to graveyard
        checkSetFeatRef(action);

        Map<String, ActiveObj> map = actionsCache.get(ref.getSourceObj());
        if (map == null) {
            map = new HashMap<>();
            actionsCache.put(ref.getSourceObj(), map);
        }
        map.put(action.getName(), action);

        return action;
    }

    public boolean activateAction(Obj target, Obj source, Active action) {
        Ref ref = source.getRef().getCopy();
        ref.setTarget(target.getId());
        ref.setTriggered(true);
        return action.activate(ref);
    }

    public boolean activateAttackOfOpportunity(ActiveObj action, Obj countering, boolean free) {
        try {
            // activateAction(countered, countering, action); TODO
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public ActiveObj activateCounterAttack(ActiveObj action, Obj _countering) {
        DC_HeroObj target = (DC_HeroObj) action.getOwnerObj();
        DC_HeroObj source = (DC_HeroObj) _countering;
        DC_ActiveObj counter = (DC_ActiveObj) getCounterAttackAction(target, source,
                (DC_ActiveObj) action);
        if (counter == null)
            return null;
        counter.setCounterMode(true);
        boolean result = false;
        try {
            result = activateAction(target, source, counter);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            counter.setCounterMode(false);
        }
        if (!result)
            return null;
        // TODO ?
        return counter;
    }

    private Active getCounterAttackAction(DC_HeroObj countered, DC_HeroObj countering,
                                          DC_ActiveObj active) {
        DC_ActiveObj counterAttack = countering.getPreferredCounterAttack();
        if (counterAttack != null)
            if (counterAttack.canBeActivatedAsCounter())
                if (counterAttack.canBeTargeted(active.getOwnerObj().getId()))
                    return counterAttack;
        for (DC_ActiveObj attack : ExtraAttacksRule.getCounterAttacks(active, countering)) {
            if (attack.canBeActivatedAsCounter())
                if (attack.canBeTargeted(active.getOwnerObj().getId()))
                    return attack;
        }
        return null;
    }

    private ActiveObj getStdAction(Obj obj, String _default, PROPERTY prop) {
        String abilTypeName = _default;
        if (!StringMaster.isEmpty(obj.getProperty(prop)))
            if (DataManager.isTypeName(obj.getProperty(prop)))
                abilTypeName = obj.getProperty(prop);
        return getAction(abilTypeName, obj);
    }

    @Override
    public DC_UnitAction newAction(String typeName, Entity entity) {
        Map<String, ActiveObj> map = actionsCache.get(entity);
        if (map == null) {
            map = new HashMap<>();
            actionsCache.put(entity, map);
        }
        if (map.get(typeName) != null)
            return (DC_UnitAction) map.get(typeName);

        ActionType type = (ActionType) DataManager.getType(typeName, OBJ_TYPES.ACTIONS);
        if (type == null) {
            main.system.auxiliary.LogMaster.log(1, "no such active: " + typeName);
            return null;
        }
        Ref ref = Ref.getCopy(entity.getRef());

        return newAction(type, ref, entity.getOwner(), game);
    }

    @Override
    public DC_ActiveObj getAction(String typeName, Entity entity) {
        if (actionsCache.get(entity) == null)
            actionsCache.put(entity, new HashMap<String, ActiveObj>());
        ActiveObj action = actionsCache.get(entity).get(typeName);
        if (action == null) {
            action = newAction(typeName, entity);
            actionsCache.get(entity).put(typeName, action);
        }
        return (DC_ActiveObj) action;
    }

    public List<DC_ActiveObj> generateStandardSubactionsForUnit(DC_HeroObj unit) {
        List<DC_ActiveObj> actions = new LinkedList<>();
        // actions.addAll(generateModesForUnit(unit,
        // ACTION_TYPE_GROUPS.ATTACK));
        actions.addAll(generateModesForUnit(unit, ACTION_TYPE_GROUPS.MOVE));
        actions.addAll(generateModesForUnit(unit, ACTION_TYPE_GROUPS.TURN));
        // actions.addAll(generateModesForUnit(unit, ACTION_TYPE_GROUPS.MODE));
        actions.addAll(getSpecialModesFromUnit(unit));
        return actions;
    }

    private List<DC_ActiveObj> getSpecialModesFromUnit(DC_HeroObj unit) {
        List<DC_ActiveObj> subActions = new LinkedList<>();
        for (String mode : StringMaster.openContainer(unit.getProperty(PROPS.SPECIAL_ACTION_MODES))) {
            DC_UnitAction baseAction = unit.getAction(VariableManager.getVar(mode));
            DC_UnitAction subAction;
            if (DataManager.getType(VariableManager.removeVarPart(mode), OBJ_TYPES.ACTIONS) != null) {
                subAction = generateModeAction(VariableManager.removeVarPart(mode), baseAction);
            }
            subAction = newAction(VariableManager.removeVarPart(mode), unit);
            baseAction.getSubActions().add(subAction);
            // DataManager.getType(VariableManager.getVar(mode),
            // OBJ_TYPES.ACTIONS);
        }

        return subActions;
    }

    // public boolean activateAttack(Obj _attacked, Obj _attacker) {
    // // for Communicator
    // // ActionType type = (ActionType) DataManager.getType(ATTACK,
    // // OBJ_TYPES.ACTIONS);
    // //
    // // Ref ref = new Ref(game, _attacker.getId());
    // // ref.setTarget(_attacked.getId());
    // // type.setOwnerObj(_attacker);
    // // if (!type.canBeActivated(ref)) {
    // // return false;
    // // }
    // // return type.activate(ref);
    //
    // }

    private DC_UnitAction generateModeAction(String mode, DC_UnitAction baseAction) {
        return generateModeAction(new EnumMaster<STD_ACTION_MODES>().retrieveEnumConst(
                STD_ACTION_MODES.class, mode), baseAction);
    }

    public List<DC_ActiveObj> generateModesForUnit(DC_HeroObj unit, ACTION_TYPE_GROUPS group) {
        List<DC_ActiveObj> actions = new LinkedList<>();
        for (ActiveObj active : unit.getActives()) {
            DC_ActiveObj action = (DC_ActiveObj) active;
            if (action.getActionGroup() != group)
                continue;
            if (!StringMaster.isEmpty(action.getActionMode()))
                continue;
            if (action.isAttack())
                continue;
            List<DC_ActiveObj> subActions = new LinkedList<>();

            for (STD_ACTION_MODES mode : STD_ACTION_MODES.values()) {
                if (checkModeForAction(action, unit, mode)) {
                    DC_UnitAction subAction = getModeAction(action, unit, mode);
                    subActions.add(subAction);
                }
            }
            action.setSubActions(subActions);
            actions.addAll(subActions);
        }

        return actions;
    }

    private boolean checkModeForAction(DC_ActiveObj action, DC_HeroObj unit, STD_ACTION_MODES mode) {
        if (hiddenActions.contains(action.getType()))
            return false;
        if (mode.getDefaultActionGroups().contains(action.getActionGroup())) {
            if (checkStdAction(action))
                return true;
        }
        if (action.getProperty(PROPS.ACTION_MODES).contains(
                StringMaster.getWellFormattedString(mode.toString())))
            return true;

        return false;
    }

    private boolean checkStdAction(DC_ActiveObj action) {
        for (STD_ACTIONS s : STD_ACTIONS.values())
            if (StringMaster.compare(s.toString(), action.getName()))
                return true;
        for (STD_SPEC_ACTIONS s : STD_SPEC_ACTIONS.values())
            if (StringMaster.compare(s.toString(), action.getName()))
                return true;
        for (ADDITIONAL_MOVE_ACTIONS s : ADDITIONAL_MOVE_ACTIONS.values())
            if (StringMaster.compare(s.toString(), action.getName()))
                return true;
        for (STD_MODE_ACTIONS s : STD_MODE_ACTIONS.values())
            if (StringMaster.compare(s.toString(), action.getName()))
                return true;
        return false;
    }

    private DC_UnitAction getModeAction(DC_ActiveObj action, DC_HeroObj unit, STD_ACTION_MODES mode) {
        DC_UnitAction subAction = (DC_UnitAction) getAction(mode.getPrefix() + action.getName(),
                unit);
        if (subAction == null)
            subAction = (generateModeAction(mode, action));
        return subAction;
    }

    private DC_UnitAction generateModeAction(STD_ACTION_MODES mode, DC_ActiveObj baseAction) {
        ActionType type = new ActionType(baseAction.getType());
        if (mode.getAddPropMap() != null)
            for (String s : mode.getAddPropMap().keySet()) {
                type.addProperty(s, mode.getAddPropMap().get(s));
            }
        if (mode.getSetPropMap() != null)
            for (String s : mode.getSetPropMap().keySet()) {
                type.setProperty(s, mode.getSetPropMap().get(s));
            }
        if (mode.getParamBonusMap() != null)
            for (String s : mode.getParamBonusMap().keySet()) {
                // type.setModifierKey(mode.getName());
                type.modifyParameter(s, mode.getParamBonusMap().get(s));
            }
        if (mode.getParamModMap() != null)
            for (String s : mode.getParamModMap().keySet()) {
                PARAMETER param = ContentManager.getPARAM(s);
                if (type.getIntParam(param) == 0)
                    type.setParam(param, param.getDefaultValue());
                type.modifyParamByPercent(param, StringMaster.getInteger(mode.getParamModMap().get(
                        s)), false);
            }
        if (mode != STD_ACTION_MODES.STANDARD)
            type.setName(mode.getPrefix() + baseAction.getName());
        DC_UnitAction subAction = newAction(type, new Ref(baseAction.getOwnerObj()), baseAction
                .getOwner(), game);
        // TODO not all!..
        subAction.setActionType(ACTION_TYPE.HIDDEN);
        subAction.setActionTypeGroup(ACTION_TYPE_GROUPS.HIDDEN);
        return subAction;
    }

    @Override
    public void resetActions(Entity entity) {
        if (!(entity instanceof DC_HeroObj))
            return;
        DC_HeroObj unit = (DC_HeroObj) entity;
        DequeImpl<ActiveObj> actives = null;
        // #1: reset prop with ids if nothing is changed
        // if (ListMaster.isNotEmpty(actives) && entity.isActivesReady()) {
        // entity.setProperty(ACTIVES, StringMaster
        // .constructContainer(StringMaster.convertToIdList(actives)));
        // return;
        // }
        actives = new DequeImpl<ActiveObj>();
        // #2: reset the list if prop has been modified (via Add/Remove effects
        // ++ items). They should set ActivesReady to false for that.
        // or upon init

        unit.setActionMap(new HashMap<ACTION_TYPE, List<DC_UnitAction>>());

        // if (!unit.isStandardActionsAdded())
        if (!unit.isBfObj())
            addStandardActions(unit, actives);

        for (String typeName : StringMaster.openContainer(entity.getProperty(ACTIVES))) {
            ObjType type = DataManager.getType(typeName, OBJ_TYPES.ACTIONS);
            DC_UnitAction action = null;
            if (type == null)
                try {
                    action = (DC_UnitAction) game.getObjectById(Integer.valueOf(typeName));
                } catch (Exception e) {
                    continue;
                }
            else {
                action = newAction(typeName, entity);
            }
            // idList.add(action.getId() + "");
            actives.add(action);
        }
        // list = new DequeImpl<>(items);
        try {
            addSpecialActions(unit, actives);
        } catch (Exception e) {
            e.printStackTrace();
        }
        unit.setActives(new LinkedList<>(actives));
        if (!unit.isBfObj())
            addHiddenActions(unit, unit.getActives());
        try {
            constructActionMaps(unit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // entity.setProperty(ACTIVES, StringMaster
        // .constructContainer(StringMaster.convertToIdList(actives)));
        entity.setActivesReady(true);
    }

    private List<DC_UnitAction> getWeaponActions(DC_WeaponObj weapon) {
        if (weapon == null)
            return new LinkedList<>();
        DC_HeroObj obj = weapon.getOwnerObj();
        if (obj == null)
            return new LinkedList<>();
        List<DC_UnitAction> list = new LinkedList<>();
        for (String typeName : StringMaster.openContainer(weapon.getProperty(PROPS.WEAPON_ATTACKS))) {
            if (!weapon.isMainHand())
                typeName = ActionGenerator.getOffhandActionName(typeName);
            DC_UnitAction action = (DC_UnitAction) getAction(typeName, obj);

            // action.setActionTypeGroup(ACTION_TYPE_GROUPS.HIDDEN);
            // TODO so it's inside normal attack then?
            if (action == null)
                continue;
            list.add(action);
        }
        if (checkAddThrowAction(weapon.getOwnerObj(), !weapon.isMainHand())) {
            DC_ActiveObj throwAction = getAction(weapon.isMainHand() ? THROW_MAIN : THROW_OFFHAND,
                    obj);
            throwAction.setName("Throw " + weapon.getName());
            list.add((DC_UnitAction) throwAction);
        }
        return list;
    }

    private void addSpecialActions(DC_HeroObj unit, DequeImpl<ActiveObj> actives) {
        // should be another passive to deny unit even those commodities...

        actives.add(newAction(DUMMY_ACTION, unit));
        if (unit.isBfObj())
            return;

        actives.add(newAction(MOVE_LEFT, unit));
        actives.add(newAction(MOVE_RIGHT, unit));
        actives.add(newAction(MOVE_BACK, unit));
        actives.add(newAction(SEARCH_MODE, unit));

        if (!unit.isHuge() && !unit.checkPassive(STANDARD_PASSIVES.CLUMSY)) {
            actives.add(newAction(CLUMSY_LEAP, unit));

        }
        if (UnitAnalyzer.checkOffhand(unit)) {
            actives.add(newAction(OFFHAND_ATTACK, unit));
            ActionGenerator.generateOffhandActions();
            addOffhandActions(actives, unit);
        }
        if (UnitAnalyzer.checkDualWielding(unit)) {
            actives.add(newAction(DUAL_ATTACK, unit));
            // addDualActions(actives, unit); good idea! :) dual thrust, dual
            // stunning blow, many possibilities! :) but it will be tricky...
            // TODO should add all dual actions
        }
        addStandardActionsForGroup(ACTION_TYPE.STANDARD_ATTACK, actives, unit);

        if (RuleMaster.checkFeature(FEATURE.USE_INVENTORY))
            if (unit.canUseItems()) {
                actives.add(newAction(USE_INVENTORY, unit));
            }

        if (RuleMaster.checkFeature(FEATURE.WATCH))
            actives.add(newAction(STD_SPEC_ACTIONS.Watch.name(), unit));

        if (RuleMaster.checkFeature(FEATURE.FLEE))
            if (FleeRule.isFleeAllowed()) {
                actives.add(newAction(FLEE, unit));
            }
        if (RuleMaster.checkFeature(FEATURE.PICK_UP))
            try {
                if (unit.getGame().getDroppedItemManager().checkHasItemsBeneath(unit)) {
                    actives.add(newAction(PICK_UP, unit));
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        if (RuleMaster.checkFeature(FEATURE.DIVINATION))
            if (unit.canDivine()) {
                actives.add(newAction(DIVINATION, unit));
            }
        if (RuleMaster.checkFeature(FEATURE.TOSS_ITEM))
            if (ListMaster.isNotEmpty(unit.getQuickItems())) {
                actives.add(newAction(TOSS_ITEM, unit));
            }

        if (RuleMaster.checkFeature(FEATURE.ENTER))
            for (Entrance e : DungeonLevelMaster.getAvailableDungeonEntrances(unit)) {
                actives.add(getEnterAction(unit, e));
            }
        // for (Entity e : LockMaster.getObjectsToUnlock(unit)) {
        // actives.add(getUnlockAction(unit, e));
        // }
        // for (Trap trap : TrapMaster.getTrapsToDisarm(unit)) {
        // actives.add(getDisarmAction(unit, trap));
        // }
    }

    private ActiveObj getDisarmAction(final DC_HeroObj hero, final Trap trap) {
        DC_UnitAction action = new DC_UnitAction(DataManager.getType(DISARM, OBJ_TYPES.ACTIONS),
                hero.getOwner(), game, hero.getRef().getCopy()) {
            public boolean resolve() {
                animate(); // pass std icon as param?
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

    private ActiveObj getUnlockAction(final DC_HeroObj hero, final Entity e) {
        DC_UnitAction action = new DC_UnitAction(DataManager.getType(UNLOCK, OBJ_TYPES.ACTIONS),
                hero.getOwner(), game, hero.getRef().getCopy()) {
            public boolean resolve() {
                animate(); // pass std icon as param?
                LockMaster.tryUnlock(e, hero);
                game.getManager().reset();
                game.getManager().refresh(ownerObj.getOwner().isMe());
                return true;
            }
        };
        actionsCache.get(hero).put(UNLOCK, action);
        return action;
    }

    private DC_UnitAction getEnterAction(final DC_HeroObj hero, final Entrance e) {
        DC_UnitAction action = new DC_UnitAction(DataManager.getType(ENTER, OBJ_TYPES.ACTIONS),
                hero.getOwner(), game, hero.getRef().getCopy()) {
            public boolean resolve() {
                animate();
                e.enter(hero, coordinates);
                game.getManager().reset();
                game.getManager().refresh(ownerObj.getOwner().isMe());
                return true;
            }
        };
        actionsCache.get(hero).put(ENTER, action);
        return action;
    }

    private boolean checkAddThrowAction(DC_HeroObj unit, boolean offhand) {
        DC_WeaponObj weapon = unit.getWeapon(offhand);
        if (weapon == null)
            return false;
        if (weapon.isShield())
            if (weapon.getWeaponGroup() == WEAPON_GROUP.BUCKLERS)
                if (unit.checkBool(STD_BOOLS.BUCKLER_THROWER))
                    return false;
        if (!weapon.isWeapon())
            return false;
        if (weapon.isRanged())
            return false;
        Integer bonus = unit.getIntParam(PARAMS.THROW_SIZE_BONUS);

        if (bonus > -2)
            if (weapon.getWeaponSize() == WEAPON_SIZE.TINY)
                return true;
        if (bonus > -1)
            if (weapon.getWeaponSize() == WEAPON_SIZE.SMALL)
                return true;
        if (bonus > 2)
            if (weapon.getWeaponSize() == WEAPON_SIZE.HUGE)
                return true;
        if (bonus > 1)
            if (weapon.getWeaponSize() == WEAPON_SIZE.LARGE)
                return true;
        if (bonus > 0)
            if (weapon.getWeaponSize() == WEAPON_SIZE.MEDIUM)
                return true;
        return false;
    }

    private void addOffhandActions(DequeImpl<ActiveObj> actives, DC_HeroObj unit) {

        for (ActiveObj attack : actives) {
            ObjType offhand = (DataManager.getType(ActionGenerator.getOffhandActionName(attack
                    .getName()), OBJ_TYPES.ACTIONS));
            if (offhand == null)
                continue;
            actives.add(getAction(offhand.getName(), unit));
        }

    }

    private void addHiddenActions(DC_HeroObj unit, Collection<ActiveObj> actives) {
        addStandardActionsForGroup(ACTION_TYPE.HIDDEN, actives, unit);
    }

    private void addStandardActions(DC_HeroObj unit, Collection<ActiveObj> actives) {
        // TODO also add to Actives container!
        for (ACTION_TYPE type : ACTION_TYPE.values()) {
            // I could actually centralize all action-adding to HERE! Dual, INV
            // and all the future ones
            if (type != ACTION_TYPE.HIDDEN)
                if (type != ACTION_TYPE.STANDARD_ATTACK)
                    addStandardActionsForGroup(type, actives, unit);
        }
        // checkDual(unit);
        // checkInv(unit);

        unit.setStandardActionsAdded(true);

    }

    public void constructActionMaps(DC_HeroObj unit) {
        for (ActiveObj active : unit.getActives()) {
            DC_UnitAction action = (DC_UnitAction) active;
            ACTION_TYPE type = action.getActionType();
            if (type == null) {
                type = action.getActionType();
            }
            List<DC_UnitAction> list = unit.getActionMap().get(type);
            if (list == null) {
                list = new LinkedList<>();
                unit.getActionMap().put(type, list);
            }
            if (!list.contains(action)) {
                if (!hiddenActions.contains(action.getType()))
                    list.add(action);
            }

        }
    }

    private void addStandardActionsForGroup(ACTION_TYPE type, Collection<ActiveObj> actives,
                                            DC_HeroObj unit) {
        if (stdActionTypes == null)
            init();
        OBJ_TYPES TYPE = OBJ_TYPES.getType(unit.getOBJ_TYPE());
        List<DC_UnitAction> actions = new LinkedList<DC_UnitAction>();
        switch (type) {
            case STANDARD_ATTACK:
                // TODO
                DC_ActiveObj action = (DC_ActiveObj) getAction(DC_ActionManager.ATTACK, unit);
                List<DC_UnitAction> subActions = getWeaponActions(unit.getActiveWeapon(false));
                action.setSubActions(new LinkedList<DC_ActiveObj>(subActions));
                actions.addAll(subActions);

                action = (DC_ActiveObj) getAction(DC_ActionManager.OFFHAND_ATTACK, unit);
                if (action != null) {
                    subActions = getWeaponActions(unit.getActiveWeapon(true));
                    action.setSubActions(new LinkedList<DC_ActiveObj>(subActions));
                    actions.addAll(subActions);
                }
                break;
            case HIDDEN:
                // TODO extract into separate?
                addActionTypes(hiddenActions, actions, TYPE, unit);
                List<DC_ActiveObj> generatedSubactions = generateStandardSubactionsForUnit(unit);
                actives.addAll(generatedSubactions);
                break;
            case MODE:
                addActionTypes(modeActionTypes, actions, TYPE, unit);
                break;
            case STANDARD:
                addActionTypes(stdActionTypes, actions, TYPE, unit);
                break;
        }
        unit.getActionMap().put(type, actions);
        actives.addAll(actions);

    }

    private void addActionTypes(List<ActionType> actionTypes, List<DC_UnitAction> list,
                                OBJ_TYPES TYPE, DC_HeroObj unit) {
        for (ActionType type : actionTypes) {
            // Ref ref = Ref.getCopy(unit.getRef());
            DC_UnitAction action = newAction(type.getName(), unit);
            // = newAction(type, ref, unit.getOwner(), game);
            list.add(action);
        }
    }

    public enum STD_MODE_ACTIONS {
        Defend, Concentrate, Rest, Meditate, On_Alert;

        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }
    }

    public enum HIDDEN_ACTIONS {
        Counter_Attack,
        Attack_of_Opportunity,
        Free_Counter_Attack,
        Free_Attack_of_Opportunity,
        Cower,
        Rage,
        Stumble;

        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }
    }

    public enum STD_SPEC_ACTIONS {
        Use_Inventory, OFFHAND_ATTACK, DUAL_ATTACK, Search_Mode, Watch
    }

    public enum STD_ACTIONS {
        Attack, Turn_Anticlockwise, Turn_Clockwise, Move, Wait,;

        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }
    }

    public enum ADDITIONAL_MOVE_ACTIONS {
        MOVE_LEFT, MOVE_RIGHT, MOVE_BACK, CLUMSY_LEAP;

        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }
    }

    public enum WEAPON_ATTACKS {
        Twohanded_Blade_Thrust,
        Twohanded_Sword_Swing,

        Sword_Swing,
        Blade_Thrust,
        Slash,
        Stab,

        Axe_Swing,
        Chop,
        Hack,
        Hook,
        Spike_Stab,

        Twohanded_Axe_Sweep,

        Heavy_Swing,
        Head_Smash,
        Slam,
        Chain_Thrust,

        Hilt_Smash,
        Shield_Push,

        Spear_Poke,
        Impale,

        Pole_Push,
        Pole_Smash,
        Pole_Thrust,
        Shield_Bash,
        Slice,
        Kick,
        Rip,
        Punch,
        Elbow_Smash,
        Fist_Swing,
        Nail_Swipe,
        Leg_Push,
        Arm_Push,
        Bite,
        Dig_Into,
        Tear,

        Tail_Smash,
        Tail_Sting,
        Pierce,

        Implode,
        Force_Push,
        Hoof_Slam,;

        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }

        ;
    }

}
