package main.game.logic.combat.damage;

import main.ability.effects.continuous.BonusDamageEffect;
import main.ability.effects.oneshot.DealDamageEffect;
import main.content.enums.GenericEnums.DAMAGE_CASE;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.game.logic.combat.attack.Attack;
import main.system.auxiliary.StringMaster;

import java.util.List;

/**
 * Created by JustMe on 3/20/2017.
 */
public class DamageFactory {

    public static Damage getDamageFromEffect(DealDamageEffect effect, int amount) {
        Damage damageObject;
        List<Damage> list =
         DamageCalculator.getBonusDamageList(effect.getRef(), effect.isMagical()
          ? DAMAGE_CASE.SPELL : DAMAGE_CASE.ACTION);
        if (!list.isEmpty())
            damageObject = new MultiDamage();
        else damageObject = new Damage();

        damageObject.setAmount(amount);
        damageObject.setDmgType(effect.getDamageType());
        damageObject.setRef(effect.getRef());
        return damageObject;
    }
//    public static MultiDamage getDamageForMultiDamageTest(DAMAGE_TYPE dmg_type,
//                                                 Ref ref, Integer amount) {
//    }

    public static MultiDamage getDamageForAttack(DAMAGE_TYPE dmg_type,
                                                 Ref ref, Integer amount) {
        List<Damage> list =
         DamageCalculator.getBonusDamageList(ref, DAMAGE_CASE.ATTACK);
        MultiDamage damageObject = new MultiDamage();
        damageObject.setAmount(amount);
        damageObject.setDmgType(dmg_type);
        damageObject.setRef(ref);
        damageObject.setAdditionalDamage(list);
        return damageObject;
    }

    public static Damage getDamageFromAttack(Attack attack) {
        return
         getDamageForAttack(attack.getDamageType(), attack.getRef(), attack.getDamage());
    }

    public static Damage getDamageForBonusEffect
     (BonusDamageEffect effect) {
        Damage damage;
        if (StringMaster.isInteger(effect.getFormula().toString())) {
            damage = new Damage();
            damage.setAmount(effect.getFormula().getInt(effect.getRef()));
        } else {
            damage = new FormulaDamage();
            ((FormulaDamage) damage).setPercentage(effect.isPercentage());
            ((FormulaDamage) damage).setFromRaw(effect.isFromRaw());
            ((FormulaDamage) damage).setFormula(effect.getFormula());
        }
        damage.setDmgType(effect.getType());
        damage.setRef(effect.getRef());
//TODO         damage.setModifiers( );

        if (effect.getConditions() != null)
            damage = new ConditionalDamage(damage, effect.getConditions());

        return damage;
    }

    public static Damage getDamageForPrecalculate(Ref ref) {
        int amount = ref.getAmount();
        // if (ref.getActive().isSpell())
        DAMAGE_TYPE damageType = ref.getDamageType();
        if (damageType == null) {
            if (ref.getActive() instanceof DC_ActiveObj) {
                DC_ActiveObj activeObj = (DC_ActiveObj) ref.getActive();
                damageType = activeObj.getDamageType();
            }
        }
        Damage dmg = new Damage();
        dmg.setRef(ref);
        dmg.setAmount(amount);
        dmg.setDmgType(damageType);
        return dmg;
    }
}
