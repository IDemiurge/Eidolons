package eidolons.game.battlecraft.rules.combat.attack;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.item.WeaponItem;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.combat.attack.accuracy.AccuracyMaster;
import eidolons.game.battlecraft.rules.combat.attack.block.BlockMaster;
import eidolons.game.battlecraft.rules.combat.attack.block.BlockResult;
import eidolons.game.battlecraft.rules.combat.attack.extra_attack.CounterAttackRule;
import eidolons.game.battlecraft.rules.combat.damage.Damage;
import eidolons.game.battlecraft.rules.combat.damage.DamageDealer;
import eidolons.game.battlecraft.rules.combat.damage.DamageFactory;
import eidolons.game.battlecraft.rules.mechanics.CoatingRule;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.master.EffectMaster;
import eidolons.system.audio.DC_SoundMaster;
import main.ability.effects.Effect;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.NewRpgEnums;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.logic.event.EventMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.Strings;
import main.system.auxiliary.log.LogMaster;
import main.system.sound.AudioEnums;

import static main.content.enums.entity.NewRpgEnums.HitType.*;

public class DC_AttackMaster {
    private final CounterAttackRule counterRule;
    private final DC_Game game;
    private final AccuracyMaster accuracyMaster;
    private final BlockMaster blockMaster;

    public DC_AttackMaster(DC_Game game) {
        this.game = game;
        counterRule = new CounterAttackRule(game);
        blockMaster = new BlockMaster(game);
        accuracyMaster = new AccuracyMaster(game);
    }

    public static Attack getAttackFromAction(DC_ActiveObj attackAction) {
        return EffectMaster.getAttackEffect(attackAction).getAttack();
    }

    private static void log(String message) {
        log(DC_Game.game, message);
    }

    private static void log(DC_Game game, String message) {
        // if (debug)
        //     LogMaster.log(LogMaster.ATTACKING_DEBUG, message);
        // else
        game.getLogManager().log(message);
    }

    public static WeaponItem getAttackWeapon(Ref ref, boolean offhand) {
        return (WeaponItem) (offhand ? ref.getObj(KEYS.OFFHAND) : ref.getObj(KEYS.WEAPON));
    }

    private static boolean checkWeapon(Ref ref) {
        return ref.getObj(KEYS.WEAPON) != null;
    }

    public boolean attack(Attack attack) {
        Boolean doubleAttack = attack.isDoubleStrike();

        Boolean result = attack(attack, attack.getRef(), attack.isFree(), attack.isCanCounter(), attack
                .getOnHit(), attack.getOnKill(), attack.isOffhand(), attack.isCounter());
        if (result == null) {
            return false;
        }
        if (doubleAttack == null) {
            return result;
        }
        if (doubleAttack) {
            result = attack(attack, attack.getRef(), attack.isFree(), false, attack
                    .getOnHit(), attack.getOnKill(), attack.isOffhand(), attack.isCounter());
        }


        return result;

    }

    private Boolean attack(Attack attack, Ref ref, boolean free, boolean canCounter,
                           Effect onHit,
                           Effect onKill, boolean offhand, boolean counter) {
        return attack(attack, ref, free, canCounter, onHit, onKill, offhand, counter, false);
    }

    private Boolean attack(Attack attack, Ref ref, boolean free, boolean canCounter,
                           Effect onHit,
                           Effect onKill, boolean offhand, boolean counter, boolean preview) {
        boolean extraAttack = attack.getAction().isExtraAttackMode();

        Boolean result;
        if (!extraAttack) {
            Unit redirectTarget = (Unit) GuardRule.checkTargetChanged(attack.getAction());
            if (redirectTarget != null) {
                game.getLogManager().log(redirectTarget.getNameIfKnown() + " intercepts " + attack.toLogString());
                ref.setTarget(redirectTarget.getId());
                attack.setRef(ref);
                attack.setAttacked(redirectTarget);
            }
        }

        attack.setSneak(SneakRule.checkSneak(ref));

        LogMaster.log(1, attack.getAttacker() + " attacks " +
                attack.getAttacked() + " with " + attack.getAction());

        DC_ActiveObj action = (DC_ActiveObj) ref.getObj(KEYS.ACTIVE);
        if (action.checkProperty(G_PROPS.ACTION_TAGS, "" + ActionEnums.ACTION_TAGS.OFF_HAND)) {
            offhand = true;
        }
        if (!offhand) {
            if (action.isRanged()) {
                if (!action.isThrow()) {
                    if (getAttackWeapon(ref, true).isRanged()) {
                        offhand = true;
                    }
                }
            }
        }

        result = attackNow(attack, action, ref, free, canCounter, onHit, onKill, offhand, counter);
        boolean countered = false;

        if (result == null) { // first strike
            game.getLogManager().log(attack.getAttacked() + ": First Strike Counter-Attack!");
            DC_ActiveObj counterAtk = counterRule.tryFindCounter(attack, false);
            if (counterAtk != null) {
                // AttackEffect effect = EffectMaster.getAttackEffect(counterAtk);
                counterRule.counterWith(attack.getAction(), counterAtk);
                attackNow(attack, action, ref, free, false, onHit, onKill, offhand, counter);
                countered = true;
            } else {
                game.getLogManager().log(LogMaster.LOG.GAME_INFO, attack.getAttacked().getNameIfKnown()
                        + " fails to counter-attack against " +
                        attack.getAttacker());
                attackNow(attack, action, ref, free, false, onHit, onKill, offhand, counter);
            }
            result = true;

        }

        // if ((!countered) || attack.getAttacker().hasDoubleCounter()) {
        //     if (canCounter) {
        //         if (!counter) {
        //             if (attack.getAttacker().hasDoubleCounter()) {
        //                 game.getLogManager().log(attack.getAttacker() + ": Double Counter-Attack!");
        //             }
        //             counterRule.tryFindCounter(attack);
        //         }
        //     }
        // }
        return result;
    }

    /**
     * @return null  if attack has been delayed by target's first strike; false if target is killed; true otherwise
     */
    private Boolean attackNow(Attack attack, DC_ActiveObj action, Ref ref, boolean free, boolean canCounter,
                              Effect onHit, Effect onKill, boolean offhand, boolean isCounter) {
        if (!(ref.getTargetObj() instanceof BattleFieldObject)) return true;
        BattleFieldObject attacked = (BattleFieldObject) ref.getTargetObj();
        Unit attacker = (Unit) ref.getSourceObj();
        if (attack.isSneak()) {
            if (attacked.checkPassive(UnitEnums.STANDARD_PASSIVES.SNEAK_IMMUNE)) {
                attack.setSneak(false);
                log(Strings.MESSAGE_PREFIX_INFO + attacked.getName()
                        + " is immune to Sneak Attacks!");
            } else log(Strings.MESSAGE_PREFIX_ALERT + attacker.getNameIfKnown()
                    + " makes a Sneak Attack against " + attacked.getName());
        }

        NewRpgEnums.HitType hitType = accuracyMaster.getHitType(attack);
        attack.setHitType(hitType);
        if (canCounter) canCounter = attacked.canCounter(action, attack.isSneak());

        logHitType(attacker.getNameIfKnown(), attacked.getName(), hitType, attack.getAccuracyRate());
        // } ====> Need a common messaging interface for actions/costs

        boolean countered = false;
        //TODO - do we need such Counter Attack at all???
        // if (canCounter) {
        //     if (attacked.hasFirstStrike() && !attacker.hasFirstStrike()) {
        //         if (!attacker.hasNoRetaliation()) {
        //             countered = counterRule.tryCounter(attack);
        //             return null;
        //         }
        //     }
        // }
        //interrupted!
        if (attacker.isDead()) return false;
        DC_SoundMaster.playEffectSound(AudioEnums.SOUNDS.ATTACK, attacker); // TODO revamp

        if (action.isRanged()) DC_SoundMaster.playRangedAttack(getAttackWeapon(ref, offhand));

        int amount = attacker.getIntParam(PARAMS.BASE_DAMAGE);
        ref.setAmount(amount);
        Event event = new Event(STANDARD_EVENT_TYPE.UNIT_IS_BEING_ATTACKED, ref);
        if (!event.fire()) {
            //interrupted!
            return false;
        }

        Unit attackedUnit = null;
        if (attacked instanceof Unit) attackedUnit = (Unit) attacked;

        boolean dodged = false;
        if (ref.getEffect().isInterrupted()) {
            event.getRef().getEffect().setInterrupted(false);
            dodged = true;
        }

        Integer final_amount = attack.getDamage();

        //TODO REAL CALC How to calc damage w/o crit (for parry)?
        if (final_amount == Attack.DAMAGE_NOT_SET) {
            AttackCalculator calculator = new AttackCalculator(attack, false);
            final_amount = calculator.calculateFinalDamage();
        }
        ref.setAmount(final_amount);

        if (final_amount < 0) {
            return true;
        }
        ref.setAmount(final_amount);
        BlockResult blockResult = null;
        if (!attacked.isDead())
            if (!dodged) {
                dodged = hitType == critical_miss || hitType == miss;
                if (!dodged && attacked instanceof Unit) {
                    blockResult = blockMaster.attacked(attack);
                    dodged = blockMaster.applyBlock(attack, blockResult);
                }
            }
        //TODO
        if (!blockResult.onBlockEffects.isEmpty()) {
            if (applyEffectGroup(ATTACK_EFFECT_GROUP.BLOCK, attack, ref.getCopy().setEffect(blockResult.onBlockEffects))) {
                return true;
            }
        }
        // BEFORE_ATTACK,
        // BEFORE_HIT
        if (!attacked.isDead())
            if (!dodged) {
                if (!new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_HIT, ref).fire()) {
                    return false;
                }
                if (attacker.isDead())
                    return true;
            } else {
                blockMaster.dodged(attack);
                applyEffectGroup(ATTACK_EFFECT_GROUP.DODGE, attack);
                return true;

            }
        attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.BEFORE_HIT, attacker, ref);

        attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.BEFORE_ATTACK, attacked, ref);


        DAMAGE_TYPE dmg_type = action.getActiveWeapon().getDamageType(); //ref.getDamageType();

        //TODO Rpg Review
        // if (attack.isCritical()) {
        //     if (attacker.checkPassive(UnitEnums.STANDARD_PASSIVES.CLEAVING_CRITICALS)) {
        //         // TODO add default cleave?
        //         CleaveRule.addCriticalCleave(attacker);
        //         dmg_type = GenericEnums.DAMAGE_TYPE.SLASHING;
        //     }
        // }

        if (dmg_type == null) {
            dmg_type = action.getDamageType();
        }
        if (dmg_type == null) {
            if (!checkWeapon(ref)) {
                dmg_type = attacker.getDamageType();
            } else {
                dmg_type = getAttackWeapon(ref, offhand).getDamageType();
            }
        }

        attack.setDamageType(dmg_type);
        if (attack.getDamage() == Attack.DAMAGE_NOT_SET) {
            attack.setDamage(final_amount);
        }

        if (!new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_ATTACKED, ref).fire()) {
            return false;
        }
        if (!new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_HIT, ref).fire()) {
            return false;
        }
        attack.setDamage(final_amount);
        if (checkAttackEventsInterrupt(attack, ref)) {
            return true;
        }

        Damage damageObj = DamageFactory.getDamageForAttack(
                dmg_type, ref, final_amount
        );
        damageObj.setSneak(attack.isSneak());
        damageObj.setHitType(attack.getHitType());
        int damageDealt = DamageDealer.dealDamage(
                damageObj);
        attack.damageDealt(damageDealt);

        attack.reset();
        if (attacked.isDead()) {
            if (!attack.isTriggered()) {
                game.getRules().getCleaveRule().apply(ref, attack);
            }
        }

        if (onHit != null) {
            onHit.apply(ref);
        }
        if (!action.isRanged()) {
            attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_HIT, attacker, ref);
        }
        attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_ATTACK, attacked, ref, offhand);

        if (attack.isSneak()) {
            applyEffectGroup(ATTACK_EFFECT_GROUP.SNEAK, attack);
        }
        if (attack.isCritical()) {
            applyEffectGroup(ATTACK_EFFECT_GROUP.CRIT, attack);
        }


        try {
            CoatingRule.unitIsHit(attacked, attacker, offhand, action, attack, attack.getWeapon());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        if (attackedUnit != null) {
            if (attack.isCritical()) {
            }
        }
        //        if (attacked.isDead()) { TODO in unit.kill()
        //            if (onKill != null) {
        //                onKill.apply(ref);
        //            }
        //            attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_DEATH, attacker, ref); // e.g.
        //            // retribution
        //            if (attacker.isDead()) {
        //                attack.setLethal(true);
        //                return true;
        //            }
        //            // attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_KILL,
        //            // attacker, ref); // already applied in DC_UnitObj.kill()
        //
        //        }

        // if (canCounter)
        // if ((!countered) || attacker.hasDoubleCounter())
        // tryFindCounter(attack);

        return true;

    }

    private void logHitType(String src, String target, NewRpgEnums.HitType hitType, int accuracyRate) {
        String msg = src + " makes a " + hitType.toString() + " on " + target
                + StringMaster.wrapInParenthesis("Accuracy: " + accuracyRate);
        log(msg);
    }

    public enum ATTACK_EFFECT_GROUP {
        DODGE, SNEAK, SHIELD, PARRY, BLOCK,
        CRIT, DEADEYE, //kill?
    }

    private boolean applyEffectGroup(ATTACK_EFFECT_GROUP group, Attack attack) {
        return applyEffectGroup(group, attack, null);
    }

    private boolean applyEffectGroup(ATTACK_EFFECT_GROUP group, Attack attack, Ref ref) {
        BattleFieldObject attacked = attack.getAttacked();
        Unit attacker = attack.getAttacker();
        Unit attackedUnit = null;
        if (attack.getAttacked() instanceof Unit) {
            attackedUnit = (Unit) attack.getAttacked();
        }
        if (ref == null)
            ref = attack.getRef();
        boolean offhand = attack.isOffhand();
        switch (group) {
            case BLOCK:
                if (checkEffectsInterrupt(attacker, attackedUnit, SPECIAL_EFFECTS_CASE.CUSTOM, ref, offhand)) {
                    return true;
                }
                break;
            case DODGE:
                if (checkEffectsInterrupt(attacker, attackedUnit, SPECIAL_EFFECTS_CASE.ON_DODGE, ref,
                        offhand)) {
                    return true;
                }
                if (checkEffectsInterrupt(attacked, attackedUnit, SPECIAL_EFFECTS_CASE.ON_DODGE_SELF, ref,
                        offhand)) {
                    return true;
                }
                break;
            case SNEAK:
                attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_SNEAK_HIT, attacker, ref);
                attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_SNEAK_HIT_SELF, attacked, ref);

                attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_SNEAK_ATTACK, attacked, ref);
                attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_SNEAK_ATTACK_SELF, attacker, ref);
                break;
            case CRIT:
                checkEffectsInterrupt(attacker, attacker, SPECIAL_EFFECTS_CASE.ON_CRIT_SELF, ref,
                        offhand);
                checkEffectsInterrupt(attackedUnit, attacker, SPECIAL_EFFECTS_CASE.ON_CRIT, ref, offhand);

                checkEffectsInterrupt(attackedUnit, attackedUnit, SPECIAL_EFFECTS_CASE.ON_CRIT_HIT_SELF, ref,
                        offhand);
                checkEffectsInterrupt(attacker, attackedUnit, SPECIAL_EFFECTS_CASE.ON_CRIT_HIT, ref, offhand);
                break;
            case DEADEYE:
                break;
        }
        return false;
    }

    private boolean checkAttackEventsInterrupt(Attack attack, Ref ref) {
        if (attack.isSneak()) {
            if (!EventMaster.fireStandard(STANDARD_EVENT_TYPE.ATTACK_SNEAK, ref)) {
                return true;
            }
        }
        if (attack.isAttackOfOpportunity()) {
            if (!EventMaster.fireStandard(STANDARD_EVENT_TYPE.ATTACK_OF_OPPORTUNITY, ref)) {
                return true;
            }
        }
        if (attack.isCounter()) {
            if (!EventMaster.fireStandard(STANDARD_EVENT_TYPE.ATTACK_COUNTER, ref)) {
                return true;
            }
        }
        if (attack.isCritical()) {
            if (!EventMaster.fireStandard(STANDARD_EVENT_TYPE.ATTACK_CRITICAL, ref)) {
                return true;
            }
        }
        if (attack.isInstant()) {
            if (!EventMaster.fireStandard(STANDARD_EVENT_TYPE.ATTACK_INSTANT, ref)) {
                return true;
            }
        }
        if (attack.isDodged()) {
            return !EventMaster.fireStandard(STANDARD_EVENT_TYPE.ATTACK_DODGED, ref);
        }
        //         ATTACK_BLOCKED,
        //         ATTACK_MISSED,
        return false;
    }

    private boolean checkEffectsInterrupt(BattleFieldObject target, Unit source,
                                          SPECIAL_EFFECTS_CASE case_type, Ref REF, boolean offhand) {
        source.applySpecialEffects(case_type, target, REF, offhand);
        return target.isDead();
        // if (attacker)
    }

    private boolean checkDeathEffects(Unit source, Unit target, Effect onKill, Ref ref,
                                      SPECIAL_EFFECTS_CASE CASE) {
        if (target.isDead()) {
            return false;
        }
        if (onKill != null) {
            onKill.apply(ref);
        }
        target.applySpecialEffects(CASE, source, ref);
        return true;
    }


    private void addAndWaitAttackAnimation(Attack attack) {
        //TODO gdx sync
        // GdxAdapter.getInstance().getAnimsApi.animate(new ActionInput(attack.getAction(),
        //         new Context(attack.getAttacker(),
        //                 attack.getAttacked())));

        //        if (attack.getAnimation() != null) { TODO is it required now??
        //            if (attack.getAnimation().isStarted()) {
        //                while (!attack.getAnimation().isFinished()) {
        //                    WaitMaster.WAIT(80);
        //                }
        //            }
        //        }
    }

}
