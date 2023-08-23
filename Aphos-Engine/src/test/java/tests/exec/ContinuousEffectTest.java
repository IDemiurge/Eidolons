package tests.exec;

import framework.entity.field.Unit;
import tests.basic.BattleInitTest;

import static combat.sub.BattleManager.combat;

/**
 * Created by Alexander on 8/23/2023
 *
 * oneshot, continuous
 */
public class ContinuousEffectTest extends BattleInitTest {
    @Override
    public void test() {
        super.test();

        Unit unit = combat().getUnitById(0);
        // conditional effect? reset via moves
        // effect that might change... atk or so
        //it's a pretty rare thing in fact
        // how about counters or some such first?
    }
}
