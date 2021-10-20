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
import main.system.datatypes.DequeImpl;

import java.util.*;

import static main.content.enums.entity.ActionEnums.ADDITIONAL_MOVE_ACTIONS.*;

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
            return actives;
        }

        actives.add(getOrCreateAction(MOVE_LEFT.toString(), unit));
        actives.add(getOrCreateAction(MOVE_RIGHT.toString(), unit));
        actives.add(getOrCreateAction(MOVE_BACK.toString(), unit));
        if (!unit.isHuge() && !unit.checkPassive(UnitEnums.STANDARD_PASSIVES.CLUMSY)) {
            actives.add(getOrCreateAction(ActionEnums.CLUMSY_LEAP, unit));

        }

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

    private void addHiddenActions(Unit unit, Collection<ActiveObj> actives) {
        // actions.addAll(getObjTypes(hiddenActions, unit));
        List<DC_ActiveObj> generatedSubactions = generateStandardSubactionsForUnit(unit);
        actives.addAll(generatedSubactions);
    }

}
