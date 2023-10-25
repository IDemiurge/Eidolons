package tests.basic_init.rules;

import elements.exec.EntityRef;
import elements.exec.effect.counter.CounterEffect;
import elements.stats.Counter;
import elements.stats.UnitParam;
import tests.basic_init.basic.BattleInitTest;

/**
 * Created by Alexander on 10/25/2023
 */
public class CounterTest extends BattleInitTest {

    @Override
    public void test() {
        super.test();

        CounterEffect effect = new CounterEffect();
        String counterName = Counter.Blaze.name();
        int counterValue = 2;
        effect.setValue(0, counterName).setValue(1, counterValue);
        effect.apply(new EntityRef(ally).setTarget(enemy));
        check(enemy.getInt(counterName) == 0); //ward :)
        effect.apply(new EntityRef(ally).setTarget(enemy));
        check(enemy.getInt(counterName) == counterValue);


        counterName = Counter.Haze.name();
        counterValue = 3;
        markValueToCheck(UnitParam.Attack_Base);
        effect.setValue(0, counterName).setValue(1, counterValue);
        effect.apply(new EntityRef(enemy).setTarget(ally));
        reset(1);
        checkValueChanged(UnitParam.Attack_Base, -3);
    }
}
