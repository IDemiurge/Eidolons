package tests.logic.combat;

import main.game.logic.action.context.Context;
import org.junit.Ignore;
import org.junit.Test;
import tests.entity.TwoUnitsTest;

/**
 * Created by JustMe on 5/5/2018.
 */
public abstract class ActionTest extends TwoUnitsTest{

    @Test
    @Ignore
    public void test (){
        testAction(getActionName());
    }
    public void testAction(String actionName){
        atbHelper.startCombat();
        helper.doAction(unit, actionName, getContext(), true);
    }

    protected Context getContext() {
        return new Context(unit, unit2);
    }

    protected abstract String getActionName() ;
}
