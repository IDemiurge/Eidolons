package tests.logic.combat;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import main.elements.conditions.Condition;
import main.game.bf.Coordinates.FACING_DIRECTION;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Nyx on 3/16/2017.
 */
public class AttackTest extends CombatTest {

    @Override
    public void test() {
        super.test();

            int origToughness = unit2.getIntParam(PARAMS.C_TOUGHNESS);
            int origEndurance = unit2.getIntParam(PARAMS.C_ENDURANCE);

            assertTrue(unit.getNaturalWeapon() != null);

            helper.turn(unit, FACING_DIRECTION.WEST);

            atbHelper.startCombat();
            DC_ActiveObj action = unit.getAction("Punch");
            assertTrue(action != null);
            boolean result = action.canBeTargeted(unit2.getId());
            Condition condition = action.getTargeting().getConditions().getLastCheckedCondition();
            if (!result) {
                fail(unit.getFacing()+" facing, failed condition: " + condition
                );
            }

            helper.defaultAttack(unit, unit2);

            Integer newToughness = unit2.getIntParam(PARAMS.C_TOUGHNESS);
            Integer newEndurance = unit2.getIntParam(PARAMS.C_ENDURANCE);
            assertTrue(newToughness < origToughness);
            assertTrue(newEndurance < origEndurance);

    }

    @Override
    protected boolean isLoggingOff() {
        return false;
    }
}
