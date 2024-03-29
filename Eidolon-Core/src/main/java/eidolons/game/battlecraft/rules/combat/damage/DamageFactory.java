package eidolons.game.battlecraft.rules.combat.damage;

import eidolons.ability.effects.continuous.BonusDamageEffect;
import eidolons.ability.effects.oneshot.DealDamageEffect;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.unit.attach.DC_HeroAttachedObj;
import eidolons.game.battlecraft.rules.combat.attack.Attack;
import main.content.enums.GenericEnums.DAMAGE_CASE;
import main.content.enums.GenericEnums.DAMAGE_MODIFIER;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.NewRpgEnums;
import main.entity.Ref;
import main.system.auxiliary.NumberUtils;

import java.util.List;

/**
 * Created by JustMe on 3/20/2017.
 */
//REVIEW0321 why no comments on public methods. at least explain
public class DamageFactory {

    public static Damage getGenericDamage(DAMAGE_TYPE damageType,
                                          int amount, Ref ref) {
        Damage damageObject = new Damage();
        damageObject.setAmount(amount);
        damageObject.setDmgType(damageType);
        damageObject.setRef(ref);
        return damageObject;
    }

    public static Damage getDamageFromEffect(DealDamageEffect effect, int amount) {
        Ref ref = Ref.getCopy(effect.getRef());
        if (ref.getSourceObj() instanceof DC_HeroAttachedObj) {
            ref.setSource(((DC_HeroAttachedObj) ref.getSourceObj()).getOwnerObj().getId());
        }
        Damage damageObject;
        List<Damage> list =
                DamageCalculator.getBonusDamageList(effect.getRef(), effect.isMagical()
                        ? DAMAGE_CASE.SPELL : DAMAGE_CASE.ACTION);
        if (!list.isEmpty()) {
            damageObject = new MultiDamage();
        } else {
            damageObject = new Damage();
        }

        damageObject.setAmount(amount);
        damageObject.setDmgType(effect.getDamageType());
        damageObject.setRef(effect.getRef());
        //TODO DC Revamp Finalize
        damageObject.setHitType(NewRpgEnums.HitType.hit);
        return damageObject;
    }
    //    public static MultiDamage getDamageForMultiDamageTest(DAMAGE_TYPE dmg_type,
    //                                                 Ref ref, Integer amount) {
    //    }

    //TODO what is the amount?! can the same attack have different amounts? dont they come like from the weapon, unit, position..
    public static Damage getDamageForAttack(DAMAGE_TYPE dmg_type,
                                            Ref ref, Integer amount) {
        List<Damage> list =
                DamageCalculator.getBonusDamageList(ref, DAMAGE_CASE.ATTACK);
        Damage damageObject;
        if (list.isEmpty()) {
            damageObject = new Damage();
        } else {
            MultiDamage multiDamage = new MultiDamage();
            multiDamage.setAdditionalDamage(list);
            damageObject = multiDamage;
        }
        damageObject.setAmount(amount);
        damageObject.setDmgType(dmg_type);
        damageObject.setRef(ref);
        return damageObject;
    }

    public static Damage getDamageFromAttack(Attack attack) {
        return
                getDamageForAttack(attack.getDamageType(), attack.getRef(), attack.getDamage());
    }

    public static Damage getDamageForBonusEffect
            (BonusDamageEffect effect) {
        Damage damage;
        if (NumberUtils.isInteger(effect.getFormula().toString())) {
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
        DAMAGE_MODIFIER[] modifiers = {
                DAMAGE_MODIFIER.UNBLOCKABLE,
                DAMAGE_MODIFIER.ARMOR_AVERAGED
        };
        damage.setModifiers(modifiers);

        if (effect.getConditions() != null) {
            damage = new ConditionalDamage(damage, effect.getConditions());
        }

        return damage;
    }

    public static Damage getDamageForPrecalculate(Ref ref) {
        Damage dmg = new Damage();
        int amount = ref.getAmount();
        // if (ref.getActive().isSpell())
        DAMAGE_TYPE damageType = ref.getDamageType();

        if (ref.getActive() instanceof ActiveObj) {
            ActiveObj activeObj = (ActiveObj) ref.getActive();
            dmg.setAction(activeObj);
            if (damageType == null) {
                damageType = activeObj.getDamageType();
            }
        }
        dmg.setRef(ref);
        dmg.setAmount(amount);
        dmg.setDmgType(damageType);
        return dmg;
    }

    public static MultiDamage getMultiDamage(Damage damage) {
        MultiDamage d = new MultiDamage();
        d.setAmount(damage.amount);
        d.setRef(damage.getRef());
        d.setDmgType(damage.getDmgType());
        return d;
    }
}
