package tests.basic_init.damage;

import elements.content.enums.types.CombatTypes.DamageType;
import elements.stats.UnitParam;
import elements.stats.UnitProp;
import logic.rules.combat.WardRule;
import tests.basic_init.basic.BattleInitTest;

/**
 * Created by Alexander on 10/24/2023
 */
public class WardTest extends BattleInitTest {
    @Override
    public void test() {
        super.test();
        check(ally.checkContainerProp(UnitProp.Wards, DamageType.Splash.toString()));

        markValueToCheck(UnitParam.Health, UnitParam.Armor);
        // WardRule.checkWard(ally, DamageType.Splash, enemy);
        stdAttack(enemy, ally);
        //need actual attack for this!
        checkValueIsSame();
        check(ally.isTrue(WardRule.getBrokenKey(DamageType.Splash)));
        // check(ally.checkContainerProp(UnitProp.Wards, DamageType.Splash.toString()));

    }

}
