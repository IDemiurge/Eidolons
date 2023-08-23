package tests.field;

import framework.entity.field.Unit;
import framework.field.FieldPos;
import tests.basic.BattleInitTest;

import java.util.Set;

import static combat.sub.BattleManager.combat;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alexander on 8/22/2023
 */
public class MoveTest extends BattleInitTest {
    @Override
    public void test() {
        super.test();
        Unit unit = combat().getUnitById(0);
        Set<FieldPos> positions = combat().getField().getAvailablePositions(unit);

        /*
        exhaustive move test - for all positions? What is the baseline logic we can check?

         */

        for (FieldPos position : positions) {
            combat().getField().stepMove(unit, position);
            assertTrue(unit.getPos().equals(position));
            assertTrue(combat().getField().getAvailablePositions(unit).size() == 0);
            combat().newRound();
            assertTrue(combat().getField().getAvailablePositions(unit).size() != 0);
            // assertTrue( combat().getField().);
        }
    }
}
