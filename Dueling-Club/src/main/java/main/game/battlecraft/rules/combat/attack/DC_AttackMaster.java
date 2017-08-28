package main.game.battlecraft.rules.combat.attack;

import main.ability.effects.Effect;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.ability.effects.oneshot.attack.AttackEffect;
import main.content.PARAMS;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.active.DC_ActiveObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.ActiveObj;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.rules.action.StackingRule;
import main.game.battlecraft.rules.combat.attack.extra_attack.CounterAttackRule;
import main.game.battlecraft.rules.combat.damage.Damage;
import main.game.battlecraft.rules.combat.damage.DamageDealer;
import main.game.battlecraft.rules.combat.damage.DamageFactory;
import main.game.battlecraft.rules.combat.misc.CleaveRule;
import main.game.battlecraft.rules.combat.misc.InjuryRule;
import main.game.battlecraft.rules.mechanics.CoatingRule;
import main.game.core.game.DC_Game;
import main.game.core.master.EffectMaster;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.logic.event.EventMaster;
import main.libgdx.anims.phased.PhaseAnimator;
import main.system.audio.DC_SoundMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.AnimPhase;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.SOUNDS;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

public class DC_AttackMaster {
    private ParryRule parryRule;
    private CounterAttackRule counterRule;
    private DC_Game game;

    // private static boolean precalc;

    // private boolean sneak;
    // private boolean offhand;

    public DC_AttackMaster(DC_Game game) {
        this.game = game;
        counterRule = new CounterAttackRule(game);
        parryRule = new ParryRule(game);
    }

    public static Attack getAttackFromAction(DC_ActiveObj attackAction) {
        return EffectMaster.getAttackEffect(attackAction).getAttack();
    }

    private static void log(String message) {
        log(DC_Game.game, message);
    }

    private static void log(DC_Game game, String message) {
        // if (!precalc)
        game.getLogManager().log(message);
    }

    public static DC_WeaponObj getAttackWeapon(Ref ref, boolean offhand) {
        return (DC_WeaponObj) (offhand ? ref.getObj(KEYS.OFFHAND) : ref.getObj(KEYS.WEAPON));
    }

    private static boolean checkWeapon(Ref ref) {
        return ref.getObj(KEYS.WEAPON) != null;
    }

    public boolean attack(Attack attack) {
        Boolean doubleAttack = attack.isDoubleStrike();

        boolean result = attack(attack, attack.getRef(), attack.isFree(), attack.isCanCounter(), attack
         .getOnHit(), attack.getOnKill(), attack.isOffhand(), attack.isCounter());
        if (doubleAttack == null) {
            return result;
        }
        if (doubleAttack) {
            result = attack(attack, attack.getRef(), attack.isFree(), false, attack
             .getOnHit(), attack.getOnKill(), attack.isOffhand(), attack.isCounter());
        }


        return result;

    }

    private boolean attack(Attack attack, Ref ref, boolean free, boolean canCounter,
                           Effect onHit,
                           Effect onKill, boolean offhand, boolean counter) {
        ENTRY_TYPE type = ENTRY_TYPE.ATTACK;
        boolean extraAttack = true;
        if (attack.getAction().isCounterMode()) {
            type = ENTRY_TYPE.COUNTER_ATTACK;
        } else if (attack.getAction().isInstantMode()) {
            type = ENTRY_TYPE.INSTANT_ATTACK;
        } else if (attack.getAction().isAttackOfOpportunityMode()) {
            type = ENTRY_TYPE.ATTACK_OF_OPPORTUNITY;
        } else {
            extraAttack = false;
        }
//        LogEntryNode entry = game.getLogManager().newLogEntryNode(type,
//         attack.getAttacker().getName(), attack.getAttackedUnit().getName(), attack.getAction());
        Boolean result = null;
        if (!extraAttack) {
            Unit guard = (Unit) GuardRule.checkTargetChanged(attack.getAction());
            if (guard != null) {
                game.getLogManager().log(guard.getNameIfKnown()+" intercepts "+attack.toLogString());
                ref.setTarget(guard.getId());
                attack.setRef(ref);
                attack.setAttacked(guard);
            }
        }

        attack.setSneak(SneakRule.checkSneak(ref));
        try {
            main.system.auxiliary.log.LogMaster.log(1,
             attack.getAttacker() + " attacks " +
              attack.getAttackedUnit() +
              " with " + attack.getAction());
            result = attackNow(attack, ref, free, canCounter, onHit, onKill, offhand, counter);
            boolean countered = false;
            if (result == null) { // first strike
                main.system.auxiliary.log.LogMaster.log(1,
                 "Counter attack with first strike against " + attack.getAction());
                ActiveObj action = counterRule.tryCounter(attack, false);
                AttackEffect effect = EffectMaster.getAttackEffect(action);
                waitForAttackAnimation(effect.getAttack());
                attackNow(attack, ref, free, false, onHit, onKill, offhand, counter);

                countered = true;
                result = true;
            }
            if ((!countered) || attack.getAttacker().hasDoubleCounter()) {
                if (canCounter) {
                    if (!counter) {
                        counterRule.tryCounter(attack);
                    }
                }
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        } finally {
            if (!extraAttack) {
                game.getLogManager().doneLogEntryNode(ENTRY_TYPE.ACTION);
            }
            game.getLogManager().doneLogEntryNode(type);

        }
        return result;
    }

    /**
     * @return null  if attack has been delayed by target's first strike; false if target is killed; true otherwise
     */
    private Boolean attackNow(Attack attack, Ref ref, boolean free, boolean canCounter,
                              Effect onHit, Effect onKill, boolean offhand, boolean isCounter) {
        if (!(ref.getTargetObj() instanceof BattleFieldObject)) {
            return true;
        }
        // PhaseAnimation animation =
        // game.getAnimationManager().getAnimation(attack.getAction().getAnimationKey());
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

        BattleFieldObject attacked = (BattleFieldObject) ref.getTargetObj();
        Unit attacker = (Unit) ref.getSourceObj();
        if (attack.isSneak()) {
            if (attacked.checkPassive(UnitEnums.STANDARD_PASSIVES.SNEAK_IMMUNE)) {
                attack.setSneak(false);
                log(StringMaster.MESSAGE_PREFIX_INFO + attacked.getName()
                 + " is immune to Sneak Attacks!");
            } else {
                log(StringMaster.MESSAGE_PREFIX_ALERT + attacker.getNameIfKnown()
                 + " makes a Sneak Attack against " + attacked.getName());
            }
        }
        if (canCounter) {
            if (!attacked.canCounter(action, attack.isSneak())) {
                canCounter = false;
            }
        }

        LogMaster.log(LogMaster.ATTACKING_DEBUG, attacker.getNameIfKnown() + " attacks "
         + attacked.getName());
        // } ====> Need a common messaging interface for actions/costs

        String damage_mods = "";
        // if (sneak)
        // damage_mods+=DAMAGE_MODIFIER.SNEAK;
        ref.setValue(KEYS.DAMAGE_MODS, damage_mods);
        boolean countered = false;
        if (canCounter) {
            if (attacked.hasFirstStrike() && !attacker.hasFirstStrike()) {
                if (!attacker.hasNoRetaliation()) {
                    // countered = tryCounter(attack);
                    return null;
                }
            }
        }
        if (attacker.isDead()) {
            attack.getAnimation().addPhase(new AnimPhase(PHASE_TYPE.INTERRUPTED, ref));
            return false;
        }
        DC_SoundMaster.playEffectSound(SOUNDS.ATTACK, attacker); // TODO revamp

        if (action.isRanged()) {
            DC_SoundMaster.playRangedAttack(getAttackWeapon(ref, offhand));
        }

        int amount = attacker.getIntParam(PARAMS.BASE_DAMAGE);
        ref.setAmount(amount);
        Event event = new Event(STANDARD_EVENT_TYPE.UNIT_IS_BEING_ATTACKED, ref);
        if (!event.fire()) {
            attack.getAnimation().addPhase(new AnimPhase(PHASE_TYPE.INTERRUPTED, ref));
            return false;
        }
        // initializeFullModifiers(attack.isSneak(), offhand, action, ref);
        Boolean dodged = false;
        if (ref.getEffect().isInterrupted()) {
            event.getRef().getEffect().setInterrupted(false);
            dodged = true;
        }


        if (!dodged) {
            boolean parried = parryRule.tryParry(attack);
            if (parried) {
                if (
                 EventMaster.fireStandard(STANDARD_EVENT_TYPE.ATTACK_DODGED, ref)) {
                    attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_PARRY, attacker, ref);
                    attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_PARRY_SELF, attacked, ref);
                }
                return true;
            }
            dodged = DefenseVsAttackRule.checkDodgedOrCrit(attack);
        }

        // BEFORE_ATTACK,
        // BEFORE_HIT
        if (dodged == null) {
            if (!new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_HIT, ref).fire()) {
                return false;
            }
            if (attacker.isDead()) {
                return true;
            }
//            if (attacked.isDead()) {  // now in unit.kill()
//                if (onKill != null) {
//                    onKill.apply(ref);
//                }
//                attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_DEATH, attacker, ref);
//                return true;
//            }
        } else {
            if (dodged) {
                log(attacked.getName() + " has dodged an attack from " + attacker.getNameIfKnown());
                DC_SoundMaster.playMissedSound(attacker, getAttackWeapon(ref, offhand));
                StackingRule.actionMissed(action);
                // ++ animation? *MISS* //TODO ++ true strike
                action.setFailedLast(true);
                if (checkEffectsInterrupt(attacked, attacker, SPECIAL_EFFECTS_CASE.ON_DODGE, ref,
                 offhand)) {
                    return true;
                }
                if (canCounter) {
                    if ((!countered) || attacked.hasDoubleCounter()) {
                        // tryCounter(attack); TODO ?
                        return true;
                    }
                }
            } else {
                if (attacked.checkPassive(UnitEnums.STANDARD_PASSIVES.CRITICAL_IMMUNE)) {
                    log(StringMaster.MESSAGE_PREFIX_INFO + attacked.getName()
                     + " is immune to Critical Hits!");
                } else {
                    log(StringMaster.MESSAGE_PREFIX_ALERT + attacker.getNameIfKnown()
                     + " scores a critical hit on " + attacked.getName());
                    attack.setCritical(true);

                }
            }
        }
        attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.BEFORE_HIT, attacker, ref);
        if (attacked.isDead()) {
//         !checkDeathEffects(attacked, attacker, onKill, ref, SPECIAL_EFFECTS_CASE.ON_DEATH)) {
            return true;
        }
        attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.BEFORE_ATTACK, attacked, ref);
        if (attacked.isDead()) {
            //!checkDeathEffects(attacker, attacked, onKill, ref, SPECIAL_EFFECTS_CASE.ON_DEATH)) {
            return true;
        }
        Integer final_amount = attack.getDamage();

        //TODO REAL CALC How to calc damage w/o crit (for parry)?
        if (final_amount == Attack.DAMAGE_NOT_SET) {
            AttackCalculator calculator = new AttackCalculator(attack, false);
            final_amount = calculator.calculateFinalDamage();
        }
        // TODO different for multiDamageType
        if (CoreEngine.isPhaseAnimsOn())
            PhaseAnimator.getInstance().initAttackAnimRawDamage(attack);

        ref.setAmount(final_amount);

        if (final_amount < 0) {
            return true;
        }
        ref.setAmount(final_amount);
        DAMAGE_TYPE dmg_type = ref.getDamageType();

        if (attack.isCritical()) {
            if (attacker.checkPassive(UnitEnums.STANDARD_PASSIVES.CLEAVING_CRITICALS)) {
                // TODO add default cleave?
                CleaveRule.addCriticalCleave(attacker);
                dmg_type = GenericEnums.DAMAGE_TYPE.SLASHING;
            }
        }

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
        Unit attackedUnit = null;
        if (attacked instanceof Unit) {
            attackedUnit = (Unit) attacked;
        }
        if (attackedUnit != null)
            if (attackedUnit.getSecondWeapon() != null) {
                if (attackedUnit.getSecondWeapon().isShield()) {
                    if (!attack.isSneak() && !isCounter) {
                        int blocked = game.getArmorMaster().getShieldDamageBlocked(final_amount, attackedUnit,
                         attacker, action, getAttackWeapon(ref, attack.isOffhand()),
                         attack.getDamageType());
                        final_amount -= blocked;
                        if (blocked > 0) {
                            Ref REF = ref.getCopy();
                            REF.setAmount(blocked);
                            if (checkEffectsInterrupt(attackedUnit, attacker, SPECIAL_EFFECTS_CASE.ON_SHIELD_BLOCK,
                             REF, offhand)) {
                                return true;
                            }
                            if (checkEffectsInterrupt(attacker, attackedUnit,
                             SPECIAL_EFFECTS_CASE.ON_SHIELD_BLOCK_SELF, REF, offhand)) {
                                return true;
                            }
                        }

                    }
                }
            }
        // armor penetration?
        attack.setDamage(final_amount);
        if (checkAttackEventsInterrupt(attack, ref)) {
            return true;
        }

//        ForceRule.addForceEffects(action); now in executor.resolve() for all actions

        Damage damageObj = DamageFactory.getDamageForAttack(
         dmg_type, ref, final_amount
        );
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
            attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_HIT, attacker, ref); // e.g.
        }
        // spikes
        // map=

        if (attackedUnit != null)
            attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_ATTACK, attackedUnit, ref, offhand); // e.g.
        // TODO

        try {
            // map=
            CoatingRule.unitIsHit(attacked, attacker, offhand, action, attack, attack.getWeapon());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (attackedUnit != null) {
            InjuryRule.applyInjuryRule(action);
            if (attack.isCritical()) {
                checkEffectsInterrupt(attackedUnit, attacker, SPECIAL_EFFECTS_CASE.ON_CRIT_SELF, ref,
                 offhand);
                checkEffectsInterrupt(attacker, attackedUnit, SPECIAL_EFFECTS_CASE.ON_CRIT, ref, offhand);
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
        // tryCounter(attack);

        return true;

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
            if (!EventMaster.fireStandard(STANDARD_EVENT_TYPE.ATTACK_DODGED, ref)) {
                return true;
            }
        }
//         ATTACK_BLOCKED,
//         ATTACK_MISSED,
        return false;
    }

    private boolean checkEffectsInterrupt(BattleFieldObject target, Unit source,
                                          SPECIAL_EFFECTS_CASE case_type, Ref REF, boolean offhand) {
        source.applySpecialEffects(case_type, target, REF, offhand);
        if (target.isDead()) {
            return true;
        }
        // if (attacker)
        return false;
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


    private void waitForAttackAnimation(Attack attack) {
//        if (attack.getAnimation() != null) { TODO is it required now??
//            if (attack.getAnimation().isStarted()) {
//                while (!attack.getAnimation().isFinished()) {
//                    WaitMaster.WAIT(80);
//                }
//            }
//        }
    }

}
