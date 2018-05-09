package tests.logic.combat.atb;

import tests.logic.combat.CombatTest;

/**
 * Created by JustMe on 5/6/2018.
 */
public abstract class JUnitAtb extends CombatTest {

    @Override
    public void test() {
        super.test();
        prepare();
        atbTest();

    }

    protected abstract void prepare();
    protected abstract void atbTest();
}
