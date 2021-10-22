package eidolons.entity.active;

import eidolons.entity.active.spaces.FeatSpaces;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.RuleEnums;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.UnitAnalyzer;
import eidolons.game.battlecraft.rules.combat.attack.dual.DualAttackMaster;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.UnitEnums;
import main.entity.Entity;
import main.entity.obj.ActiveObj;
import main.game.core.game.GenericGame;
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
        actives = createStandardActions(unit);

        // addCustomActions(unit, actives); deprecated!
        constructActionMaps(unit);
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
        Arrays.stream(ActionEnums.DEFAULT_ACTION.values()).forEach(
                action -> actives.add(getOrCreateAction(action.name(), unit))
        );

        //TODO ATKS
        // actives.addAll(getAndInitAttacks(false, unit));
        if (UnitAnalyzer.checkOffhand(unit)) {
            // actives.addAll(getAndInitAttacks(true, unit));

            actives.add(getOrCreateAction(ActionEnums.STD_ACTIONS.Offhand_Attack.toString(), unit));
            // addOffhandActions(unit.getActionMap().get(ActionEnums.ACTION_TYPE.STANDARD_ATTACK), unit);
            // addOffhandActions(unit.getActionMap().get(ActionEnums.ACTION_TYPE.SPECIAL_ATTACK), unit);
        }

        actives.add(getOrCreateAction(MOVE_LEFT.toString(), unit));
        actives.add(getOrCreateAction(MOVE_RIGHT.toString(), unit));
        actives.add(getOrCreateAction(MOVE_BACK.toString(), unit));
        if (!unit.isHuge() && !unit.checkPassive(UnitEnums.STANDARD_PASSIVES.CLUMSY)) {
            actives.add(getOrCreateAction(CLUMSY_LEAP.toString(), unit));
        }

        if (RuleKeeper.checkFeature(RuleEnums.FEATURE.DUAL_ATTACKS))
            if (UnitAnalyzer.checkDualWielding(unit)) {
                actives.addAll(DualAttackMaster.getDualAttacks(unit));
            }

        // if (unit.getReserveOffhandWeapon() != null) {
        //     actives.add(getOrCreateAction(ActionEnums.TOGGLE_WEAPON_SET, unit));
        // }
        // if (unit.getReserveMainWeapon() != null) {
        //     actives.add(getOrCreateAction(ActionEnums.TOGGLE_WEAPON_SET, unit));
        // }

        return actives;
    }

}
