package tests.logic.combat.buff_rules;

import TestUtils.printingAsserts;
import eidolons.content.PARAMS;
import eidolons.game.battlecraft.rules.buff.*;
import main.entity.Ref;
import main.system.math.Formula;
import org.junit.Before;
import org.junit.Test;
import tests.entity.CreateUnitTest;

/**
 * Created by JustMe on 3/26/2017.
 * <portrait>
 * DOES NOT TEST ADDITIONAL EFFECTS (PANIC, TREASON, CONFUSION ETC)
 *
 * //TODO but what exactly does it test?
 * // which buff rules?
 */
public class BuffRulesTest extends CreateUnitTest {

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
     PARAMS.RESISTANCE,
     PARAMS.N_OF_ACTIONS,
     null,
    };


    @Before
    public void createEntity() {
        super.createEntity();
    }

    @Test
    public void buffRuleTest() {
        int i = 0; //root_params
        for (PARAMS root_param : root_params) {
//also test rule's state?

            Integer initial = entity.getIntParam(reduced_params[i]);
            entity.setParam(root_param,
             new Formula(value_low[i]).getInt(new Ref(entity)));
            game.getStateManager().reset(entity);
            Integer reduced = entity.getIntParam(reduced_params[i]);

            if (isReductionOn(root_param)) {
                printingAsserts.assertGreaterThanAndLog(initial, reduced, root_param + " rule");
            }

            if (!isIncreaseOn(root_param)) {
                i++;
                continue;
            }
            if (reduced_params[i] != increased_params[i]) {
                initial = entity.getIntParam(increased_params[i]);
            }
            entity.setParam(root_param, new Formula(value_high[i]).getInt(new Ref(entity))
            );
            game.getStateManager().reset(entity);
            Integer increased = entity.getIntParam(increased_params[i]);
            printingAsserts.assertGreaterThanAndLog(increased, initial, root_param + " rule");
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
