package tests.logic.combat.buff_rules;

import TestUtils.JUnitUtils;
import eidolons.content.PARAMS;
import eidolons.game.battlecraft.rules.buff.*;
import main.entity.Ref;
import main.system.math.Formula;
import org.junit.Test;
import tests.entity.JUnitPartyCreated;

/**
 * Created by JustMe on 3/26/2017.
 * <portrait>
 * DOES NOT TEST ADDITIONAL EFFECTS (PANIC, TREASON, CONFUSION ETC)
 *
 * //TODO but what exactly does it test?
 * // which buff rules?
 */
public class BuffRulesTest extends JUnitPartyCreated {

    PARAMS[] root_params = {
     PARAMS.C_FOCUS,
     PARAMS.C_STAMINA,
     PARAMS.C_MORALE,
     PARAMS.C_ENDURANCE,
     PARAMS.C_CARRYING_WEIGHT
    };
    String[] value_low = {
     FocusBuffRule.formulas[1] + " - 5",
     StaminaBuffRule.formulas[1] + " - 5",
     MoraleBuffRule.formulas[1] + " - 5",
     WoundsBuffRule.formulas[1] + " - 1",
     null,
    };
    String[] value_high = {
     FocusBuffRule.formulas[2] + " + 5",
     StaminaBuffRule.formulas[2] + " + 5",
     MoraleBuffRule.formulas[2] + " + 5",
     null,
     WeightBuffRule.formulas[0] + " + 5",
    };
    int[] value_special_effect = {

    };
    PARAMS[] increased_params = {
     PARAMS.DEFENSE,
     PARAMS.DAMAGE,
     PARAMS.RESISTANCE,
     null,
     PARAMS.MOVE_AP_PENALTY

    };
    PARAMS[] reduced_params = {
     PARAMS.DEFENSE,
     PARAMS.DAMAGE,
     MoraleBuffRule.PARAMETERS[0],
     PARAMS.N_OF_ACTIONS,
     null,
    };



    @Test
    public void buffRuleTest() {
        int i = 0; //root_params
        for (PARAMS root_param : root_params) {
//also test rule's state?

            Integer initial = unit.getIntParam(reduced_params[i]);
            unit.setParam(root_param,
             new Formula(value_low[i]).getInt(new Ref(unit)));
           atbHelper.startCombat();
           helper.resetAll();
            Integer reduced = unit.getIntParam(reduced_params[i]);

            if (isReductionOn(root_param)) {
                JUnitUtils.assertGreaterThanAndLog(initial, reduced,
                 root_param + " rule's Reduction effect on " + reduced_params[i]);
            }

            if (!isIncreaseOn(root_param)) {
                i++;
                continue;
            }
            if (reduced_params[i] != increased_params[i]) {
                initial = unit.getIntParam(increased_params[i]);
            }
            unit.setParam(root_param, new Formula(value_high[i]).getInt(new Ref(unit))
            );
            atbHelper.startCombat();
            helper.resetAll();
            Integer increased = unit.getIntParam(increased_params[i]);
            JUnitUtils.assertGreaterThanAndLog(increased, initial, root_param
             + " rule's Increase effect on " + increased_params[i]);
            i++;
        }


    }

    //buff name?
    private boolean isSpecialEffectOn(PARAMS p) {
        switch (p) {
            case ENDURANCE:
                return false;
        }
        return true;
    }

    private boolean isIncreaseOn(PARAMS p) {
        switch (p) {
            case ENDURANCE:
                return false;
        }
        return true;
    }

    private boolean isReductionOn(PARAMS p) {
        switch (p) {
            case C_CARRYING_WEIGHT:
                return false;
        }
        return true;
    }
}
