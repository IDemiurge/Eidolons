package tests.basic_init.damage;

import elements.content.enums.types.CombatTypes.DamageType;
import elements.exec.EntityRef;
import elements.exec.effect.DamageEffect;
import elements.stats.UnitParam;
import tests.basic_init.basic.BattleInitTest;

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
        //apply dmg atk efx?

        EntityRef ref = new EntityRef(ally);
        ref.setTarget(enemy);
        int dmg= 5;
        int hp = enemy.getInt(UnitParam.Health);
        int block= enemy.getInt(UnitParam.Melee_block);
        DamageEffect damageEffect = new DamageEffect(DamageType.Strike, dmg);
        damageEffect.apply(ref);

        check(enemy.getInt(UnitParam.Health) == hp - dmg +block );
        check(enemy.getInt(UnitParam.Armor) == enemy.getInt(UnitParam.Armor_Max)- block );
        // damageEffect.getResult();


        //assert result..

    }
}
