package tests.rules;

import elements.exec.EntityRef;
import elements.stats.UnitParam;
import elements.stats.UnitProp;
import logic.rules.combat.wounds.Wounds;
import logic.rules.combat.wounds.WoundsRule;
import system.math.Rolls;
import tests.basic.BattleInitTest;

import static combat.sub.BattleManager.combat;
import static main.content.enums.GenericEnums.DieType.d6;

/**
 * Created by Alexander on 10/24/2023
 */
public class WoundTest extends BattleInitTest {

    @Override
    public void test() {
        super.test();
        check(enemy.getInt(UnitParam.Defense_Auto_Fail) == 0);
        Rolls.setNext(d6, 1);
        EntityRef ref = new EntityRef(ally).setTarget(enemy);
        Wounds.apply(0, UnitParam.Health, ref);
        //pack into tick / reset
        // check(enemy.getInt(UnitParam.Defense_Auto_Fail) == 2);
        reset(2);
        check(enemy.getInt(UnitParam.Defense_Auto_Fail) == 2); // body wound: -2 Категории Броска Защиты
        enemy.setValue(UnitProp.Wound_Body, false);
        reset(2);
        check(enemy.getInt(UnitParam.Defense_Auto_Fail) == 0);

        //will have 2 auto-fails on defense? AYE! Or rather, enemy has 2 auto-successes?
    }

}
