package tests.entity;

import eidolons.entity.active.DC_ActionManager.STD_ACTIONS;
import eidolons.entity.obj.unit.Unit;
import main.game.logic.action.context.Context;
import org.junit.Test;
import res.JUnitResources;
import tests.FastDcTest;

/**
 * Created by JustMe on 4/12/2018.
 */
public class JUnitHelperTest extends FastDcTest {
    @Test
    public  void test() {
        Unit unit = helper.unit(JUnitResources.DEFAULT_UNIT, 0, 0, true);
        helper.move(unit, 1, 1);
        helper.doAction(unit, STD_ACTIONS.Move.name(), new Context(unit.getRef()), true);
//        helper.buff(unit, ;
        helper.kill(unit);







































    }
}
