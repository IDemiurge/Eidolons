package eidolons.entity.active;

import eidolons.ability.ActionGenerator;
import eidolons.content.PARAMS;
import eidolons.content.PROPS;
import eidolons.entity.item.DC_QuickItemObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.RuleKeeper.FEATURE;
import eidolons.game.battlecraft.rules.UnitAnalyzer;
import eidolons.game.battlecraft.rules.combat.attack.dual.DualAttackMaster;
import eidolons.game.battlecraft.rules.combat.attack.extra_attack.ExtraAttacksRule;
import eidolons.game.battlecraft.rules.mechanics.FleeRule;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevelMaster;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.module.dungeoncrawl.objects.Trap;
import eidolons.game.module.dungeoncrawl.objects.TrapMaster;
import main.content.CONTENT_CONSTS2.STD_ACTION_MODES;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.ActionEnums.ACTION_TYPE;
import main.content.enums.entity.ActionEnums.ACTION_TYPE_GROUPS;
import main.content.enums.entity.ItemEnums;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Active;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.entity.type.ActionType;
import main.entity.type.ObjType;
import main.game.core.game.MicroGame;
import main.game.logic.battle.player.Player;
import main.game.logic.generic.ActionManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.threading.Weaver;

import java.util.*;

public class DC_ActionManager implements ActionManager {

    public static final String OFFHAND_ATTACK = StringMaster
     .getWellFormattedString(STD_SPEC_ACTIONS.OFFHAND_ATTACK.name());
    public static final String DUAL_ATTACK = StringMaster
     .getWellFormattedString(STD_SPEC_ACTIONS.DUAL_ATTACK.name());
    public static final G_PROPS ACTIVES = G_PROPS.ACTIVES;
    public static final String ATTACK = (STD_ACTIONS.Attack
     .name());
    public static final String OFFHAND = "Off Hand ";
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
    public static final String DUMMY_ACTION = "Dummy Action";
    public static final String USE_INVENTORY = StringMaster
     .getWellFormattedString(STD_SPEC_ACTIONS.Use_Inventory.toString());
    public static final String DIVINATION = "Divination";
    public static final String PICK_UP = "Pick Up Items";
    private static ArrayList<ObjType> stdActionTypes;
    private static ArrayList<ObjType> hiddenActions;
    private static ArrayList<ObjType> modeActionTypes;
    private static ArrayList<ObjType> orderActionTypes;
    private MicroGame game;
    private HashMap<Entity, Map<String, ActiveObj>> actionsCache = new HashMap<>();
    private boolean offhandInit;

    public DC_ActionManager(MicroGame game) {
        this.game = game;
    }

    public static void init() {
        stdActionTypes = new ArrayList<>();
        for (STD_ACTIONS name : STD_ACTIONS.values()) {
            if (!(DataManager.getType(StringMaster
             .getWellFormattedString(name.name()), DC_TYPE.ACTIONS) instanceof ActionType)) {
                continue;
            }
            ObjType type = DataManager.getType(StringMaster
             .getWellFormattedString(name.name()), DC_TYPE.ACTIONS);

            stdActionTypes.add(type);
        }

        modeActionTypes = new ArrayList<>();
        for (STD_MODE_ACTIONS name : STD_MODE_ACTIONS.values()) {
            ObjType type = DataManager.getType(StringMaster
             .getWellFormattedString(name.name()), DC_TYPE.ACTIONS);

            modeActionTypes.add(type);
        }

        orderActionTypes = new ArrayList<>();
        for (STD_ORDER_ACTIONS type : STD_ORDER_ACTIONS.values()) {
            ObjType actionType = DataManager.getType(StringMaster
             .getWellFormattedString(type.toString()), DC_TYPE.ACTIONS);
            orderActionTypes.add(actionType);
        }

        hiddenActions = new ArrayList<>();
        for (HIDDEN_ACTIONS name : HIDDEN_ACTIONS.values()) {
            ObjType type = DataManager.getType(StringMaster
             .getWellFormattedString(name.name()), DC_TYPE.ACTIONS);

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

    public static ACTION_TYPE_GROUPS getStdActionType(DC_ActiveObj activeObj) {
        for (STD_ACTIONS action : STD_ACTIONS.values()) {
            if (action.toString().equals(activeObj.getType().getName())) {
                switch (action) {
                    case Attack:
                        return ActionEnums.ACTION_TYPE_GROUPS.ATTACK;
                    case Move:
                        return ActionEnums.ACTION_TYPE_GROUPS.MOVE;
                    case Turn_Anticlockwise:
                        return ActionEnums.ACTION_TYPE_GROUPS.TURN;
                    case Turn_Clockwise:
                        return ActionEnums.ACTION_TYPE_GROUPS.TURN;

                    default:
                        break;

                }
            }
        }
        for (STD_ACTIONS action : STD_ACTIONS.values()) {
            if (activeObj.getType().getName().contains(action.toString())) {
                switch (action) {
                    case Attack:
                        return ActionEnums.ACTION_TYPE_GROUPS.ATTACK;
                    case Move:
                        return ActionEnums.ACTION_TYPE_GROUPS.MOVE;
                    case Turn_Anticlockwise:
                        return ActionEnums.ACTION_TYPE_GROUPS.TURN;
                    case Turn_Clockwise:
                        return ActionEnums.ACTION_TYPE_GROUPS.TURN;
                    default:
                        break;

                }
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
        return action.activatedOn(ref);
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
        Unit target = (Unit) action.getOwnerObj();
        Unit source = (Unit) _countering;
        DC_ActiveObj counter = (DC_ActiveObj) getCounterAttackAction(target, source,
         (DC_ActiveObj) action);
        if (counter == null) {
            return null;
        }
        counter.setCounterMode(true);
        boolean result = false;
        try {
            result = activateAction(target, source, counter);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            counter.setCounterMode(false);
        }
        if (!result) {
            return null;
        }
        // TODO ?
        return counter;
    }

    public Active getCounterAttackAction(Unit countered, Unit countering,
                                         DC_ActiveObj active) {
        DC_ActiveObj counterAttack = countering.getPreferredCounterAttack();
        if (counterAttack != null) {
            if (counterAttack.canBeActivatedAsCounter()) {
                if (counterAttack.canBeTargeted(active.getOwnerObj().getId())) {
                    return counterAttack;
                }
            }
        }
        for (DC_ActiveObj attack : ExtraAttacksRule.getCounterAttacks(active, countering)) {
//            if (AnimMaster.isTestMode())

            if (attack.canBeActivatedAsCounter()) {
                if (attack.canBeTargeted(active.getOwnerObj().getId())) {
                    return attack;
                }
            }
        }
        return null;
    }


    @Override
    public DC_UnitAction newAction(String typeName, Entity entity) {
        Map<String, ActiveObj> map = actionsCache.get(entity);
        if (map == null) {
            map = new HashMap<>();
            actionsCache.put(entity, map);
        }
        if (map.get(typeName) != null) {
            return (DC_UnitAction) map.get(typeName);
        }

        ActionType type = (ActionType) DataManager.getType(typeName, DC_TYPE.ACTIONS);
        if (type == null) {
            LogMaster.log(1, "no such active: " + typeName);
            return null;
        }
        Ref ref = Ref.getCopy(entity.getRef());

        return newAction(type, ref, entity.getOwner(), game);
    }

    @Override
    public DC_ActiveObj getAction(String typeName, Entity entity) {
        return getAction(typeName, entity, true);
    }

    public DC_UnitAction getOrCreateAction(String typeName, Entity entity) {
        return (DC_UnitAction) getAction(typeName, entity, false);
    }

    public DC_ActiveObj getAction(String typeName, Entity entity, boolean onlyIfAlreadyPresent) {


        if (actionsCache.get(entity) == null) {
            actionsCache.put(entity, new HashMap<>());
        }
        ActiveObj action = actionsCache.get(entity).get(typeName);
        if (action == null) {
            if (onlyIfAlreadyPresent) {
                if (!StringMaster.contains(entity.getProperty(G_PROPS.ACTIVES), typeName)) {
                    return null;
                }
            }

//            action =new SearchMaster<ActiveObj>().find(typeName, entity.getActives());
//            if (action != null)
//                return (DC_ActiveObj) action;

            try {
                action = newAction(typeName, entity);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            actionsCache.get(entity).put(typeName, action);
        }
        return (DC_ActiveObj) action;
    }

    public List<DC_ActiveObj> generateStandardSubactionsForUnit(Unit unit) {
        List<DC_ActiveObj> actions = new ArrayList<>();
        // actions.addAll(generateModesForUnit(unit,
        // ACTION_TYPE_GROUPS.ATTACK));
        actions.addAll(generateModesForUnit(unit, ActionEnums.ACTION_TYPE_GROUPS.MOVE));
        actions.addAll(generateModesForUnit(unit, ActionEnums.ACTION_TYPE_GROUPS.TURN));
        // actions.addAll(generateModesForUnit(unit, ACTION_TYPE_GROUPS.MODE));
        actions.addAll(getSpecialModesFromUnit(unit));
        return actions;
    }


    private List<DC_ActiveObj> getSpecialModesFromUnit(Unit unit) {
        List<DC_ActiveObj> subActions = new ArrayList<>();
        for (String mode : StringMaster.open(unit.getProperty(PROPS.SPECIAL_ACTION_MODES))) {
            DC_UnitAction baseAction = unit.getAction(VariableManager.getVar(mode));
            DC_UnitAction subAction;
            if (DataManager.getType(VariableManager.removeVarPart(mode), DC_TYPE.ACTIONS) != null) {
                subAction = generateModeAction(VariableManager.removeVarPart(mode), baseAction);
            }
            subAction = getOrCreateAction(VariableManager.removeVarPart(mode), unit);
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

    public List<DC_ActiveObj> generateModesForUnit(Unit unit, ACTION_TYPE_GROUPS group) {
        List<DC_ActiveObj> actions = new ArrayList<>();
        for (ActiveObj active : unit.getActives()) {
            DC_ActiveObj action = (DC_ActiveObj) active;
            if (action.getActionGroup() != group) {
                continue;
            }
            if (!StringMaster.isEmpty(action.getActionMode())) {
                continue;
            }
            if (action.isAttackGeneric()) {
                continue;
            }
            List<DC_ActiveObj> subActions = new ArrayList<>();

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

    private boolean checkModeForAction(DC_ActiveObj action, Unit unit, STD_ACTION_MODES mode) {
        if (hiddenActions.contains(action.getType())) {
            return false;
        }
        if (mode.getDefaultActionGroups().contains(action.getActionGroup())) {
            if (checkStdAction(action)) {
                return true;
            }
        }
        return action.getProperty(PROPS.ACTION_MODES).contains(
         StringMaster.getWellFormattedString(mode.toString()));

    }

    private boolean checkStdAction(DC_ActiveObj action) {
        for (STD_ACTIONS s : STD_ACTIONS.values()) {
            if (StringMaster.compare(s.toString(), action.getName())) {
                return true;
            }
        }
        for (STD_SPEC_ACTIONS s : STD_SPEC_ACTIONS.values()) {
            if (StringMaster.compare(s.toString(), action.getName())) {
                return true;
            }
        }
        for (ADDITIONAL_MOVE_ACTIONS s : ADDITIONAL_MOVE_ACTIONS.values()) {
            if (StringMaster.compare(s.toString(), action.getName())) {
                return true;
            }
        }
        for (STD_MODE_ACTIONS s : STD_MODE_ACTIONS.values()) {
            if (StringMaster.compare(s.toString(), action.getName())) {
                return true;
            }
        }
        return false;
    }

    private DC_UnitAction getModeAction(DC_ActiveObj action, Unit unit, STD_ACTION_MODES mode) {
        DC_UnitAction subAction = (DC_UnitAction) getAction(mode.getPrefix() + action.getName(),
         unit);
        if (subAction == null) {
            subAction = (generateModeAction(mode, action));
        }
        return subAction;
    }

    private DC_UnitAction generateModeAction(STD_ACTION_MODES mode, DC_ActiveObj baseAction) {
        ActionType type = new ActionType(baseAction.getType());
        if (mode.getAddPropMap() != null) {
            for (String s : mode.getAddPropMap().keySet()) {
                type.addProperty(s, mode.getAddPropMap().get(s));
            }
        }
        if (mode.getSetPropMap() != null) {
            for (String s : mode.getSetPropMap().keySet()) {
                type.setProperty(s, mode.getSetPropMap().get(s));
            }
        }
        if (mode.getParamBonusMap() != null) {
            for (String s : mode.getParamBonusMap().keySet()) {
                // type.setModifierKey(mode.getName());
                type.modifyParameter(s, mode.getParamBonusMap().get(s));
            }
        }
        if (mode.getParamModMap() != null) {
            for (String s : mode.getParamModMap().keySet()) {
                PARAMETER param = ContentValsManager.getPARAM(s);
                if (type.getIntParam(param) == 0) {
                    type.setParam(param, param.getDefaultValue());
                }
                type.modifyParamByPercent(param, StringMaster.getInteger(mode.getParamModMap().get(
                 s)), false);
            }
        }
        if (mode != STD_ACTION_MODES.STANDARD) {
            type.setName(mode.getPrefix() + baseAction.getName());
        }
        DC_UnitAction subAction = newAction(type, new Ref(baseAction.getOwnerObj()), baseAction
         .getOwner(), game);
        // TODO not all!..
        subAction.setActionType(ActionEnums.ACTION_TYPE.HIDDEN);
        subAction.setActionTypeGroup(ActionEnums.ACTION_TYPE_GROUPS.HIDDEN);
        return subAction;
    }

    public void resetActionsForGameMode(Unit unit, boolean exploreMode) {
        List<ActiveObj> actions = unit.getActives();
        /*
        in explore:
        + Camp

        - Defend

         substitude wait?

         */

    }

    public boolean isActionAvailable(ActiveObj activeObj, boolean exploreMode) {
        switch (activeObj.getName()) {
            case "Defend":
                return !exploreMode;
            case "Camp":
                return exploreMode;

        }
        return true;
    }

    @Override
    public void resetActions(Entity entity) {
        if (!(entity instanceof Unit)) {
            return;
        }
        Unit unit = (Unit) entity;
        DequeImpl<ActiveObj> actives;
        // #1: reset prop with ids if nothing is changed
        // if (ListMaster.isNotEmpty(actives) && entity.isActivesReady()) {
        // entity.setProperty(ACTIVES, StringMaster
        // .constructContainer(StringMaster.convertToIdList(actives)));
        // return;
        // }
        actives = new DequeImpl<>();
        // #2: reset the list if prop has been modified (via Add/Remove effects
        // ++ items). They should set ActivesReady to false for that.
        // or upon init

        unit.setActionMap(new HashMap<>());

        // if (!unit.isStandardActionsAdded())
        if (!unit.isBfObj()) {
            actives.addAll(getStandardActions(unit));
        }
        addSpecialActions(unit, actives);

        String activesProp = entity.getProperty(ACTIVES);
        for (String typeName : StringMaster.open(activesProp)) {
            ObjType type = DataManager.getType(typeName, DC_TYPE.ACTIONS);
            DC_UnitAction action;
            if (type == null) {
                try {
                    action = (DC_UnitAction) game.getObjectById(Integer.valueOf(typeName));
                } catch (Exception e) {
                    continue;
                }
            } else {
                action = getOrCreateAction(typeName, entity);
            }
            // idList.add(action.getId() + "");
            actives.add(action);
        }
        // list = new DequeImpl<>(items);
        actives.removeIf(activeObj -> !isActionAvailable(activeObj, ExplorationMaster.isExplorationOn()));


        if (ExplorationMaster.isExplorationOn())
            try {
                actives.removeIf(activeObj -> unit.getGame().getDungeonMaster().
                 getExplorationMaster().getActionHandler().
                 isActivationDisabledByExploration((DC_ActiveObj) activeObj));

                List<DC_ActiveObj> actions = unit.getGame().getDungeonMaster().
                 getExplorationMaster().getActionHandler().
                 getExplorationActions(unit);
                actives.addAll(actions);

            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }

        unit.setActives(new ArrayList<>(actives));
        if (!unit.isBfObj()) {
            addHiddenActions(unit, unit.getActives());
        }
        try {
            constructActionMaps(unit);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        // entity.setProperty(ACTIVES, StringMaster
        // .constructContainer(StringMaster.convertToIdList(actives)));
        entity.setActivesReady(true);
        for (ActiveObj a : actives) {
            if (activesProp.contains(a.getName())) {
                activesProp += a.getName() + ";";
            }
        }
        entity.setProperty(ACTIVES, activesProp);
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
        for (String typeName : StringMaster.open(weapon.getProperty(PROPS.WEAPON_ATTACKS))) {
            if (!weapon.isMainHand()) {
                typeName = ActionGenerator.getOffhandActionName(typeName);
            }
            DC_UnitAction action = getOrCreateAction(typeName, obj);

            // action.setActionTypeGroup(ACTION_TYPE_GROUPS.HIDDEN);
            // TODO so it's inside normal attack then?
            if (action == null) {
                continue;
            }
            list.add(action);
        }
        if (checkAddThrowAction(weapon.getOwnerObj(), weapon)) {
            DC_ActiveObj throwAction = getOrCreateAction(weapon.isMainHand() ? THROW_MAIN : THROW_OFFHAND,
             obj);
            throwAction.setName(getThrowName(weapon.getName()));
            list.add((DC_UnitAction) throwAction);
        }
        weapon.setAttackActions(new ArrayList<>(list));
        return list;
    }

    public static String getThrowName(String itemName) {
       return  "Throw " + itemName;
    }
    private void addSpecialActions(Unit unit, DequeImpl<ActiveObj> actives) {
        // should be another passive to deny unit even those commodities...

        actives.add(getOrCreateAction(DUMMY_ACTION, unit));
        if (unit.isBfObj()) {
            return;
        }

        actives.add(getOrCreateAction(MOVE_LEFT, unit));
        actives.add(getOrCreateAction(MOVE_RIGHT, unit));
        actives.add(getOrCreateAction(MOVE_BACK, unit));

        if (!unit.isHuge() && !unit.checkPassive(UnitEnums.STANDARD_PASSIVES.CLUMSY)) {
            actives.add(getOrCreateAction(CLUMSY_LEAP, unit));

        }
        actives.add(getOrCreateAction(STD_SPEC_ACTIONS.Wait.name(), unit));
        if (UnitAnalyzer.checkOffhand(unit)) {
            actives.add(getOrCreateAction(OFFHAND_ATTACK, unit));

            addOffhandActions(unit.getActionMap().get(ACTION_TYPE.STANDARD_ATTACK), unit);
            addOffhandActions(unit.getActionMap().get(ACTION_TYPE.SPECIAL_ATTACK), unit);
        }

        actives.addAll(getStandardActionsForGroup(ActionEnums.ACTION_TYPE.STANDARD_ATTACK, unit));
        if (RuleKeeper.checkFeature(FEATURE.DUAL_ATTACKS))
            if (UnitAnalyzer.checkDualWielding(unit)) {
                actives.addAll(DualAttackMaster.getDualAttacks(unit));
                // good idea! :) dual thrust, dual
                // stunning blow, many possibilities! :) but it will be tricky...
                // TODO should add all dual actions
            }

        if (RuleKeeper.checkFeature(FEATURE.USE_INVENTORY)) {
            if (unit.canUseItems()) {
                actives.add(getOrCreateAction(USE_INVENTORY, unit));
            }
        }

        if (RuleKeeper.checkFeature(FEATURE.WATCH)) {
            actives.add(getOrCreateAction(STD_SPEC_ACTIONS.Watch.name(), unit));
        }


        if (RuleKeeper.checkFeature(FEATURE.FLEE)) {
            if (FleeRule.isFleeAllowed()) {
                actives.add(getOrCreateAction(FLEE, unit));
            }
        }
        if (RuleKeeper.checkFeature(FEATURE.PICK_UP)) {
            try {
                if (unit.getGame().getDroppedItemManager().checkHasItemsBeneath(unit)) {
                    actives.add(getOrCreateAction(PICK_UP, unit));
                }
            } catch (Exception e) {
                // main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (RuleKeeper.checkFeature(FEATURE.DIVINATION)) {
            if (unit.canDivine()) {
                actives.add(getOrCreateAction(DIVINATION, unit));
            }
        }
        if (RuleKeeper.checkFeature(FEATURE.TOSS_ITEM)) {
            if (ListMaster.isNotEmpty(unit.getQuickItems())) {
                actives.add(getOrCreateAction(TOSS_ITEM, unit));
            }
        }

        if (RuleKeeper.checkFeature(FEATURE.ENTER)) {
            for (Entrance e : DungeonLevelMaster.getAvailableDungeonEntrances(unit)) {
                actives.add(getEnterAction(unit, e));
            }
        }

        actives.add(getOrCreateAction(SEARCH_MODE, unit));
//  TODO condition?      if (unit.isHero())

        if (RuleKeeper.checkFeature(FEATURE.GUARD_MODE))
            actives.add(getOrCreateAction(StringMaster.getWellFormattedString(
             STD_SPEC_ACTIONS.Guard_Mode.name()), unit));

        // for (Entity e : LockMaster.getObjectsToUnlock(unit)) {
        // actives.add(getUnlockAction(unit, e));
        // }
        // for (Trap trap : TrapMaster.getTrapsToDisarm(unit)) {
        // actives.add(getDisarmAction(unit, trap));
        // }
    }


    private ActiveObj getDisarmAction(final Unit hero, final Trap trap) {
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

    private ActiveObj getUnlockAction(final Unit hero, final Entity e) {
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

    private DC_UnitAction getEnterAction(final Unit hero, final Entrance e) {
        DC_UnitAction action = new DC_UnitAction(DataManager.getType(ENTER, DC_TYPE.ACTIONS),
         hero.getOwner(), game, hero.getRef().getCopy()) {
            public boolean resolve() {
//                animate();
                e.enter(hero, coordinates);
                game.getManager().reset();
                game.getManager().refresh(ownerObj.getOwner().isMe());
                return true;
            }
        };
        actionsCache.get(hero).put(ENTER, action);
        return action;
    }

    private boolean checkAddThrowAction(Unit unit, DC_WeaponObj weapon) {

        if (weapon == null) {
            return false;
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
            if (weapon.getWeaponSize() == ItemEnums.WEAPON_SIZE.MEDIUM) {
                return true;
            }
        }
        return false;
    }

    private void addOffhandActions(DequeImpl<DC_UnitAction> actives, Unit unit) {
        if (!offhandInit) {
            ActionGenerator.generateOffhandActions();
            offhandInit = true;
        }
        if (actives != null)
            for (ActiveObj attack : actives) {
                ObjType offhand = (DataManager.getType(ActionGenerator.getOffhandActionName(attack
                 .getName()), DC_TYPE.ACTIONS));
                if (offhand == null) {
                    continue;
                }
                actives.add(getOrCreateAction(offhand.getName(), unit));
            }

    }

    private void addHiddenActions(Unit unit, Collection<ActiveObj> actives) {
        actives.addAll(getStandardActionsForGroup(ActionEnums.ACTION_TYPE.HIDDEN, unit));
    }

    private Collection<ActiveObj> getStandardActions(Unit unit) {
        Collection<ActiveObj> actives = new ArrayList<>();
        // TODO also add to Actives container!
        for (ACTION_TYPE type : ActionEnums.ACTION_TYPE.values()) {
            // I could actually centralize all action-adding to HERE! Dual, INV
            // and all the future ones
            if (type != ActionEnums.ACTION_TYPE.HIDDEN) {
                if (type != ActionEnums.ACTION_TYPE.STANDARD_ATTACK) {
                    actives.addAll(getStandardActionsForGroup(type, unit));
                }
            }
        }

        if (RuleKeeper.checkFeature(FEATURE.ORDERS))
            actives.addAll(getOrderActions(unit));
        // checkDual(unit);
        // checkInv(unit);

        return actives;
    }

    private Collection<? extends ActiveObj> getOrderActions(Unit unit) {
        return getActionTypes(orderActionTypes, unit);
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

    private List<DC_UnitAction> getAndInitAttacks(boolean offhand, Unit unit) {
        DC_ActiveObj action = unit.getAttackAction(offhand);
        if (action == null) {
            return null;
        }
        List<DC_UnitAction> subActions = getOrCreateWeaponActions(unit.getWeapon(offhand));
        subActions.addAll(getOrCreateWeaponActions(unit.getNaturalWeapon(offhand)));
        action.setSubActions(new ArrayList<>(subActions));
//      ???  if (action.getSubActions().isEmpty()) {
//            action.setSubActions(new ArrayList<>(subActions));
//        }
        return subActions;
    }

    private DequeImpl<DC_UnitAction> getStandardActionsForGroup(ACTION_TYPE type,

                                                                Unit unit) {
        if (stdActionTypes == null) {
            init();
        }
        DequeImpl<DC_UnitAction> actions = new DequeImpl<>();
        DequeImpl<DC_UnitAction> actives = new DequeImpl<>();

        switch (type) {
            case STANDARD_ATTACK:
                // TODO
                actions.addAll(getAndInitAttacks(false, unit));
                actions.addAll(getAndInitAttacks(true, unit));
                break;
            case HIDDEN:
                // TODO extract into separate?
                actions.addAll(getActionTypes(hiddenActions, unit));
                List<DC_ActiveObj> generatedSubactions = generateStandardSubactionsForUnit(unit);
                actives.addAllCast(generatedSubactions);
                break;
            case MODE:
                actions.addAll(getActionTypes(modeActionTypes, unit));
                break;
            case STANDARD:
                actions.addAll(getActionTypes(stdActionTypes, unit));
                break;
        }
        unit.getActionMap().put(type, actions);
        actives.addAll(actions);
        return (actives);

    }

    private List<DC_UnitAction> getActionTypes(List<? extends ObjType> actionTypes,
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


    public enum ADDITIONAL_MOVE_ACTIONS {
        MOVE_LEFT, MOVE_RIGHT, MOVE_BACK, CLUMSY_LEAP;

        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }
    }

    public enum HIDDEN_ACTIONS {
        Cower,
        Rage,
        Idle,
        Stumble;

        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }
    }

    public enum STD_ACTIONS {
        Attack, Turn_Anticlockwise, Turn_Clockwise, Move;

        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }
    }

    public enum STD_MODE_ACTIONS {
        Defend, Camp, Concentrate, Rest, Meditate, On_Alert;

        public String toString() {
            return StringMaster.getWellFormattedString(name());
        }
    }

    public enum STD_ORDER_ACTIONS {
        Press_the_Attack,
        Hold_Fast,
        Protect_me,
        Heal_me,
        Kill_Him,
        Retreat,
        Cancel_Order {
            @Override
            public String toString() {
                return StringMaster.getWellFormattedString(name());
            }
        },;

        public String toString() {
            return "Order: " + StringMaster.getWellFormattedString(name() +
             "!");
        }

    }

    public enum STD_SPEC_ACTIONS {
        Use_Inventory, OFFHAND_ATTACK, DUAL_ATTACK, Search_Mode, Guard_Mode, Watch, Wait
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

    }

}
