package tests.logic.combat.atb;

import main.entity.obj.BuffObj;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 5/6/2018.
 */
public class JUnitBuffDuration extends JUnitAtb {
    private String buffName = BuffObj.DUMMY_BUFF_TYPE;
    private float defaultDuration = 10f;

    @Override
    protected void prepare() {
        helper.buff(unit, buffName, defaultDuration);
    }

    @Override
    protected void atbTest() {
        atbHelper.passTime(5f);
        assertTrue(unit.getBuff(buffName).getDuration() == defaultDuration - 5f);
        atbHelper.passTime(5f);
        helper.resetAll();
        assertTrue(
         unit.getBuff(buffName) == null);
    }
}
