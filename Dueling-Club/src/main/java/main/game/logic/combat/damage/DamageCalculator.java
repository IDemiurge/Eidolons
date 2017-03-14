package main.game.logic.combat.damage;

import main.ability.effects.Effect;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.ability.effects.oneshot.DealDamageEffect;
import main.content.PARAMS;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_MODIFIER;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.UnitEnums;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.ai.tools.target.EffectFinder;
import main.game.logic.combat.attack.Attack;
import main.game.logic.combat.attack.SneakRule;
import main.libgdx.anims.phased.PhaseAnimator;
import main.rules.round.UnconsciousRule;
import main.system.auxiliary.StringMaster;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.graphics.PhaseAnimation;
import main.system.math.DC_MathManager;
import main.system.math.MathMaster;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 3/14/2017.
 */
public class DamageCalculator {

protected static int calculateToughnessDamage(Unit attacked, Unit attacker,
                                        int base_damage, boolean magical, Ref ref, int blocked, DAMAGE_TYPE damage_type) {
        return calculateDamage(false, attacked, attacker, base_damage, magical, ref, blocked,
         damage_type);
    }

    private static int calculateDamage(boolean endurance, Unit attacked, Unit attacker,
                                       int base_damage, boolean magical, Ref ref, int blocked, DAMAGE_TYPE damage_type) {

        if (!endurance) {
            if (isEnduranceOnly(ref)) {
                return 0;
            }
        }

        int i = base_damage - blocked;
        int resistance = getResistanceForDamageType(i, attacked, attacker, damage_type);
        i = applyPercentReduction(i, resistance);
        int armor = ArmorMaster.getArmorValue(attacked, damage_type);
        PhaseAnimation animation = magical ? PhaseAnimator.getActionAnimation(ref, attacker) : PhaseAnimator.getAttackAnimation(ref,
         attacked);
        if (animation != null) {
            if (resistance != 0 || armor != 0) {
                animation.addPhaseArgs(PHASE_TYPE.REDUCTION_NATURAL, armor, resistance, base_damage
                 - blocked);
            }
        }
        return Math.max(0, i - armor);
    }

    protected static int calculateEnduranceDamage(Unit attacked, Unit attacker,
                                        int base_damage, boolean magical, Ref ref, int blocked, DAMAGE_TYPE damage_type) {
        return calculateDamage(true, attacked, attacker, base_damage, magical, ref, blocked,
         damage_type);
    }

    protected static int getArmorReduction(int base_damage, Unit attacked, Unit attacker,
                                 DC_ActiveObj action, boolean simulation) {
        // if (attacked.checkPassive(STANDARD_PASSIVES.IMMATERIAL)
        // || attacker.checkPassive(STANDARD_PASSIVES.IMMATERIAL))
        // return applySpellArmorReduction(base_damage, attacked, attacker);
        if (attacked.getArmor() == null) {
            return 0;
        }
        int blocked = (simulation ? attacked.getGame().getArmorSimulator() : attacked.getGame()
         .getArmorMaster()).getArmorBlockDamage(base_damage, attacked, attacker, action);
        return Math.min(base_damage, blocked);
    }

    public static int getDamage(Attack attack) {
        Unit attacked = attack.getAttacked();
        Unit attacker = attack.getAttacker();
        if (!attacked.checkPassive(UnitEnums.STANDARD_PASSIVES.SNEAK_IMMUNE)) {
            attack.setSneak(SneakRule.checkSneak(attack.getRef()));
        }
        // TODO ref.setFuture(true) -> average dice, auto-reset action etc
        int amount = attack.getPrecalculatedDamageAmount();
        DAMAGE_TYPE dmg_type = attack.getDamageType();
        if (dmg_type == DAMAGE_TYPE.PURE || dmg_type == DAMAGE_TYPE.POISON) {
            return amount;
        }
        amount -= applyAverageShieldReduction(amount, attacked, attacker, attack.getAction(),
         attack.getWeapon(), attack.getDamageType());
        amount -= DamageDealer.getArmorReduction(amount, attacked, attacker,
         attack.getAction());

        if (attack.getAction().isAttack()) {
            return amount;
        }
        return amount;
    }

    private static int getResistanceForDamageType(int amount, Unit attacked,
                                                  Unit attacker, DAMAGE_TYPE type) {
        if (type == null) {
            return 0;
        }
        int resistance = DC_MathManager.getDamageTypeResistance(attacked, type);
        if (type.isMagical()) {
            resistance -= attacker.getIntParam(PARAMS.RESISTANCE_PENETRATION);
        }
        return resistance;
    }

    private static int applyPercentReduction(int base_damage, int percent) {
        return MathMaster.applyMod(base_damage, percent);
//        int damage = base_damage - Math.round(base_damage * percent / 100);
//        return damage;
    }

    public static int getDamage(Ref ref) {
        int amount = ref.getAmount();
        // if (ref.getActive().isSpell())

        DAMAGE_TYPE damageType = ref.getDamageType();
        if (damageType == null) {
            if (ref.getActive() instanceof DC_ActiveObj) {
                DC_ActiveObj activeObj = (DC_ActiveObj) ref.getActive();
                damageType = activeObj.getDamageType();
            }
        }
        Unit sourceObj = (Unit) ref.getSourceObj();
        Damage damage = new Damage(damageType, amount, sourceObj, (Unit) ref.getTargetObj());
        int blocked = sourceObj.getGame().getArmorSimulator().getArmorBlockDamage(damage);
        amount -= blocked;
        amount = getResistanceForDamageType(amount, (Unit) ref.getTargetObj(), sourceObj,
         damageType);

        return amount;// applySpellArmorReduction(amount, (DC_HeroObj)
        // ref.getTargetObj(), ref.getSourceObj());

    }

    public static boolean isDead(BattleFieldObject unit) {
        if (unit instanceof Unit) {
            return UnconsciousRule.checkUnitDies((Unit) unit);
        }
        if (0 >= unit.getIntParam(PARAMS.C_ENDURANCE)) {
            return true;
        }
        if (0 >= unit.getIntParam(PARAMS.C_TOUGHNESS)) {
            return true;
        }
        return false;
    }

    public static boolean isUnblockable(Ref ref) {
        if (isPeriodic(ref))return true;
        return StringMaster.compare(ref.getValue(KEYS.DAMAGE_SOURCE),
         DAMAGE_MODIFIER.UNBLOCKABLE
          .toString());
    }
    static boolean isPeriodic(Ref ref) {
        return StringMaster.compare(ref.getValue(KEYS.DAMAGE_SOURCE),
         GenericEnums.DAMAGE_MODIFIER.PERIODIC
          .toString());
    }

    public static boolean isEnduranceOnly(Ref ref) {
        return StringMaster.compare(ref.getValue(KEYS.DAMAGE_SOURCE),
         GenericEnums.DAMAGE_MODIFIER.ENDURANCE_ONLY
          .toString());
    }
    public static boolean isLethal(int damage, Obj targetObj) {
        if (damage >= targetObj.getIntParam(PARAMS.C_TOUGHNESS)) {
            return true;
        }
        if (damage >= targetObj.getIntParam(PARAMS.C_ENDURANCE)) {
            return true;
        }
        return false;
    }

    public static List<Damage> precalculateRawDamageForDisplay(Attack attack) {

        List<Damage> list = new LinkedList<>();
        list.add(new Damage(attack.getDamageType(), attack.getDamage(), attack.getAttacked(),
         attack.getAttacker()));

        List<Effect> effects = new LinkedList<>();
        if (attack.getWeapon().getSpecialEffects() != null) {
            if (attack.getWeapon().getSpecialEffects().get(SPECIAL_EFFECTS_CASE.ON_ATTACK) != null) {
                effects.add(attack.getWeapon().getSpecialEffects().get(
                 SPECIAL_EFFECTS_CASE.ON_ATTACK));
            }
        }
        if (attack.getAttacker().getSpecialEffects() != null) {
            if (attack.getAttacker().getSpecialEffects().get(SPECIAL_EFFECTS_CASE.ON_ATTACK) != null) {
                effects.add(attack.getAttacker().getSpecialEffects().get(
                 SPECIAL_EFFECTS_CASE.ON_ATTACK));
            }
        }
        for (Effect e : effects) {
            // TODO ++ PARAM MOD
            for (Effect dmgEffect : EffectFinder.getEffectsOfClass(e, DealDamageEffect.class)) {
                int amount = dmgEffect.getFormula().getInt(attack.getRef());
                DAMAGE_TYPE damageType = ((DealDamageEffect) dmgEffect).getDamage_type();
                list.add(new Damage(damageType, amount, attack.getAttacked(), attack.getAttacker()));
            }
        }
        // TODO display target's ON_HIT? PARAM_MODS?

        return list;
    }

    @Deprecated
    private static int initializeDamageModifiers(int amount, boolean offhand, Unit unit,
                                                 DC_WeaponObj weapon) {
        amount += weapon.getDamageModifiers();
        amount += weapon.getIntParam(PARAMS.DAMAGE_BONUS);
        int hero_dmg_mod = unit.getIntParam((offhand) ? PARAMS.OFFHAND_DAMAGE_MOD
         : PARAMS.DAMAGE_MOD);
        if (hero_dmg_mod == 0) {
            hero_dmg_mod = 100;
        }
        Integer weapon_mod = weapon.getIntParam(PARAMS.DAMAGE_MOD);
        if (weapon_mod == 0) {
            weapon_mod = 100;
        }
        int dmg_mod = weapon_mod * hero_dmg_mod / 100;
        amount = amount * dmg_mod / 100;

        return amount;
    }
    @Deprecated
    public static Integer getUnitAttackDamage(Unit unit, boolean offhand) {
        int amount = unit.getIntParam(PARAMS.BASE_DAMAGE);
        DC_WeaponObj weapon = unit.getWeapon(offhand);
        if (weapon == null) {
            weapon = unit.getNaturalWeapon(offhand);
        }
        if (weapon == null) {
            return (offhand) ? 0 : amount;
        }
        amount = initializeDamageModifiers(amount, offhand, unit, weapon);
        return amount;

    }


    @Deprecated
    private static int applyAverageShieldReduction(int amount, Unit attacked,
                                                   Unit attacker, DC_ActiveObj action, DC_WeaponObj weapon, DAMAGE_TYPE damage_type) {
        if (!attacked.getGame().getArmorSimulator().checkCanShieldBlock(action, attacked)) {
            return 0;
        }
        int blocked = attacked.getGame().getArmorSimulator().getShieldDamageBlocked(amount,
         attacked, attacker, action, weapon, damage_type);

        return blocked;
    }
}
