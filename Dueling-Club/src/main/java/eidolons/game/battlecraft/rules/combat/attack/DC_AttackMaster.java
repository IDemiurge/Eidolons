package eidolons.game.battlecraft.rules.combat.attack;

import eidolons.ability.effects.oneshot.attack.AttackEffect;
import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.action.StackingRule;
import eidolons.game.battlecraft.rules.combat.attack.extra_attack.CounterAttackRule;
import eidolons.game.battlecraft.rules.combat.damage.Damage;
import eidolons.game.battlecraft.rules.combat.damage.DamageDealer;
import eidolons.game.battlecraft.rules.combat.damage.DamageFactory;
import eidolons.game.battlecraft.rules.combat.misc.CleaveRule;
import eidolons.game.battlecraft.rules.combat.misc.InjuryRule;
import eidolons.game.battlecraft.rules.mechanics.CoatingRule;
import eidolons.game.battlecraft.rules.mechanics.DurabilityRule;
import eidolons.game.core.ActionInput;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.master.EffectMaster;
import eidolons.libgdx.anims.AnimContext;
import eidolons.libgdx.anims.main.ActionAnimMaster;
import eidolons.system.audio.DC_SoundMaster;
import main.ability.effects.Effect;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_TYPE;
import main.content.enums.entity.ActionEnums;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.game.logic.event.EventMaster;
import main.system.auxiliary.Strings;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.Flags;
import main.system.sound.SoundMaster.SOUNDS;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;

public class DC_AttackMaster {
    private final ParryRule parryRule;
    private final CounterAttackRule counterRule;
    private final DC_Game game;

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
                game.getLogManager().log(guard.getNameIfKnown() + " intercepts " + attack.toLogString());
                ref.setTarget(guard.getId());
                attack.setRef(ref);
                attack.setAttacked(guard);
            }
        }

        attack.setSneak(SneakRule.checkSneak(ref));
        try {
            main.system.auxiliary.log.LogMaster.log(1,
                    attack.getAttacker() + " attacks " +
                            attack.getAttacked() +
                            " with " + attack.getAction());
            result = attackNow(attack, ref, free, canCounter, onHit, onKill, offhand, counter);
            boolean countered = false;
            if (result == null) { // first strike
                game.getLogManager().log(attack.getAttacked() + ": First Strike Counter-Attack!");
                DC_ActiveObj action = counterRule.tryFindCounter(attack, false);
                if (action != null) {
                    AttackEffect effect = EffectMaster.getAttackEffect(action);
                    addAndWaitAttackAnimation(effect.getAttack());

                    counterRule.counterWith(attack.getAction(), action);

                    attackNow(attack, ref, free, false, onHit, onKill, offhand, counter);
                    countered = true;
                } else {
                    addAndWaitAttackAnimation(attack);
                    game.getLogManager().log(LogMaster.LOG.GAME_INFO, attack.getAttacked().getNameIfKnown()
                            + " fails to counter-attack against " +
                            attack.getAttacker());
                    attackNow(attack, ref, free, false, onHit, onKill, offhand, counter);
                }
                result = true;

            } else {
                addAndWaitAttackAnimation(attack);
            }
            if ((!countered) || attack.getAttacker().hasDoubleCounter()) {
                if (canCounter) {
                    if (!counter) {
                        if (attack.getAttacker().hasDoubleCounter()) {
                            game.getLogManager().log(attack.getAttacker() + ": Double Counter-Attack!");
                        }
                        counterRule.tryFindCounter(attack);
                    }
                }
            }
        } catch (Exception e) {
            result = false;
            main.system.ExceptionMaster.printStackTrace(e);
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
                log(Strings.MESSAGE_PREFIX_INFO + attacked.getName()
                        + " is immune to Sneak Attacks!");
            } else {
                log(Strings.MESSAGE_PREFIX_ALERT + attacker.getNameIfKnown()
                        + " makes a Sneak Attack against " + attacked.getName());
            }
        }
        if (canCounter) {
            canCounter = attacked.canCounter(action, attack.isSneak());
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
                    // countered = tryFindCounter(attack);
                    return null;
                }
            }
        }
        if (attacker.isDead()) {
            //TODO interrupted!
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
            //TODO interrupted!
            return false;
        }
        // initializeFullModifiers(attack.isSneak(), offhand, action, ref);

        Unit attackedUnit = null;
        if (attacked instanceof Unit) {
            attackedUnit = (Unit) attacked;
        }

        Boolean dodged = false;
        if (ref.getEffect().isInterrupted()) {
            event.getRef().getEffect().setInterrupted(false);
            dodged = true;
        }

        if (!attacked.isDead())
            if (!dodged)
                if (attacked instanceof Unit) {
                    boolean parried = parryRule.tryParry(attack);
                    if (parried) {
                        attack.setParried(true);
                        //                    if (
                        //                     EventMaster.fireStandard(STANDARD_EVENT_TYPE.ATTACK_DODGED, ref)) {
                        attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_PARRY, attacker, ref);
                        attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_PARRY_SELF, attacked, ref);
                        //                    }
                        return true;
                    }
                    dodged = DefenseVsAttackRule.checkDodgedOrCrit(attack);
                }

        // BEFORE_ATTACK,
        // BEFORE_HIT
        if (!attacked.isDead())
            if (dodged == null) {
                if (!new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_HIT, ref).fire()) {
                    return false;
                }
                if (attacker.isDead()) {
                    return true; // ???
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
                    attack.setDodged(true);
                    DC_SoundMaster.playMissedSound(attacker, getAttackWeapon(ref, offhand));
                    StackingRule.actionMissed(action);
                    // ++ animation? *MISS* //TODO ++ true strike
                    action.setFailedLast(true);
                    if (checkEffectsInterrupt(attacker, attackedUnit, SPECIAL_EFFECTS_CASE.ON_DODGE, ref,
                            offhand)) {
                        return true;
                    }
                    if (checkEffectsInterrupt(attacked, attackedUnit, SPECIAL_EFFECTS_CASE.ON_DODGE_SELF, ref,
                            offhand)) {
                        return true;
                    }
                    if (canCounter) {
                        if ((!countered) || attacked.hasDoubleCounter()) {
                            // tryFindCounter(attack); TODO ?
                            return true;
                        }
                    }
                    return true;
                } else {
                    if (attacked.checkPassive(UnitEnums.STANDARD_PASSIVES.CRITICAL_IMMUNE)) {
                        log(Strings.MESSAGE_PREFIX_INFO + attacked.getName()
                                + " is immune to Critical Hits!");
                    } else {
                        log(Strings.MESSAGE_PREFIX_ALERT + attacker.getNameIfKnown()
                                + " scores a critical hit on " + attacked.getName());
                        attack.setCritical(true);

                    }
                }
            }
        attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.BEFORE_HIT, attacker, ref);

        attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.BEFORE_ATTACK, attacked, ref);
        Integer final_amount = attack.getDamage();

        //TODO REAL CALC How to calc damage w/o crit (for parry)?
        if (final_amount == Attack.DAMAGE_NOT_SET) {
            AttackCalculator calculator = new AttackCalculator(attack, false);
            final_amount = calculator.calculateFinalDamage();
        }
        // TODO different for multiDamageType
        if (Flags.isPhaseAnimsOn()) {
            //         TODO    PhaseAnimator.getInstance().initAttackAnimRawDamage(attack);
        }

        ref.setAmount(final_amount);

        if (final_amount < 0) {
            return true;
        }
        ref.setAmount(final_amount);

        DAMAGE_TYPE dmg_type = action.getActiveWeapon().getDamageType(); //ref.getDamageType();

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
        if (!attacked.isDead())
            if (attackedUnit != null)
                if (attackedUnit.getOffhandWeapon() != null) {
                    int blocked = 0;
                    if (attackedUnit.getOffhandWeapon().isShield()) {
                        if (!attack.isSneak()) {// && !isCounter) {
                            blocked = game.getArmorMaster().getShieldDamageBlocked(final_amount, attackedUnit,
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
                    //SHIELD ONLY!
                    if (blocked > 0) {
                        int durabilityLost = DurabilityRule.damageDealt(
                                blocked, attackedUnit.getOffhandWeapon(), dmg_type, attacker
                                        .getActiveWeapon(offhand), final_amount, attacked);
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
            attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_HIT, attacker, ref);
        }
        attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_ATTACK, attacked, ref, offhand);

        if (attack.isSneak()) {
            attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_SNEAK_HIT, attacker, ref);
            attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_SNEAK_HIT_SELF, attacked, ref);

            attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_SNEAK_ATTACK, attacked, ref);
            attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_SNEAK_ATTACK_SELF, attacker, ref);
            if (attack.isCritical()) {

                attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_SNEAK_CRIT_HIT, attacker, ref);
                attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_SNEAK_CRIT_SELF, attacked, ref);

                attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_SNEAK_CRIT, attacked, ref);
                attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_SNEAK_CRIT_SELF, attacker, ref);
            }
        }


        try {
            // map=
            CoatingRule.unitIsHit(attacked, attacker, offhand, action, attack, attack.getWeapon());
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        if (attackedUnit != null) {
            InjuryRule.applyInjuryRule(action);
            if (attack.isCritical()) {
                checkEffectsInterrupt(attacker, attacker, SPECIAL_EFFECTS_CASE.ON_CRIT_SELF, ref,
                        offhand);
                checkEffectsInterrupt(attackedUnit, attacker, SPECIAL_EFFECTS_CASE.ON_CRIT, ref, offhand);

                checkEffectsInterrupt(attackedUnit, attackedUnit, SPECIAL_EFFECTS_CASE.ON_CRIT_HIT_SELF, ref,
                        offhand);
                checkEffectsInterrupt(attacker, attackedUnit, SPECIAL_EFFECTS_CASE.ON_CRIT_HIT, ref, offhand);
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
        ActionAnimMaster.animate(new ActionInput(attack.getAction(),
                new AnimContext(attack.getAttacker(),
                        attack.getAttacked())));
        //        if (attack.getAnimation() != null) { TODO is it required now??
        //            if (attack.getAnimation().isStarted()) {
        //                while (!attack.getAnimation().isFinished()) {
        //                    WaitMaster.WAIT(80);
        //                }
        //            }
        //        }
    }

}
