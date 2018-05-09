package tests.logic.combat;


import eidolons.ability.effects.oneshot.DealDamageEffect;
import eidolons.content.PARAMS;
import eidolons.game.battlecraft.rules.combat.damage.Damage;
import eidolons.game.battlecraft.rules.combat.damage.DamageDealer;
import eidolons.game.battlecraft.rules.combat.damage.DamageFactory;
import main.ability.effects.Effect;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_MODIFIER;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.system.math.Formula;
import org.junit.Test;
import tests.entity.TwoUnitsTest;

import static org.junit.Assert.assertTrue;


/**
 * Created by JustMe on 3/6/2017.
 */
public class DealDamageEffectTest extends TwoUnitsTest {

    @Test
    public void dealDamageEffectTest() {
 

        int origToughness = unit2.getIntParam(PARAMS.C_TOUGHNESS);
        int origEndurance = unit2.getIntParam(PARAMS.C_ENDURANCE);



        Effect eff = new DealDamageEffect(new Formula("50"),
         GenericEnums.DAMAGE_TYPE.BLUDGEONING.getName(), DAMAGE_MODIFIER.UNBLOCKABLE);
        Ref ref = new Ref(unit);
        ref.setTarget(unit2.getId());
        ref.setID(KEYS.ACTIVE, unit.getAction("Attack").getId());
        eff.apply(ref);
        Integer newToughness = unit2.getIntParam(PARAMS.C_TOUGHNESS);
        Integer newEndurance = unit2.getIntParam(PARAMS.C_ENDURANCE);

        assertTrue(newToughness < origToughness);
        assertTrue(newEndurance < origEndurance);


    }

    @Test
    public void dealDamageObjectTest() {

        int origToughness = unit2.getIntParam(PARAMS.C_TOUGHNESS);
        int origEndurance = unit2.getIntParam(PARAMS.C_ENDURANCE);



        DealDamageEffect eff = new DealDamageEffect(new Formula("50"),
                GenericEnums.DAMAGE_TYPE.BLUDGEONING.getName(), DAMAGE_MODIFIER.UNBLOCKABLE);

        Damage dmg = DamageFactory.getDamageFromEffect(eff,25);
        dmg.getRef().setTarget(unit2.getId());
        DamageDealer.dealDamage(dmg);


        Integer newToughness = unit2.getIntParam(PARAMS.C_TOUGHNESS);
        Integer newEndurance = unit2.getIntParam(PARAMS.C_ENDURANCE);

        assertTrue(newToughness < origToughness);
        assertTrue(newEndurance < origEndurance);


    }

}
