package eidolons.entity.active;

import eidolons.ability.ActionGenerator;
import eidolons.content.PROPS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.UnitAnalyzer;
import eidolons.game.battlecraft.rules.combat.attack.dual.DualAttackMaster;
import eidolons.game.battlecraft.rules.mechanics.FleeRule;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.netherflame.igg.death.ShadowMaster;
import main.content.DC_TYPE;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.UnitEnums;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.Entity;
import main.entity.obj.ActiveObj;
import main.entity.type.ObjType;
import main.game.core.game.GenericGame;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ActionInitializer extends DC_ActionManager {
    public ActionInitializer(GenericGame game) {
        super(game);
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
        if (unit.isBoss()) {

        } else {
            addSpecialActions(unit, actives);
            addCustomActions(unit, actives);
            try {
                constructActionMaps(unit);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        // entity.setProperty(ACTIVES, StringMaster
        // .constructContainer(StringMaster.convertToIdList(actives)));
        entity.setActivesReady(true);

        unit.setActives(new ArrayList<>(actives));
    }

    protected List<DC_ActiveObj> getSpecialModesFromUnit(Unit unit) {
        List<DC_ActiveObj> subActions = new ArrayList<>();
        for (String mode : ContainerUtils.open(unit.getProperty(PROPS.SPECIAL_ACTION_MODES))) {
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

    public static boolean isActionNotBlocked(DC_ActiveObj activeObj, boolean exploreMode) {
        if (activeObj.getOwnerUnit() == Eidolons.getMainHero())
            if (EidolonsGame.DUEL) {
                if (EidolonsGame.TURNS_DISABLED)
                    if (activeObj.isTurn())
                        return false;
                if (activeObj.isMove())
                    if (EidolonsGame.MOVES_DISABLED) {
                        return false;
                    }
                if (activeObj.isAttackAny())
                    return !EidolonsGame.ATTACKS_DISABLED;

                if (activeObj.isMode()) {
                    return EidolonsGame.getActionSwitch(activeObj.getName());
                }
                if (!activeObj.isSpell()) {
                    return EidolonsGame.getActionSwitch(activeObj.getName());
                }
//                switch (activeObj.getName()) {
//                    case "Wait":
//                        return EidolonsGame.getActionSwitch(activeObj.getName());
//                }

            }
        return true;
    }

    public boolean isActionAvailable(DC_ActiveObj activeObj, boolean exploreMode) {

        switch (activeObj.getName()) {
            case "Defend":
                return !exploreMode;
            case "Camp":
            case "Dissolve":
                return exploreMode;

        }
        return true;
    }

    private void addCustomActions(Unit unit, DequeImpl<ActiveObj> actives) {

        String activesProp = unit.getProperty(ACTIVES);
        for (String typeName : ContainerUtils.open(activesProp)) {
            ObjType type = DataManager.getType(typeName, DC_TYPE.ACTIONS);
            DC_UnitAction action;
            if (type == null) {
                try {
                    action = (DC_UnitAction) game.getObjectById(Integer.valueOf(typeName));
                } catch (Exception e) {
                    continue;
                }
            } else {
                action = getOrCreateAction(typeName, unit);
            }
            // idList.add(action.getId() + "");
            actives.add(action);
        }
        // list = new DequeImpl<>(items);

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

        if (!unit.isBfObj()) {
            addHiddenActions(unit, actives);
        }

        actives.removeIf(activeObj -> !isActionAvailable((DC_ActiveObj) activeObj, ExplorationMaster.isExplorationOn()));

        StringBuilder activesPropBuilder = new StringBuilder(activesProp);
        for (ActiveObj a : actives) {
            if (activesPropBuilder.toString().contains(a.getName())) {
                activesPropBuilder.append(a.getName()).append(";");
            }
        }
        activesProp = activesPropBuilder.toString();
        unit.setProperty(ACTIVES, activesProp);
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


//        if (RuleKeeper.checkFeature(RuleKeeper.FEATURE.PUSH))
        actives.add(getOrCreateAction(STD_SPEC_ACTIONS.Push.name(), unit));
        actives.add(getOrCreateAction(STD_SPEC_ACTIONS.Pull.name(), unit));

        actives.addAll(getStandardActionsForGroup(ActionEnums.ACTION_TYPE.STANDARD_ATTACK, unit));

        if (UnitAnalyzer.checkOffhand(unit)) {
            actives.add(getOrCreateAction(OFFHAND_ATTACK, unit));
            addOffhandActions(unit.getActionMap().get(ActionEnums.ACTION_TYPE.STANDARD_ATTACK), unit);
            addOffhandActions(unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_ATTACK), unit);
        }

        if (RuleKeeper.checkFeature(RuleKeeper.FEATURE.DUAL_ATTACKS))
            if (UnitAnalyzer.checkDualWielding(unit)) {
                actives.addAll(DualAttackMaster.getDualAttacks(unit));
                // good idea! :) dual thrust, dual
                // stunning blow, many possibilities! :) but it will be tricky...
                // TODO should add all dual actions
            }

        if (RuleKeeper.checkFeature(RuleKeeper.FEATURE.USE_INVENTORY)) {
            if (unit.canUseItems()) {
                actives.add(getOrCreateAction(USE_INVENTORY, unit));
            }
        }

        if (RuleKeeper.checkFeature(RuleKeeper.FEATURE.TOGGLE_WEAPON_SET)) {
            if (unit.getReserveOffhandWeapon() != null ||
                    unit.getReserveMainWeapon() != null) {
                actives.add(getOrCreateAction(TOGGLE_WEAPON_SET, unit));
            }
        }
        if (RuleKeeper.checkFeature(RuleKeeper.FEATURE.WATCH)) {
            actives.add(getOrCreateAction(STD_SPEC_ACTIONS.Watch.name(), unit));
        }


        if (RuleKeeper.checkFeature(RuleKeeper.FEATURE.FLEE)) {
            if (FleeRule.isFleeAllowed()) {
                actives.add(getOrCreateAction(FLEE, unit));
            }
        }
        if (RuleKeeper.checkFeature(RuleKeeper.FEATURE.PICK_UP)) {
            try {
                if (unit.getGame().getDroppedItemManager().checkHasItemsBeneath(unit)) {
                    actives.add(getOrCreateAction(PICK_UP, unit));
                }
            } catch (Exception e) {
                // main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (RuleKeeper.checkFeature(RuleKeeper.FEATURE.DIVINATION)) {
            if (unit.canDivine()) {
                actives.add(getOrCreateAction(DIVINATION, unit));
            }
        }
        if (RuleKeeper.checkFeature(RuleKeeper.FEATURE.TOSS_ITEM)) {
            if (ListMaster.isNotEmpty(unit.getQuickItems())) {
                actives.add(getOrCreateAction(TOSS_ITEM, unit));
            }
        }



//        actives.add(getOrCreateAction(SEARCH_MODE, unit));

//  TODO condition?      if (unit.isHero())

        if (RuleKeeper.checkFeature(RuleKeeper.FEATURE.GUARD_MODE))
            actives.add(getOrCreateAction(StringMaster.getWellFormattedString(
                    STD_SPEC_ACTIONS.Guard_Mode.name()), unit));

        // for (Entity e : LockMaster.getObjectsToUnlock(unit)) {
        // actives.add(getUnlockAction(unit, e));
        // }
        // for (Trap trap : TrapMaster.getTrapsToDisarm(unit)) {
        // actives.add(getDisarmAction(unit, trap));
        // }
    }

    private void addOffhandActions(DequeImpl<DC_UnitAction> actives, Unit unit) {
        if (actives != null) {
            ArrayList<ActiveObj> list = new ArrayList<>(actives);
            for (ActiveObj attack : list) {

                ObjType offhand = ActionGenerator.generateOffhandAction(attack.getType());

                if (offhand == null) {
                    continue;
                }
                actives.add(getOrCreateAction(offhand.getName(), unit));
            }
        }

    }

    private void addHiddenActions(Unit unit, Collection<ActiveObj> actives) {
        actives.addAll(getStandardActionsForGroup(ActionEnums.ACTION_TYPE.HIDDEN, unit));
    }

    private Collection<ActiveObj> getStandardActions(Unit unit) {
        Collection<ActiveObj> actives = new ArrayList<>();
        // TODO also add to Actives container!

        if (unit.isBoss()) {
//            actives.addAll(BossMaster.getActionMaster(unit).getStandardActions(unit));
        } else
            for (ActionEnums.ACTION_TYPE type : ActionEnums.ACTION_TYPE.values()) {
                // I could actually centralize all action-adding to HERE! Dual, INV
                // and all the future ones
                if (type != ActionEnums.ACTION_TYPE.HIDDEN) {
                    if (type != ActionEnums.ACTION_TYPE.STANDARD_ATTACK) {
                        actives.addAll(getStandardActionsForGroup(type, unit));
                    }
                }
            }

        if (RuleKeeper.checkFeature(RuleKeeper.FEATURE.ORDERS))
            actives.addAll(getOrderActions(unit));
        // checkDual(unit);
        // checkInv(unit);

        return actives;
    }

    private Collection<? extends ActiveObj> getOrderActions(Unit unit) {
        return getObjTypes(orderObjTypes, unit);
    }

    private DequeImpl<DC_UnitAction> getStandardActionsForGroup(ActionEnums.ACTION_TYPE type,

                                                                Unit unit) {
        if (stdObjTypes == null) {
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
                actions.addAll(getObjTypes(hiddenActions, unit));
                List<DC_ActiveObj> generatedSubactions = generateStandardSubactionsForUnit(unit);
                actives.addAllCast(generatedSubactions);
                break;
            case MODE:
                if (unit == ShadowMaster.getShadowUnit()) {
                    actions.add(unit.getAction("Shadow Step"));
                    break;
                }
                actions.addAll(getObjTypes(modeObjTypes, unit));
                break;
            case STANDARD:
                actions.addAll(getObjTypes(stdObjTypes, unit));
                break;
        }
        unit.getActionMap().put(type, actions);
        actives.addAll(actions);
        return (actives);

    }
}
