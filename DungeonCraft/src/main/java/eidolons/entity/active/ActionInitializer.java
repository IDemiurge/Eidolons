package eidolons.entity.active;

import eidolons.content.PROPS;
import eidolons.entity.active.spaces.FeatSpaces;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.RuleEnums;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.UnitAnalyzer;
import eidolons.game.battlecraft.rules.combat.attack.dual.DualAttackMaster;
import main.content.DC_TYPE;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.UnitEnums;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.Entity;
import main.entity.obj.ActiveObj;
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
        entity.setActivesReady(true);

        unit.setActives(new ArrayList<>(actives));

        FeatSpaces spaces = getSpaceManager().createFeatSpaces(unit, true);
        unit.setSpellSpaces(spaces);
        spaces = getSpaceManager().createFeatSpaces(unit, false);
        unit.setCombatSpaces(spaces);
    }

    private DequeImpl<ActiveObj> createStandardActions(Unit unit) {
        DequeImpl<ActiveObj> actives = new DequeImpl<>();
        // should be another passive to deny unit even those commodities...

        actives.add(getOrCreateAction(ActionEnums.DUMMY_ACTION, unit));
        if (unit.isBfObj()) {
            return;
        }

        actives.add(getOrCreateAction(ActionEnums.MOVE_LEFT, unit));
        actives.add(getOrCreateAction(ActionEnums.MOVE_RIGHT, unit));
        actives.add(getOrCreateAction(ActionEnums.MOVE_BACK, unit));

        if (!unit.isHuge() && !unit.checkPassive(UnitEnums.STANDARD_PASSIVES.CLUMSY)) {
            actives.add(getOrCreateAction(ActionEnums.CLUMSY_LEAP, unit));

        }
        actives.add(getOrCreateAction(ActionEnums.STD_SPEC_ACTIONS.Wait.name(), unit));


        //        if (RuleKeeper.checkFeature(RuleKeeper.FEATURE.PUSH))
        actives.add(getOrCreateAction(ActionEnums.STD_SPEC_ACTIONS.Push.name(), unit));
        actives.add(getOrCreateAction(ActionEnums.STD_SPEC_ACTIONS.Pull.name(), unit));

        actives.addAll(getStandardActionsForGroup(ActionEnums.ACTION_TYPE.STANDARD_ATTACK, unit));

        if (UnitAnalyzer.checkOffhand(unit)) {
            actives.add(getOrCreateAction(ActionEnums.OFFHAND_ATTACK, unit));
            addOffhandActions(unit.getActionMap().get(ActionEnums.ACTION_TYPE.STANDARD_ATTACK), unit);
            addOffhandActions(unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_ATTACK), unit);
        }

        if (RuleKeeper.checkFeature(RuleEnums.FEATURE.DUAL_ATTACKS))
            if (UnitAnalyzer.checkDualWielding(unit)) {
                try {
                    actives.addAll(DualAttackMaster.getDualAttacks(unit));
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }   // good idea! :) dual thrust, dual
                // stunning blow, many possibilities! :) but it will be tricky...
                // TODO should add all dual actions
            }

        if (RuleKeeper.checkFeature(RuleEnums.FEATURE.USE_INVENTORY)) {
            if (unit.canUseItems()) {
                actives.add(getOrCreateAction(ActionEnums.USE_INVENTORY, unit));
            }
        }

        if (RuleKeeper.checkFeature(RuleEnums.FEATURE.TOGGLE_WEAPON_SET)) {
            if (unit.getReserveOffhandWeapon() != null ||
                    unit.getReserveMainWeapon() != null) {
                actives.add(getOrCreateAction(ActionEnums.TOGGLE_WEAPON_SET, unit));
            }
        }
        if (RuleKeeper.checkFeature(RuleEnums.FEATURE.WATCH)) {
            actives.add(getOrCreateAction(ActionEnums.STD_SPEC_ACTIONS.Watch.name(), unit));
        }


        if (RuleKeeper.checkFeature(RuleEnums.FEATURE.FLEE)) {
            if (FleeRule.isFleeAllowed()) {
                actives.add(getOrCreateAction(ActionEnums.FLEE, unit));
            }
        }
        if (RuleKeeper.checkFeature(RuleEnums.FEATURE.PICK_UP)) {
            try {
                if (unit.getGame().getDroppedItemManager().checkHasItemsBeneath(unit)) {
                    actives.add(getOrCreateAction(ActionEnums.PICK_UP, unit));
                }
            } catch (Exception e) {
                // main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        if (RuleKeeper.checkFeature(RuleEnums.FEATURE.DIVINATION)) {
            if (unit.canDivine()) {
                actives.add(getOrCreateAction(ActionEnums.DIVINATION, unit));
            }
        }
        if (RuleKeeper.checkFeature(RuleEnums.FEATURE.TOSS_ITEM)) {
            if (ListMaster.isNotEmpty(unit.getQuickItems())) {
                actives.add(getOrCreateAction(ActionEnums.TOSS_ITEM, unit));
            }
        }


        //        actives.add(getOrCreateAction(SEARCH_MODE, unit));

        //  TODO condition?      if (unit.isHero())

        if (RuleKeeper.checkFeature(RuleEnums.FEATURE.GUARD_MODE))
            actives.add(getOrCreateAction(StringMaster.format(
                    ActionEnums.STD_SPEC_ACTIONS.Guard_Mode.name()), unit));

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

        if (RuleKeeper.checkFeature(RuleEnums.FEATURE.ORDERS))
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
