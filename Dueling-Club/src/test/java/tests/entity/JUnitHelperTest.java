package tests.entity;

import eidolons.entity.active.DC_ActionManager.STD_ACTIONS;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.logic.action.context.Context;
import org.junit.Test;
import res.JUnitResources;
import tests.FastDcTest;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 4/12/2018.
 */
public class JUnitHelperTest extends FastDcTest {
    @Test
    public  void test() {
        Unit unit = helper.unit(JUnitResources.DEFAULT_UNIT, 0, 0, true);
        helper.move(unit, 1, 1);
helper.turn(unit, FACING_DIRECTION.SOUTH);
        helper.turn(unit, true, true);
        helper.doAction(unit, STD_ACTIONS.Move.name(), new Context(unit.getRef()), true);
        assertTrue(unit.getX() == 0);
//        helper.buff(unit, ;

        Unit enemy = helper.unit(JUnitResources.DEFAULT_UNIT, 0, 0, false);
        helper.startCombat();

        //wait for player input
        helper.turn(unit, true, true);
        helper.defaultAttack(unit, enemy);

        helper.kill(unit);

        assertTrue(ExplorationMaster.isExplorationOn());







































    }
}
