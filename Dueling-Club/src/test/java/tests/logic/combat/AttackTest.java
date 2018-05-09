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

    protected int difT;
    protected int difE;

    @Override
    public void test() {
        super.test();
        testAttack();

    }

    protected void testAttack() {

        int origToughness = unit2.getIntParam(PARAMS.C_TOUGHNESS);
        int origEndurance = unit2.getIntParam(PARAMS.C_ENDURANCE);

        assertTrue(unit.getNaturalWeapon() != null);

        helper.turn(unit, FACING_DIRECTION.WEST);

        atbHelper.startCombat();
        helper.resetAll();
        DC_ActiveObj action =getAction();;
        assertTrue(action != null);
        boolean result = action.canBeTargeted(unit2.getId());
        Condition condition = action.getTargeting().getConditions().getLastCheckedCondition();
        if (!result) {
            fail(unit.getFacing()+" facing, failed condition: " + condition);
        }

        helper.defaultAttack(unit, unit2);

        Integer newToughness = unit2.getIntParam(PARAMS.C_TOUGHNESS);
        Integer newEndurance = unit2.getIntParam(PARAMS.C_ENDURANCE);

          difT = newToughness - origToughness;
          difE = newEndurance - origEndurance;
        assertTrue(newToughness < origToughness);
        assertTrue(newEndurance < origEndurance);
    }

    protected DC_ActiveObj getAction() {
        return unit.getAction("Punch");
    }

    @Override
    protected boolean isLoggingOff() {
        return false;
    }
}
