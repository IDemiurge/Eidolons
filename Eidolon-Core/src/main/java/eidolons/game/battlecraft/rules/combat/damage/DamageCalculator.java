package eidolons.game.battlecraft.rules.combat.damage;

import eidolons.ability.effects.oneshot.DealDamageEffect;
import eidolons.content.PARAMS;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.combat.attack.Attack;
import eidolons.game.battlecraft.rules.combat.attack.AttackCalculator;
import eidolons.game.battlecraft.rules.combat.attack.SneakRule;
import eidolons.game.battlecraft.rules.round.UnconsciousRule;
import eidolons.game.core.master.EffectMaster;
import main.ability.effects.Effect;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_CASE;
import main.content.enums.GenericEnums.DAMAGE_MODIFIER;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.NewRpgEnums;
import main.content.enums.entity.UnitEnums;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.IActiveObj;
import main.entity.obj.Obj;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/14/2017.
 * <portrait>
 * Calculates final Endurance/Toughness losses that BattleFieldObject receives from Damage Also calculates damage for AI
 * FutureBuilder, Unit's displayed value and PhaseAnimations
 */
public class DamageCalculator {

    protected static int calculateToughnessDamage(BattleFieldObject attacked, BattleFieldObject attacker,
                                                  int base_damage, Ref ref, DAMAGE_TYPE damage_type, StringBuilder log) {
       /*
       NF Rules
       Now Toughness acts as a buffer of HP you can lose each round without consequence.
       > Cannot take Endurance damage while has Toughness (unless damage is VORPAL)
        */
       if (checkDamagePenetratesToughness(ref, damage_type))
           return 0;
        return calculateDamage(false, attacked, attacker, base_damage, ref,
                damage_type, log);
    }


    protected static int calculateEnduranceDamage(BattleFieldObject attacked, BattleFieldObject attacker,
                                                  int base_damage, Ref ref,  DAMAGE_TYPE damage_type, StringBuilder log) {
        return calculateDamage(true, attacked, attacker, base_damage, ref,
                damage_type, log);
    }

    private static boolean checkDamagePenetratesToughness(Ref ref, DAMAGE_TYPE damage_type) {
        //TODO NF Rules review
        if (isEnduranceOnly(ref))
            return true;
        return damage_type== DAMAGE_TYPE.POISON || damage_type== DAMAGE_TYPE.PURE;
    }
    public static int calculateDamage(boolean endurance, BattleFieldObject attacked, BattleFieldObject attacker,
                                      int base_damage, Ref ref,DAMAGE_TYPE damage_type, StringBuilder log) {

        int amount = base_damage ;
        // int resistance = ResistMaster.getResistanceForDamageType(attacked, attacker, damage_type);
        // amount = amount - MathMaster.applyPercent(amount, resistance);
        // int armor = ArmorMaster.getArmorValue(attacked, damage_type);
        //TODO Review - now before T/E dmg calc in DamageDealer!
        // if (log != null) {
        //     log.append("Damage reduced by " +
        //             resistance +
        //             "% (Resistance), plus additional " +
        //             armor + " (Natural Armor)");
        // }
        if (endurance) {
            return amount;
            // return Math.min(amount, attacked.getIntParam(PARAMS.C_ENDURANCE)); allow negative Endurance
        }
        Integer toughness = attacked.getIntParam(PARAMS.C_TOUGHNESS);
        return Math.min(amount, toughness);
    }

    public static int precalculateDamage(Attack attack) {
        return precalculateDamage(attack, null);
    }

    /**
     * Calculates damage for AI's FutureBuilder (AttackEffect)
     *
     * @param attack
     * @return
     */
    public static int precalculateDamage(Attack attack, Boolean min_max_normal) {
        BattleFieldObject attacked = attack.getAttacked();
        BattleFieldObject attacker = attack.getAttacker();
        if (!attacked.checkPassive(UnitEnums.STANDARD_PASSIVES.SNEAK_IMMUNE)) {
            attack.setSneak(SneakRule.checkSneak(attack.getRef()));
        }
        // TODO ref.setFuture(true) -> average dice, auto-reset action etc
        AttackCalculator calculator = new AttackCalculator(attack, true);
        attack.setHitType(NewRpgEnums.HitType.hit);

        if (min_max_normal != null)
            if (min_max_normal) {
                calculator.setMin(true);
                calculator.setHitType(NewRpgEnums.HitType.graze);
                attack.setHitType(NewRpgEnums.HitType.graze);
            } else {
                calculator.setMax(true);
                calculator.setHitType(NewRpgEnums.HitType.critical_hit);
                attack.setHitType(NewRpgEnums.HitType.critical_hit);
            }
        int amount = calculator.calculateFinalDamage();
        DAMAGE_TYPE dmg_type = attack.getDamageType();
        return precalculateDamage(amount, dmg_type, attacked, attacker, attack.getWeapon(), attack.getAction());
    }

    public static int precalculateDamage(int amount, DAMAGE_TYPE dmg_type, BattleFieldObject attacked,
                                         BattleFieldObject attacker, WeaponItem weapon, ActiveObj action) {
        if (dmg_type == DAMAGE_TYPE.PURE || dmg_type == DAMAGE_TYPE.POISON) {
            return amount;
        }
        if (!(attacked instanceof Unit)) {
            return amount;
        }
        // int blocked = attacked.getGame().getArmorSimulator().getShieldDamageBlocked(amount,
        //         attacked, attacker, action, weapon, dmg_type);
        // blocked += attacked.getGame()
        //         .getArmorSimulator().getArmorBlockDamage(amount, attacked, attacker, action);

        Ref ref= new Ref(attacker);
        ref.setTarget(attacked.getId());
        ref.setAmount(amount);
        Damage damage = DamageFactory.getDamageForPrecalculate(ref);

        int blocked = attacked.getGame().getArmorSimulator().
                processDamage(damage).getBlocked();
        amount -= blocked;
        amount = calculateDamage(true, attacked, attacker, amount,
                null, dmg_type, null );

        return amount;
    }

    /**
     * Calculates damage for AI's FutureBuilder (DealDamageEffect)
     *
     * @param ref
     * @return
     */
    public static int precalculateDamage(Ref ref) {
        BattleFieldObject sourceObj = (Unit) ref.getSourceObj();
        Damage damage = DamageFactory.getDamageForPrecalculate(ref);
        damage.setHitType(getAverageHitType(sourceObj, ref.getTargetObj(), ref.getActive()));
        int amount = damage.getAmount();
        DAMAGE_TYPE damageType = damage.getDmgType();
        if (damage.getTarget() instanceof Unit) {
            int blocked = sourceObj.getGame().getArmorSimulator().
                    processDamage(damage).getBlocked();
            amount -= blocked;
        }
        amount -= amount * ResistMaster.getResistanceForDamageType(
                (Unit) ref.getTargetObj(), sourceObj,
                damageType) / 100;

        return amount;// applySpellArmorReduction(amount, (DC_HeroObj)
        // ref.getTargetObj(), ref.getSourceObj());

    }

    private static NewRpgEnums.HitType getAverageHitType(BattleFieldObject sourceObj,
                                                         Obj targetObj, IActiveObj active) {
        //TODO AI revamp
        return NewRpgEnums.HitType.hit;
    }

    public static boolean isDead(BattleFieldObject unit) {
        if (unit instanceof Unit) {
            return UnconsciousRule.checkUnitDies((Unit) unit);
        }
        if (0 >= unit.getIntParam(PARAMS.C_ENDURANCE)) {
            return true;
        }
        return 0 >= unit.getIntParam(PARAMS.C_TOUGHNESS);
    }

    public static boolean isUnblockable(Ref ref) {
        if (isPeriodic(ref)) {
            return true;
        }
        return StringMaster.compare(ref.getValue(KEYS.DAMAGE_MODS),
                DAMAGE_MODIFIER.UNBLOCKABLE
                        .toString());
    }

    public static boolean isPeriodic(Ref ref) {
        return StringMaster.compare(ref.getValue(KEYS.DAMAGE_MODS),
                GenericEnums.DAMAGE_MODIFIER.PERIODIC
                        .toString());
    }

    public static boolean isEnduranceOnly(Ref ref) {
        return StringMaster.compare(ref.getValue(KEYS.DAMAGE_MODS),
                GenericEnums.DAMAGE_MODIFIER.ENDURANCE_ONLY
                        .toString());
    }

    public static boolean isArmorAveraged(Ref ref) {
        return StringMaster.compare(ref.getValue(KEYS.DAMAGE_MODS),
                DAMAGE_MODIFIER.ARMOR_AVERAGED
                        .toString());
    }

    public static boolean isLethal(int damage, Obj targetObj) {
        return isDamageBeyondThreshold(damage, targetObj, false);
    }

    public static boolean isUnconscious(int damage, Obj targetObj) {
        return isDamageBeyondThreshold(damage, targetObj, true);
    }

    public static boolean isDamageBeyondThreshold(int damage, Obj targetObj, boolean unconscious) {
        if (targetObj.isDead()) {
            return false;
        } //annihilation!
        if (targetObj instanceof Unit) {
            return
                    unconscious ?
                            UnconsciousRule.checkFallsUnconscious((Unit) targetObj,
                                    targetObj.getIntParam(PARAMS.C_TOUGHNESS) - damage, targetObj.getIntParam(PARAMS.C_FOCUS))
                            : UnconsciousRule.checkUnitAnnihilated(
                            targetObj.getIntParam(PARAMS.C_ENDURANCE) - damage, (Unit) targetObj);
        }
        if (damage >= targetObj.getIntParam(PARAMS.C_TOUGHNESS)) {
            return true;
        }
        return damage >= targetObj.getIntParam(PARAMS.C_ENDURANCE);
    }

    public static List<Damage> getBonusDamageList(Ref ref, DAMAGE_CASE CASE) {
        List<Damage> list = new ArrayList<>();
        //TODO make BonusDamage all add to source?
        DC_Obj obj = (DC_Obj) ref.getSourceObj();
        for (DAMAGE_CASE e : obj.getBonusDamage().keySet()) {
            if (e == CASE) {
                list.addAll(obj.getBonusDamage().get(e));
            }
        }
        obj = (DC_Obj) ref.getObj(KEYS.ACTIVE);

        if (obj instanceof ActiveObj) {
            for (DAMAGE_CASE e : obj.getBonusDamage().keySet()) {
                if (e == CASE) {
                    list.addAll(obj.getBonusDamage().get(e));
                }
            }
            obj = ((ActiveObj) obj).getActiveWeapon();
            if (obj != null) {
                for (DAMAGE_CASE e : obj.getBonusDamage().keySet()) {
                    if (e == CASE) {
                        list.addAll(obj.getBonusDamage().get(e));
                    }
                }
            }
        }

        return list;
    }

    /**
     * Calculates and creates Damage Objects of each DAMAGE_TYPE present for PhaseAnimations
     *
     * @param attack
     * @return
     */
    public static List<Damage> precalculateRawDamageForDisplay(Attack attack) {

        List<Damage> list = new ArrayList<>();
        list.add(DamageFactory.getDamageFromAttack(attack));

        List<Effect> effects = new ArrayList<>();
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
            for (Effect dmgEffect : EffectMaster.getEffectsOfClass(e, DealDamageEffect.class)) {
                int amount = dmgEffect.getFormula().getInt(attack.getRef());
                list.add(DamageFactory.getDamageFromEffect((DealDamageEffect) dmgEffect, amount));
            }
        }
        // TODO display target's ON_HIT? PARAM_MODS?

        return list;
    }

    @Deprecated
    private static int initializeDamageModifiers(int amount, boolean offhand, BattleFieldObject unit,
                                                 WeaponItem weapon) {
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

    /**
     * @param unit
     * @param offhand
     * @return Displayed damage value for units (average)
     */
    @Deprecated
    public static Integer getUnitAttackDamage(Unit unit, boolean offhand) {
        int amount = unit.getIntParam(PARAMS.BASE_DAMAGE);
        WeaponItem weapon = unit.getWeapon(offhand);
        if (weapon == null) {
            weapon = unit.getNaturalWeapon(offhand);
        }
        if (weapon == null) {
            return (offhand) ? 0 : amount;
        }
        amount = initializeDamageModifiers(amount, offhand, unit, weapon);
        return amount;

    }


}
