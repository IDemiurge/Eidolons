package tests.calc;

import elements.exec.EntityRef;
import logic.calculation.damage.DamageCalc;
import logic.calculation.damage.DamageCalcResult;
import tests.basic.BattleInitTest;

/**
 * Created by Alexander on 8/23/2023
 *
 * Some real numbers? Real content
 *
 */
public class DamageCalcTest extends BattleInitTest {

    @Override
    public void test() {
        super.test();

        EntityRef ref = new EntityRef(ally);
        ref.setTarget(enemy);
        DamageCalc damageCalc = new DamageCalc(ref);
        DamageCalcResult result = damageCalc.calculate(false);

        //assert result..

    }
}
