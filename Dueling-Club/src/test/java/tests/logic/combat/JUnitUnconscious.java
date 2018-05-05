package tests.logic.combat;

import eidolons.content.PARAMS;
import eidolons.game.core.atb.AtbMaster;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 5/5/2018.
 */
public class JUnitUnconscious extends CombatTest {

    @Override
    public void test() {
        super.test();
        unit2.setParameter(PARAMS.C_TOUGHNESS, 0);
        Integer focus = unit2.getIntParam(PARAMS.C_FOCUS);
        Integer readiness = AtbMaster.getReadiness(unit2);
        helper.resetAll();
        assertTrue(unit2.isUnconscious());
        assertTrue(!unit2.canAct());
        assertTrue(focus> unit2.getIntParam(PARAMS.C_FOCUS));
//        assertTrue(readiness> AtbMaster.getReadiness(unit2));


        unit2.setParameter(PARAMS.C_TOUGHNESS, unit2.getIntParam(PARAMS. TOUGHNESS));
        unit2.setParameter(PARAMS.C_FOCUS, 100);
        atbHelper.startCombat();
        helper.resetAll();
        atbHelper.startCombat();
        helper.resetAll();
        assertTrue(!unit2.isUnconscious());


    }
}
