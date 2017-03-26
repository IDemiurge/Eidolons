package tests.logic.combat.buff_rules;

import main.content.PARAMS;
import main.rules.buff.DC_BuffRule;
import main.rules.buff.FocusBuffRule;
import org.junit.Before;
import org.junit.Test;
import tests.entity.CreateUnitTest;
import static org.junit.Assert.assertTrue;
/**
 * Created by JustMe on 3/26/2017.
 *
 * DOES NOT TEST ADDITIONAL EFFECTS (PANIC, TREASON, CONFUSION ETC)
 */
public class BuffRulesTest extends CreateUnitTest {

    PARAMS[] root_params = {
     PARAMS.C_FOCUS,
     PARAMS.C_STAMINA,
     PARAMS.C_MORALE,
     PARAMS.C_ENDURANCE,
     PARAMS.C_CARRYING_WEIGHT
    };
    int[] value_low = {
     Integer.valueOf(FocusBuffRule.formulas[1]) - 5,
    };
    int[] value_high = {

    };
    int[] value_special_effect = {

    };
    PARAMS[] increased_params = {

    };
    PARAMS[] reduced_params = {

    };


    @Before
    public void createEntity() {
        super.createEntity();
    }

    @Test
    public void buffRuleTest() {
        int i = 0;
        for (DC_BuffRule rule : judi.game.getRules().getBuffRules()) {
//also test rule's state?

            Integer initial = entity.getIntParam(reduced_params[i]);
            entity.setParam(root_params[i], value_low[i]);
            entity.reset();
            Integer reduced = entity.getIntParam(reduced_params[i]);

            if (isReductionOn(root_params[i]))
                assertTrue(reduced < initial);

            if (!isIncreaseOn(root_params[i]))
                continue;

            initial = entity.getIntParam(increased_params[i]);
            entity.setParam(root_params[i], value_high[i]);
            entity.reset();
            Integer increased = entity.getIntParam(increased_params[i]);
            assertTrue(increased > initial);
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
