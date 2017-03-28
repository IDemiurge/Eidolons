package main.game.logic.combat.mechanics;

import main.ability.effects.Effect;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.ability.effects.Effects;
import main.ability.effects.oneshot.DealDamageEffect;
import main.ability.effects.oneshot.attack.force.ForceEffect;
import main.ability.effects.oneshot.attack.force.KnockdownEffect;
import main.ability.effects.oneshot.move.MoveEffect;
import main.content.PARAMS;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_CASE;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_SpellObj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.DirectionMaster;
import main.game.logic.combat.damage.Damage;
import main.game.logic.combat.damage.DamageFactory;
import main.rules.RuleMaster;
import main.rules.RuleMaster.RULE;
import main.rules.combat.KnockdownRule;
import main.rules.mechanics.InterruptRule;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.math.Formula;
import main.system.math.MathMaster;
import main.system.math.roll.RollMaster;

public class ForceRule {
    public static final int ROLL_FACTOR = 50;
    private static final int DAMAGE_FACTOR = 100;
    private static final float PUSH_DISTANCE_COEFFICIENT = 0.1F;
    private static final float KNOCK_MAX_WEIGHT_COEFFICIENT = 0.25F;
    private static final float KNOCK_ALWAYS_WEIGHT_COEFFICIENT = 0.05F;
    private static final float INTERRUPT_MAX_WEIGHT_COEFFICIENT = 0.5F;

    // or spell!
    public static int getForceFromAttack(DC_ActiveObj attack) {
        boolean offhand = attack.isOffhand();
        Obj weapon = attack.getOwnerObj().getActiveWeapon(offhand);
        if (attack.isRanged()) {
            if (!attack.isThrow()) {
                weapon = weapon.getRef().getObj(KEYS.AMMO);
            }
        }
        if (weapon == null) {
            return 0;
        }
        Integer weightModifier = getAttackerWeightModifier(attack, weapon);
        double strengthModifier = getStrengthModifier(attack, weapon);


        int force = weightModifier + (int) strengthModifier;
        force = MathMaster.applyModIfNotZero(force, attack.getIntParam(PARAMS.FORCE_MOD));
        attack.setParam(PARAMS.FORCE, force);

        LogMaster.log(1, "getForceFromAttack = weightModifier"
         + weightModifier + "+strengthModifier* "
         + new Float(attack.getIntParam(PARAMS.FORCE_MOD)) / 100 + " = " + force);
        return force;

    }

    private static double getStrengthModifier(DC_ActiveObj attack, Obj weapon) {
        int strength = attack.getOwnerObj().getIntParam(PARAMS.STRENGTH);
        int weight = weapon.getIntParam(PARAMS.WEIGHT);
        return MathMaster.applyModIfNotZero(
         Math.min(weight * weight * 2, weight * strength)
         , attack.getIntParam(PARAMS.FORCE_MOD_WEAPON_WEIGHT));
    }

    private static int getAttackerWeightModifier(DC_ActiveObj attack, Obj weapon) {
        return MathMaster.applyMods(attack.getOwnerObj().getIntParam(PARAMS.WEIGHT)
         , weapon.getIntParam(PARAMS.FORCE_MOD_SOURCE_WEIGHT),
         attack.getIntParam(PARAMS.FORCE_MOD_SOURCE_WEIGHT));
    }

    private static int getForceFromSpell(DC_ActiveObj spell) {
        int force =
         // new Formula(spell.getProp(PROPS.FO)).getInt(ref);
         spell.getIntParam(PARAMS.FORCE, true) + spell.getOwnerObj().getIntParam(PARAMS.SPELLPOWER)
          * spell.getIntParam(PARAMS.FORCE_SPELLPOWER_MOD);
        return force;
    }

    public static void applyForceEffects(DC_ActiveObj action) {
        // or spell
        if (!RuleMaster.isRuleOn(RULE.FORCE)) {
            return;
        }
        int force = getForce(action, true);
        if (force == 0) {
            return;
        }
        applyForceEffects(force, action);
    }

    public static void applyForceEffects(int force, DC_ActiveObj action) {
        Unit target = (Unit) action.getRef().getTargetObj();
        Unit source = (Unit) action.getRef().getSourceObj();
        Boolean result = null;
        //TODO DEXTERITY ROLL TO AVOID ALL?
        if (target.getIntParam(PARAMS.TOTAL_WEIGHT) < getMinWeightKnock(action)) {
            result = RollMaster.rollForceKnockdown(target, action, force);
            if (BooleanMaster.isFalse(result))
                result = null; //ALWAYS INTERRUPT AT LEAST
        } else if (target.getIntParam(PARAMS.TOTAL_WEIGHT) > getMaxWeightKnock(action)) {
            result = false;
        } else {
            result = RollMaster.rollForceKnockdown(target, action, force);
        }
        if (isTestMode()) {
            result = true;
        }

        if (result == null) {
            InterruptRule.interrupt(target);
        } else if (result) {
            KnockdownRule.knockdown(target);
        }

        applyPush(force, action, source, target);
        if (action.isSpell()) {
            applyDamage(force, action, source, target);
        }
    }

    private static boolean isTestMode() {
        return RuleMaster.isRuleTestOn(RULE.FORCE);
        // return true;
    }

    private static int getForce(DC_ActiveObj attack) {
        return getForce(attack, false);
    }

    private static int getForce(DC_ActiveObj attack, boolean recalc) {
        if (!recalc) {
            return attack.getIntParam(PARAMS.FORCE);
        }
        if (attack instanceof DC_SpellObj) {
            return getForceFromSpell(attack);
        }
        return getForceFromAttack(attack);
    }


    public static void addForceEffects(DC_ActiveObj action) {
        Unit source = action.getOwnerObj();
        Unit target = (Unit) action.getRef().getTargetObj();
        Damage dmg = getDamageObject(action, source, target);

        Effect effects = getForceEffects(action);
        if (effects != null) {
            action.addSpecialEffect(
             action.isSpell() ? SPECIAL_EFFECTS_CASE.SPELL_IMPACT :
              SPECIAL_EFFECTS_CASE.ON_ATTACK,
             effects);
        }

        if (dmg != null) {
            action.addBonusDamage(action.isSpell() ? DAMAGE_CASE.SPELL : DAMAGE_CASE.ATTACK, dmg);
        }

    }

    private static Effect getForceEffects(DC_ActiveObj action) {
        String force = String.valueOf(getForce(action));
        KnockdownEffect e = new KnockdownEffect(force);
//        PushEffect e1 = new PushEffect(force);
//        InterruptionEffect e2 = new InterruptionEffect(force);
        Effects effects = new Effects();
//        if ()
//            effects.add(e);
        effects.add(new ForceEffect(force, action.isAttackAny()));
        return effects;

    }

    public static Damage getDamageObject(DC_ActiveObj action, Unit attacker, Unit attacked) {
        int amount = getDamage(action, attacker, attacked);
        if (amount <= 0) return null;
        DAMAGE_TYPE type = getForceDamageType(action);
        return DamageFactory.getGenericDamage(
         type, amount, new Ref(attacker, attacked));
    }

    private static DAMAGE_TYPE getForceDamageType
     (DC_ActiveObj action) {
        if (!action.getDamageType().isMagical())
            return DAMAGE_TYPE.BLUDGEONING;
        if (action.getDamageType().isNatural())
            return DAMAGE_TYPE.SONIC;

        return DAMAGE_TYPE.PSIONIC;
    }


    public static int getDamage(DC_ActiveObj action, Unit attacker, Unit attacked) {
        if (!RuleMaster.isRuleOn(RULE.FORCE)) {
            return 0;
        }
        int force = getForceFromAttack(action);
        return getDamage(force, action, attacker, attacked);
    }

    private static int getDamage(int force, Entity attack, Entity source, Unit attacked) {
        int damage = Math.round(force / DAMAGE_FACTOR);
        damage = damage

         * attack.getIntParam(PARAMS.FORCE_DAMAGE_MOD)
         / 100

         + damage
         * (attacked.getIntParam(PARAMS.FORCE_DAMAGE_MOD) - source
         .getIntParam(PARAMS.FORCE_PROTECTION)) / 100;
        return damage;
    }

    // TODO into PushEffect! With std knockdown on "landing" or damage!
    public static void applyPush(int force, DC_ActiveObj attack, Unit source,
                                 Unit target) {
        DIRECTION d = DirectionMaster.getRelativeDirection(source, target);

        if (attack.isSpell()) {
            d = DirectionMaster.getRelativeDirection(attack.getRef().getTargetObj(), target);
            // cell
        }
        int distance = getPushDistance(MathMaster.applyModIfNotZero(force, attack
         .getFinalModParam(PARAMS.FORCE_PUSH_MOD)), target);

        if (distance == 0) {
            if (isTestMode()) {
                distance = 1;
            } else {
                return;
            }
        }
        if (d.isDiagonal()) {
            distance = (int) Math.round(Math.sqrt(distance));
            if (distance == 0) {
                d = d.rotate90(RandomWizard.random());
                distance = 1;
            }
        }
        // int weight = target.getIntParam(PARAMS.TOTAL_WEIGHT);
        // if (distance == 0)
        // distance = RandomWizard.chance(force * 100 / weight) ? 1 : 0; // TODO

        int x_displacement = BooleanMaster.isTrue(d.isGrowX()) ? distance : -distance;
        int y_displacement = BooleanMaster.isTrue(d.isGrowY()) ? distance : -distance;
        if (!d.isDiagonal()) {
            if (d.isVertical()) {
                x_displacement = 0;
            } else {
                y_displacement = 0;
            }
        }

        Ref ref = attack.getRef().getCopy();
        ref.setTarget(target.getId());
        new MoveEffect("target", new Formula("" + x_displacement), new Formula("" + y_displacement))
         .apply(ref);

        // roll dexterity against Fall Down

        // TODO knock vs push - which is 'critical'?
        // maybe just apply a modifier, then calculate if push/knock?
        // sometimes it'll be better to be 'weak', eh?

    }

    public static Formula getCollisionDamageFormula(Unit moveObj, Unit collideObj,
                                                    int force, boolean forCollide) {
        int weight = (forCollide ? moveObj : collideObj).getIntParam(PARAMS.TOTAL_WEIGHT);
        // Math.min(, b);

        return null;
    }

    public static void applyDamage(int force, DC_ActiveObj attack, Unit source,
                                   Unit target) {
        int damage = getDamage(force, target, target, target);
        // attack.modifyParameter(PARAMS.BASE_DAMAGE, damage);
        // if (target.getShield()!=null )
        Ref ref = attack.getRef().getCopy();
        ref.setTarget(target.getId());
        new DealDamageEffect(new Formula(damage + ""), GenericEnums.DAMAGE_TYPE.BLUDGEONING).apply(ref);
    }


    private static int getPushDistance(int force, Unit target) {
        int distance = Math.round(force * PUSH_DISTANCE_COEFFICIENT /
         (1+target.getIntParam(PARAMS.TOTAL_WEIGHT)));
        LogMaster.log(1, "getPushDistance = " + force + "/10/"
         + target.getIntParam(PARAMS.TOTAL_WEIGHT) + " = " + distance);
        return distance;
    }

    public static int getMaxWeightPush(DC_ActiveObj action) {
        int force = getForce(action);
        force = MathMaster.applyMod(force,
         action.getIntParam(PARAMS.FORCE_PUSH_MOD));
        return Math.round(force * PUSH_DISTANCE_COEFFICIENT
         * 1.5f //round
        );
    }

    public static int getMaxWeightKnock(DC_ActiveObj action) {
        int force = getForce(action);
        force = MathMaster.applyMod(force,
         action.getIntParam(PARAMS.FORCE_KNOCK_MOD));
        return (int) (KNOCK_MAX_WEIGHT_COEFFICIENT * force);
    }

    public static int getMinWeightKnock(DC_ActiveObj action) {
        int force = getForce(action);
        force = MathMaster.applyMod(force,
         action.getIntParam(PARAMS.FORCE_KNOCK_MOD));
        return (int) (KNOCK_ALWAYS_WEIGHT_COEFFICIENT * force);
    }


}
