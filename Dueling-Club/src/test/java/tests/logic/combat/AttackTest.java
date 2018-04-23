package tests.logic.combat;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import main.elements.conditions.Condition;
import main.game.bf.Coordinates.FACING_DIRECTION;
import org.junit.Test;
import res.JUnitResources;
import tests.JUnitDcTest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Nyx on 3/16/2017.
 */
public class AttackTest extends JUnitDcTest {


    private Unit source;
    private Unit target;

    @Override
    protected String getPlayerParty() {
        return "";
    }

    @Test
    public void attackTest() {
        for (int i = 0; i < 121; i++) {

            source = helper.unit(JUnitResources.DEFAULT_UNIT, 1, 0, true);
            target = helper.unit(JUnitResources.DEFAULT_UNIT, 0, 0, false);

            int origToughness = target.getIntParam(PARAMS.C_TOUGHNESS);
            int origEndurance = target.getIntParam(PARAMS.C_ENDURANCE);

            assertTrue(source.getNaturalWeapon() != null);

            helper.turn(source, FACING_DIRECTION.WEST);

            atbHelper.startCombat();
            atbHelper.waitForGameLoopStart();

            DC_ActiveObj action = source.getAction("Punch");
            assertTrue(action != null);
            boolean result = action.canBeTargeted(target.getId());
            Condition condition = action.getTargeting().getConditions().getLastCheckedCondition();
            if (!result) {
                fail(source.getFacing()+" facing, failed condition: " + condition );
            }


            helper.defaultAttack(source, target);

            Integer newToughness = target.getIntParam(PARAMS.C_TOUGHNESS);
            Integer newEndurance = target.getIntParam(PARAMS.C_ENDURANCE);
            assertTrue(newToughness < origToughness);
            assertTrue(newEndurance < origEndurance);

        }

    }


}
