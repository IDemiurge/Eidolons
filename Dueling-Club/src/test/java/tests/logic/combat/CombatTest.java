package tests.logic.combat;

import org.junit.Test;
import tests.entity.TwoUnitsTest;

/**
 * Created by JustMe on 5/5/2018.
 */
public class CombatTest extends TwoUnitsTest{

    @Test
    public void test(){
        atbHelper.startCombat();

    }

    @Override
    protected boolean isGraphicsOff() {
        return super.isGraphicsOff();
    }
}
