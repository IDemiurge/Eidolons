package main.game.battlefield;

import main.ability.conditions.special.SneakCondition;
import main.ability.effects.AttackEffect;
import main.ability.effects.DealDamageEffect;
import main.ability.effects.Effect;
import main.ability.effects.Effect.SPECIAL_EFFECTS_CASE;
import main.content.CONTENT_CONSTS.*;
import main.content.DC_ContentManager;
import main.content.PARAMS;
import main.content.properties.G_PROPS;
import main.elements.conditions.ConditionImpl;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.ActiveObj;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_WeaponObj;
import main.entity.obj.top.DC_ActiveObj;
import main.game.DC_Game;
import main.game.battlefield.attack.Attack;
import main.game.event.Event;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.rules.ForceRule;
import main.rules.mechanics.*;
import main.system.DC_Formulas;
import main.system.DC_SoundMaster;
import main.system.ai.logic.target.EffectMaster;
import main.system.auxiliary.LogMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.graphics.AnimPhase;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.graphics.Animation;
import main.system.graphics.AttackAnimation;
import main.system.math.roll.RollMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.SOUNDS;
import main.system.text.EntryNodeMaster.ENTRY_TYPE;
import main.system.text.LogEntryNode;
import main.system.threading.WaitMaster;

import java.util.LinkedList;
import java.util.List;

public class DC_AttackMaster {
    private static ConditionImpl sneakCondition;
    private DC_Game game;

    // private static boolean precalc;

    // private boolean sneak;
    // private boolean offhand;

    public DC_AttackMaster(DC_Game game) {
        this.game = game;
    }

    public static AttackEffect getAttackEffect(ActiveObj action) {
        AttackEffect effect = (AttackEffect) EffectMaster.getEffectsOfClass((DC_ActiveObj) action,
                AttackEffect.class).get(0);
        return effect;
    }

    public static Attack getAttackFromAction(DC_ActiveObj attackAction) {
        return getAttackEffect(attackAction).getAttack();
    }

    public static List<Damage> precalculateRawDamage(Attack attack) {

        List<Damage> list = new LinkedList<>();
        list.add(new Damage(attack.getDamageType(), attack.getDamage(), attack.getAttacked(),
                attack.getAttacker()));

        List<Effect> effects = new LinkedList<>();
        if (attack.getWeapon().getSpecialEffects() != null) {
            if (attack.getWeapon().getSpecialEffects().get(SPECIAL_EFFECTS_CASE.ON_ATTACK) != null)
                effects.add(attack.getWeapon().getSpecialEffects().get(
                        SPECIAL_EFFECTS_CASE.ON_ATTACK));
        }
        if (attack.getAttacker().getSpecialEffects() != null) {
            if (attack.getAttacker().getSpecialEffects().get(SPECIAL_EFFECTS_CASE.ON_ATTACK) != null)
                effects.add(attack.getAttacker().getSpecialEffects().get(
                        SPECIAL_EFFECTS_CASE.ON_ATTACK));
        }
        for (Effect e : effects) {
            // TODO ++ PARAM MOD
            for (Effect dmgEffect : EffectMaster.getEffectsOfClass(e, DealDamageEffect.class)) {
                int amount = dmgEffect.getFormula().getInt(attack.getRef());
                DAMAGE_TYPE damageType = ((DealDamageEffect) dmgEffect).getDamage_type();
                list.add(new Damage(damageType, amount, attack.getAttacked(), attack.getAttacker()));
            }
        }
        // TODO display target's ON_HIT? PARAM_MODS?

        return list;
    }

    public static int getAttackValue(Attack attack) {
        return getAttackValue(attack.isOffhand(), attack.getAttacker(), attack.getAttacked(),
                attack.getAction());
    }

    public static int getDefenseValue(Attack attack) {
        return getDefenseValue(attack.getAttacker(), attack.getAttacked(), attack.getAction());
    }

    public static Integer calculateAttackDamage(Attack attack) {
        return calculateAttackDamage(attack, attack.isCritical(), attack.isSneak(), attack
                .isOffhand(), attack.getRef(), attack.getAction(), attack.getAttacked(), attack
                .getAttacker(), attack.isCounter());
    }

    public static Integer calculateAttackDamage(Attack attack, boolean precalc) {
        AttackCalculator calculator = new AttackCalculator(attack, true);
        return calculator.calculateFinalDamage();
        // Integer result = 0;
        // if (precalc)
        // initializeFullModifiers(attack.isSneak(), attack.isOffhand(),
        // attack.getAction(),
        // attack.getRef()); // TODO side-effect?
        // try {
        // result = calculateAttackDamage(attack);
        // } catch (Exception e) {
        // e.printStackTrace();
        // } finally {
        // setPrecalc(false);
        // }
        // return result;
    }

    public static Integer calculateAttackDamage(Attack attack, boolean critical, boolean sneak,
                                                boolean offhand, Ref ref, DC_ActiveObj action, DC_HeroObj attacked,
                                                DC_HeroObj attacker, boolean counter) {
        AttackCalculator calculator = new AttackCalculator(attack, false);
        return calculator.calculateFinalDamage();
        // Integer amount = 0;
        // if (ref != null)
        // amount = ref.getAmount();
        // if (amount == null)
        // amount = attacker.getIntParam(PARAMS.BASE_DAMAGE);
        // amount = applyDamageMods(attack, amount, action, ref, counter,
        // offhand, sneak);
        // // TODO attack/armor penetration bonuses
        //
        // if (critical) {
        // amount += getCriticalDamageBonus(attack, amount, attacker, attacked,
        // action, offhand);
        // }
        // // if (!attacked.checkPassive(STANDARD_PASSIVES.IMMATERIAL)
        // // && !attacker.checkPassive(STANDARD_PASSIVES.IMMATERIAL))
        // else
        // amount += getAttackDefenseDamageMod(attack, amount, attacker,
        // attacked, action, offhand);
        // return amount;
    }

    public static int getDefenseValue(DC_HeroObj attacker, DC_HeroObj attacked, DC_ActiveObj action) {
        int defense = attacked.getIntParam(PARAMS.DEFENSE)
                - attacker.getIntParam(PARAMS.DEFENSE_PENETRATION);
        defense = defense * (action.getIntParam(PARAMS.DEFENSE_MOD)) / 100;
        defense += action.getIntParam(PARAMS.DEFENSE_BONUS);
        if (WatchRule.checkWatched(attacked, attacker)) {
            defense = defense
                    * (100 + 2 * WatchRule.DEFENSE_MOD + attacker
                    .getIntParam(PARAMS.WATCH_DEFENSE_MOD)) / 100;
        }
        return defense;
    }

    public static int getAttackValue(boolean offhand, DC_HeroObj attacker, DC_HeroObj attacked,
                                     DC_ActiveObj action) {
        int attack = attacker.getIntParam((offhand) ? PARAMS.OFF_HAND_ATTACK : PARAMS.ATTACK);
        Boolean flying_mod = null;
        if (!action.isRanged()) {
            if (attacker.isFlying())
                if (!attacked.isFlying()) {
                    flying_mod = true;
                }
            if (!attacker.isFlying())
                if (attacked.isFlying()) {
                    flying_mod = false;
                }
        }
        attack = attack * (action.getIntParam(PARAMS.ATTACK_MOD)) / 100;
        attack += action.getIntParam(PARAMS.ATTACK_BONUS);

        if (flying_mod != null)
            attack = FlyingRule.getModifiedAttackValue(attack, flying_mod);

        if (WatchRule.checkWatched(attacker, attacked)) {
            attack = attack
                    * (100 + 2 * WatchRule.ATTACK_MOD + attacker
                    .getIntParam(PARAMS.WATCH_ATTACK_MOD)) / 100;
        }

        return attack;
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

    public static DC_WeaponObj getAttackWeapon(Ref ref) {
        return getAttackWeapon(ref, ref.getActive().isOffhand());
    }

    private static int initializeDamageModifiers(int amount, boolean offhand, DC_HeroObj unit,
                                                 DC_WeaponObj weapon) {
        amount += weapon.getDamageModifiers();
        amount += weapon.getIntParam(PARAMS.DAMAGE_BONUS);
        int hero_dmg_mod = unit.getIntParam((offhand) ? PARAMS.OFFHAND_DAMAGE_MOD
                : PARAMS.DAMAGE_MOD);
        if (hero_dmg_mod == 0)
            hero_dmg_mod = 100;
        Integer weapon_mod = weapon.getIntParam(PARAMS.DAMAGE_MOD);
        if (weapon_mod == 0)
            weapon_mod = 100;
        int dmg_mod = weapon_mod * hero_dmg_mod / 100;
        amount = amount * dmg_mod / 100;

        return amount;
    }

    public static Integer getUnitAttackDamage(DC_HeroObj unit, boolean offhand) {
        int amount = unit.getIntParam(PARAMS.BASE_DAMAGE);
        DC_WeaponObj weapon = unit.getWeapon(offhand);
        if (weapon == null)
            weapon = unit.getNaturalWeapon(offhand);
        if (weapon == null)
            return (offhand) ? 0 : amount;
        amount = initializeDamageModifiers(amount, offhand, unit, weapon);
        return amount;

    }

    public static boolean checkSneak(Ref ref) {
        if (sneakCondition == null)
            sneakCondition = new SneakCondition();
        return sneakCondition.check(ref);
    }

    private static boolean checkWeapon(Ref ref) {
        return ref.getObj(KEYS.WEAPON) != null;
    }

    public boolean attack(Attack attack) {
        Boolean doubleAttack = attack.isDouble();
        return attack(attack, attack.getRef(), attack.isFree(), attack.isCanCounter(), attack
                        .getOnHit(), attack.getOnKill(), attack.isOffhand(), attack.isCounter(),
                doubleAttack);

    }

    public boolean attack(Attack attack, Ref ref, boolean free, boolean canCounter, Effect onHit,
                          Effect onKill, boolean offhand, boolean counter, Boolean doubleAttack) {
        boolean result = attack(attack, ref, free, canCounter, onHit, onKill, offhand, counter);
        if (doubleAttack == null)
            return result;
        if (doubleAttack) {
            result = attack(attack, ref, free, canCounter, onHit, onKill, offhand, counter);
        }
        return result;
    }

    public boolean attack(Attack attack, Ref ref, boolean free, boolean canCounter, Effect onHit,
                          Effect onKill, boolean offhand, boolean counter) {
        ENTRY_TYPE type = ENTRY_TYPE.ATTACK;
        boolean extraAttack = true;
        if (attack.getAction().isCounterMode())
            type = ENTRY_TYPE.COUNTER_ATTACK;
        else if (attack.getAction().isInstantMode())
            type = ENTRY_TYPE.INSTANT_ATTACK;
        else if (attack.getAction().isAttackOfOpportunityMode())
            type = ENTRY_TYPE.ATTACK_OF_OPPORTUNITY;
        else
            extraAttack = false;
        LogEntryNode entry = game.getLogManager().newLogEntryNode(type,
                attack.getAttacker().getName(), attack.getAttacked().getName(), attack.getAction());
        Boolean result = false;
        try {
            attack.setSneak(checkSneak(ref));

            AttackAnimation animation = null;
            if (attack.getAction().getAnimation() instanceof AttackAnimation)
                animation = (AttackAnimation) attack.getAction().getAnimation();
            if (animation == null)
                animation = new AttackAnimation(attack);
            if (ref.getGroup() != null) {
                // animation = new MultiAttackAnimation(attack, ref.getGroup());
            }
            // (AttackAnimation) game.getAnimationManager().getAnimation(
            // attack.getAction().getAnimationKey());
            attack.setAnimation(animation);
            // attack.getAnimation().setAttack(attack);
            // new AttackAnimation(attack);
            game.getAnimationManager().newAnimation(animation);
            if (entry != null)
                entry.setLinkedAnimation(animation);
            if (entry.getType() == ENTRY_TYPE.INSTANT_ATTACK) {
                // TODO add triggering action!
            }
            result = attackNow(attack, ref, free, canCounter, onHit, onKill, offhand, counter);
            boolean countered = false;
            if (result == null) { // first strike - update?
                ActiveObj action = tryCounter(attack, false);
                AttackEffect effect = getAttackEffect(action);
                waitForAttackAnimation(effect.getAttack());
                attackNow(attack, ref, free, false, onHit, onKill, offhand, counter);
                countered = true;
                result = true;
            }

            animation.start();
            if ((!countered) || attack.getAttacker().hasDoubleCounter())
                if (canCounter)
                    if (!counter) {
                        waitForAttackAnimation(attack);
                        tryCounter(attack);
                    }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        } finally {
            if (!extraAttack)
                game.getLogManager().doneLogEntryNode(ENTRY_TYPE.ACTION);
            game.getLogManager().doneLogEntryNode(type);

        }
        return result;
    }

    public Boolean attackNow(Attack attack, Ref ref, boolean free, boolean canCounter,
                             Effect onHit, Effect onKill, boolean offhand, boolean counter) {
        if (!(ref.getTargetObj() instanceof DC_HeroObj))
            return true;
        // Animation animation =
        // game.getAnimationManager().getAnimation(attack.getAction().getAnimationKey());
        DC_ActiveObj action = (DC_ActiveObj) ref.getObj(KEYS.ACTIVE);
        if (action.checkProperty(G_PROPS.ACTION_TAGS, "" + ACTION_TAGS.OFF_HAND))
            offhand = true;
        if (!offhand)
            if (action.isRanged())
                if (!action.isThrow())
                    if (getAttackWeapon(ref, true).isRanged())
                        offhand = true;

        DC_HeroObj attacked = (DC_HeroObj) ref.getTargetObj();
        DC_HeroObj attacker = (DC_HeroObj) ref.getSourceObj();
        if (attack.isSneak())
            if (attacked.checkPassive(STANDARD_PASSIVES.SNEAK_IMMUNE)) {
                attack.setSneak(false);
                log(StringMaster.MESSAGE_PREFIX_INFO + attacked.getName()
                        + " is immune to Sneak Attacks!");
            } else {
                log(StringMaster.MESSAGE_PREFIX_ALERT + attacker.getNameIfKnown()
                        + " makes a Sneak Attack against " + attacked.getName());
            }
        if (canCounter)
            if (!attacked.canCounter(action, attack.isSneak()))
                canCounter = false;

        LogMaster.log(LogMaster.ATTACKING_DEBUG, attacker.getNameIfKnown() + " attacks "
                + attacked.getName());
        // } ====> Need a common messaging interface for actions/costs

        String damage_mods = "";
        // if (sneak)
        // damage_mods+=DAMAGE_MODIFIER.SNEAK;
        ref.setValue(KEYS.DAMAGE_MODS, damage_mods);
        boolean countered = false;
        if (canCounter)
            if (attacked.hasFirstStrike() && !attacker.hasFirstStrike()) {
                if (!attacker.hasNoRetaliation()) {
                    // countered = tryCounter(attack);
                    return null;
                }
            }
        if (attacker.isDead()) {
            attack.getAnimation().addPhase(new AnimPhase(PHASE_TYPE.INTERRUPTED, ref));
            return false;
        }
        SoundMaster.playEffectSound(SOUNDS.ATTACK, attacker); // TODO revamp

        if (action.isRanged())
            DC_SoundMaster.playRangedAttack(getAttackWeapon(ref, offhand));

        int amount = attacker.getIntParam(PARAMS.BASE_DAMAGE);
        ref.setAmount(amount);
        Event event = new Event(STANDARD_EVENT_TYPE.UNIT_IS_BEING_ATTACKED, ref);
        if (!event.fire()) {
            attack.getAnimation().addPhase(new AnimPhase(PHASE_TYPE.INTERRUPTED, ref));
            return false;
        }
        // initializeFullModifiers(attack.isSneak(), offhand, action, ref);
        Boolean dodged = false;
        while (true) {

            if (ref.getEffect().isInterrupted()) {
                event.getRef().getEffect().setInterrupted(false);
                dodged = true;
            }
            if (dodged) {
                break;
            }
            dodged = checkDodgedOrCrit(attack);
            break;
        }

        // BEFORE_ATTACK,
        // BEFORE_HIT
        if (dodged == null) {
            if (!new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_HIT, ref).fire())
                return false;
            if (attacker.isDead())
                return true;
            if (attacked.isDead()) {
                if (onKill != null)
                    onKill.apply(ref);
                attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_DEATH, attacker, ref);
                return true;
            }
        } else {
            if (dodged) {
                log(attacked.getName() + " has dodged an attack from " + attacker.getNameIfKnown());
                DC_SoundMaster.playMissedSound(attacker, getAttackWeapon(ref, offhand));
                StackingRule.actionMissed(action);
                // ++ animation? *MISS* //TODO ++ true strike

                if (checkEffectsInterrupt(attacked, attacker, SPECIAL_EFFECTS_CASE.ON_DODGE, ref,
                        offhand))
                    return true;
                if (canCounter)
                    if ((!countered) || attacked.hasDoubleCounter())
                        // tryCounter(attack);
                        return true;
            } else {
                if (attacked.checkPassive(STANDARD_PASSIVES.CRITICAL_IMMUNE)) {
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
        if (!checkDeathEffects(attacked, attacker, onKill, ref, SPECIAL_EFFECTS_CASE.ON_DEATH))
            return true;
        attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.BEFORE_ATTACK, attacked, ref);
        if (!checkDeathEffects(attacker, attacked, onKill, ref, SPECIAL_EFFECTS_CASE.ON_DEATH))
            return true;
        Integer final_amount = attack.getDamage();
        if (final_amount == Attack.DAMAGE_NOT_SET)
            final_amount = calculateAttackDamage(attack);
        // TODO different for multiDamageType
        List<Damage> rawDamage = precalculateRawDamage(attack);
        attack.setRawDamage(rawDamage);
        attack.getAnimation().addPhase(new AnimPhase(PHASE_TYPE.PRE_ATTACK, attack, rawDamage), 0);

        ref.setAmount(final_amount);

        if (final_amount < 0) {
            return true;
        }
        ref.setAmount(final_amount);
        DAMAGE_TYPE dmg_type = ref.getDamageType();

        if (attack.isCritical())
            if (attacker.checkPassive(STANDARD_PASSIVES.CLEAVING_CRITICALS)) {
                // TODO add default cleave?
                CleaveRule.addCriticalCleave(attacker);
                dmg_type = DAMAGE_TYPE.SLASHING;
            }

        if (dmg_type == null)
            dmg_type = action.getDamageType();
        if (dmg_type == null) {
            if (!checkWeapon(ref)) {
                dmg_type = attacker.getDamageType();
            } else {
                dmg_type = getAttackWeapon(ref, offhand).getDamageType();
            }
        }

        attack.setDamageType(dmg_type);
        if (attack.getDamage() == Attack.DAMAGE_NOT_SET)
            attack.setDamage(final_amount);
        boolean parried = tryParry(attack);
        if (parried) {
            attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_PARRY, attacker, ref);
            attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_PARRY_SELF, attacked, ref);
            return true;
        }
        if (!new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_ATTACKED, ref).fire())
            return false;
        if (!new Event(STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_HIT, ref).fire())
            return false;

        if (!attack.isSneak() && canBlock(attacked) && !counter) {
            int blocked = game.getArmorMaster().getShieldDamageBlocked(final_amount, attacked,
                    attacker, action, getAttackWeapon(ref, attack.isOffhand()),
                    attack.getDamageType());
            final_amount -= blocked;
            if (blocked > 0) {
                Ref REF = ref.getCopy();
                REF.setAmount(blocked);
                if (checkEffectsInterrupt(attacked, attacker, SPECIAL_EFFECTS_CASE.ON_SHIELD_BLOCK,
                        REF, offhand))
                    return true;
                if (checkEffectsInterrupt(attacker, attacked,
                        SPECIAL_EFFECTS_CASE.ON_SHIELD_BLOCK_SELF, REF, offhand))
                    return true;
            }

        }
        // armor penetration?
        attack.setDamage(final_amount);

        int damageDealt = DamageMaster.dealDamageOfType(dmg_type, attacked, ref, final_amount);
        attack.damageDealt(damageDealt);

        attack.reset();
        if (attacked.isDead()) {
            if (!attack.isTriggered())
                game.getRules().getCleaveRule().apply(ref, attack);
        }

        if (onHit != null)
            onHit.apply(ref);
        if (!action.isRanged())
            attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_HIT, attacker, ref); // e.g.
        // spikes
        // map=
        attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_ATTACK, attacked, ref, offhand); // e.g.
        // TODO

        try {
            // map=
            CoatingRule.unitIsHit(attacked, attacker, offhand, action, attack, attack.getWeapon());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ForceRule.applyForceEffects(action);
        InjuryRule.applyInjuryRule(action);
        if (attack.isCritical()) {
            checkEffectsInterrupt(attacked, attacker, SPECIAL_EFFECTS_CASE.ON_CRIT_SELF, ref,
                    offhand);
            checkEffectsInterrupt(attacker, attacked, SPECIAL_EFFECTS_CASE.ON_CRIT, ref, offhand);
        }
        if (attacked.isDead()) {
            if (onKill != null)
                onKill.apply(ref);
            attacked.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_DEATH, attacker, ref); // e.g.
            // retribution
            if (attacker.isDead())
                return true;
            // attacker.applySpecialEffects(SPECIAL_EFFECTS_CASE.ON_KILL,
            // attacker, ref); // already applied in DC_UnitObj.kill()

        }

        // if (canCounter)
        // if ((!countered) || attacker.hasDoubleCounter())
        // tryCounter(attack);

        return true;

    }

    private boolean checkEffectsInterrupt(DC_HeroObj target, DC_HeroObj source,
                                          SPECIAL_EFFECTS_CASE case_type, Ref REF, boolean offhand) {
        source.applySpecialEffects(case_type, target, REF, offhand);
        if (target.isDead())
            return true;
        // if (attacker)
        return false;
    }

    private boolean checkDeathEffects(DC_HeroObj source, DC_HeroObj target, Effect onKill, Ref ref,
                                      SPECIAL_EFFECTS_CASE CASE) {
        if (target.isDead())
            return false;
        if (onKill != null)
            onKill.apply(ref);
        target.applySpecialEffects(CASE, source, ref);
        return true;
    }

    // precalculateRawDamage
    private boolean canParry(Attack attack) {
        // if (!RuleMaster.isParryOn())return false;
        if (attack.getAttacked().getIntParam(PARAMS.PARRY_CHANCE) <= 0)
            return false;
        if (attack.isSneak())
            return false;
        if (attack.isCritical())
            return false;
        if (attack.isRanged())
            return false;
        if (attack.getWeapon().getWeaponType() == WEAPON_TYPE.NATURAL)
            return false;
        if (attack.getWeapon().getWeaponType() == WEAPON_TYPE.BLUNT)
            return false;
        // if (attack.getWeapon().getWeaponSize() == WEAPON_SIZE.TINY)
        // {
        // TODO
        DC_WeaponObj parryWeapon = attack.getAttacked().getActiveWeapon(false);
        if (Math.abs(DC_ContentManager.compareSize(parryWeapon.getWeaponSize(), attack.getWeapon()
                .getWeaponSize())) > 2) {
            if (attack.getAttacked().checkDualWielding()) {

            } else
                return false;
        }
        // }
        return true;
    }

    private boolean tryParry(Attack attack) {
        // TODO could return and Integer for dmg reduction or -1 if all
        // absorbed!
        if (!isParryTest())
            if (!canParry(attack))
                return false;

        int attackValue = getAttackValue(attack);
        int defenseValue = getDefenseValue(attack);

        float chance = getProportionBasedChance(attackValue, defenseValue, false);
        chance += attack.getAttacked().getIntParam(PARAMS.PARRY_CHANCE);
        chance += -attack.getAttacker().getIntParam(PARAMS.PARRY_PENETRATION);
        Integer chanceRounded = Math.round(chance);

        game.getLogManager().newLogEntryNode(ENTRY_TYPE.PARRY, attack.getAttacked().getName(),
                attack.getAction().getName(), attack.getAttacker().getName(),
                chanceRounded.toString());
        if (!RandomWizard.chance(chanceRounded)) {
            log(attack.getAttacked().getName() + " fails to parry " + attack.getAction().getName()
                    + " from " + attack.getAttacker().getNameIfKnown()
                    + StringMaster.wrapInParenthesis(chanceRounded + "%"));
            game.getLogManager().doneLogEntryNode();
            if (!isParryTest())
                return false;
        }
        DC_HeroObj attacked = attack.getAttacked();
        DC_HeroObj attacker = attack.getAttacker();
        boolean dual = false;
        if (attacked.checkDualWielding()) {
            dual = true;
        }
        int damage = attack.getDamage();
        log(attack.getAttacked().getName() + " parries " + attack.getAction().getName() + " from "
                + attack.getAttacker().getNameIfKnown()
                + StringMaster.wrapInParenthesis(chanceRounded + "%") + ", deflecting " + damage
                + " " + attack.getDamageType() + " damage");
        int mod = DC_Formulas.DEFAULT_PARRY_DURABILITY_DAMAGE_MOD;
        if (dual) {
            mod /= 2;
        }
        AnimPhase animPhase = new AnimPhase(PHASE_TYPE.PARRY, chanceRounded);
        int durabilityLost = attacked.getWeapon(false).reduceDurabilityForDamage(damage, damage,
                mod, false);
        animPhase.addArg(durabilityLost);
        if (dual) {
            durabilityLost += attacked.getWeapon(true).reduceDurabilityForDamage(damage, damage,
                    mod, false);
            animPhase.addArg(durabilityLost);
        }

        // if () BROKEN
        // return false
        mod = DC_Formulas.DEFAULT_PARRY_DURABILITY_DAMAGE_MOD;
        durabilityLost = durabilityLost * mod / 100;
        attacker.getActiveWeapon(attack.isOffhand()).reduceDurability(durabilityLost);
        animPhase.addArg(durabilityLost);

        attack.getAnimation().addPhase(animPhase);

        // game.getLogManager().doneLogEntryNode(); ???
        return true;

    }

    private boolean isParryTest() {
        return false;
    }

    // returns true if dodged, false if critical, otherwise null
    private Boolean checkDodgedOrCrit(Attack attack) {
        return checkDodgedOrCrit(attack.getAttacker(), attack.getAttacked(), attack.getAction(),
                attack.getRef(), attack.isOffhand(), attack.getAnimation());
    }

    private Boolean checkDodgedOrCrit(DC_HeroObj attacker, DC_HeroObj attacked,
                                      DC_ActiveObj action, Ref ref, boolean offhand, Animation animation) {
        if (attacked.checkPassive(STANDARD_PASSIVES.IMMATERIAL))
            if (!attacker.checkPassive(STANDARD_PASSIVES.IMMATERIAL)) {
                if (!RollMaster.roll(ROLL_TYPES.IMMATERIAL, ref))
                    return true; // TODO ANIM
            }

        int attack = getAttackValue(offhand, attacker, attacked, action);
        int defense = getDefenseValue(attacker, attacked, action);
        float diff = defense - attack;
        boolean crit = false;
        if (diff < 0)
            crit = true;

        // first check ARITHMETIC difference...
        diff = Math.abs(diff)
                - ((crit) ? DC_Formulas.ATTACK_DMG_INCREASE_LIMIT
                : DC_Formulas.DEFENSE_DMG_DECREASE_LIMIT);
        if (diff <= 0) {
            diff = 0;
        }
        // add PROPORTION BASED
        diff += getProportionBasedChance(attack, defense, crit);

        float mod = (crit) ? DC_Formulas.ATTACK_CRIT_CHANCE : DC_Formulas.DEFENSE_DODGE_CHANCE;
        int chance = Math.round(diff * mod);

        if (crit) {
            // TODO chance modifiers?
        } else {
            if (attacker.checkPassive(STANDARD_PASSIVES.TRUE_STRIKE))
                chance = 0;
            chance += attacked.getIntParam(PARAMS.EVASION) - attacker.getIntParam(PARAMS.ACCURACY);

        }

        chance = Math.min(100, chance);
        log(""
                + (crit ? "...chance for critical strike: " : "..." + attacked.getName()
                + "'s chance to dodge: ") + String.valueOf(chance) + "%");
        boolean result = RandomWizard.chance(chance);
        // if (crit)
        // result = !result;
        if (isCRIT_TEST()) {
            result = true;
            crit = true;
        }
        if (result)
            if (crit) {
                animation.addPhase(new AnimPhase(PHASE_TYPE.ATTACK_CRITICAL, chance));
                return false;
            } else {
                animation.addPhase(new AnimPhase(PHASE_TYPE.ATTACK_DODGED, chance));
                return true;
            }
        chance = action.getIntParam(PARAMS.AUTO_CRIT_CHANCE);
        if (chance > 0) {
            if (!RandomWizard.chance(chance))
                return null;
            animation.addPhase(new AnimPhase(PHASE_TYPE.ATTACK_CRITICAL, chance, true));
            return false;
        }
        return null;
    }

    private boolean isCRIT_TEST() {
        return false;
    }

    private float getProportionBasedChance(int attack, int defense, boolean crit) {
        if (attack <= 0) {
            defense += -attack;
            attack = 1;
        }
        if (defense <= 0) {
            attack += -defense;
            defense = 1;
        }
        float advantage = new Float(new Float(attack) / new Float(defense));
        if (Math.abs(advantage) < 1)
            advantage = new Float(new Float(defense) / new Float(attack));
        if (crit) {
            return new Float(Math.min(DC_Formulas.ATTACK_PROPORTION_CRIT_MAX, Math
                    .sqrt(DC_Formulas.ATTACK_PROPORTION_CRIT_SQRT_BASE_MULTIPLIER * advantage)));
        } else {
            return new Float(Math.min(DC_Formulas.DEFENSE_PROPORTION_CRIT_MAX, Math
                    .sqrt(DC_Formulas.DEFENSE_PROPORTION_CRIT_SQRT_BASE_MULTIPLIER * advantage)));
        }
    }

    // similarly for parry!
    private boolean canBlock(DC_HeroObj attacked) {
        // if (attacked.getIntParam(PARAMS.BLOCK_CHANCE) <= 0)
        // return false; not a chance!
        try {
            return attacked.getSecondWeapon().isShield();
        } catch (Exception e) {
            return false;
        }
    }

    private ActiveObj tryCounter(Attack attack) {
        return tryCounter(attack, true);
    }

    private ActiveObj tryCounter(Attack attack, boolean checkAnimationFinished) {
        if (checkAnimationFinished)
            if (attack.getAnimation() != null)
                waitForAttackAnimation(attack);

        ActiveObj counter = null;
        if (attack.isCanCounter())
            if (!attack.isCounter()) // TODO
                if (attack.getAttacked().canCounter(attack.getAction()))
                    counter = game.getActionManager().activateCounterAttack(attack.getAction(),
                            attack.getAttacked());

        return counter;

    }

    private void waitForAttackAnimation(Attack attack) {
        if (attack.getAnimation() != null)
            if (attack.getAnimation().isStarted())
                while (!attack.getAnimation().isFinished()) { // TODO limit?
                    WaitMaster.WAIT(80);
                }
    }

}
