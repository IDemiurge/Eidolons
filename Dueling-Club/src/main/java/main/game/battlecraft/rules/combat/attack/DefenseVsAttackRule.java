package main.game.battlecraft.rules.combat.attack;

import main.content.PARAMS;
import main.content.enums.GenericEnums;
import main.content.enums.entity.UnitEnums;
import main.entity.Ref;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.rules.RuleMaster;
import main.game.battlecraft.rules.RuleMaster.RULE;
import main.game.battlecraft.rules.action.WatchRule;
import main.game.battlecraft.rules.perk.FlyingRule;
import main.system.DC_Formulas;
import main.system.auxiliary.RandomWizard;
import main.system.graphics.AnimPhase;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.graphics.PhaseAnimation;
import main.system.math.MathMaster;
import main.system.math.roll.RollMaster;

/**
 * Created by JustMe on 3/12/2017.
 */
public class DefenseVsAttackRule {
    public static int getAttackValue(Attack attack) {
        return getAttackValue(attack.isOffhand(), attack.getAttacker(), attack.getAttacked(),
         attack.getAction());
    }

    public static int getDefenseValue(Attack attack) {
        return getDefenseValue(attack.getAttacker(), attack.getAttacked(), attack.getAction());
    }

    //
    public static int getDefenseValue(Unit attacker, BattleFieldObject attacked, DC_ActiveObj action) {
        int defense = attacked.getIntParam(PARAMS.DEFENSE)
         - attacker.getIntParam(PARAMS.DEFENSE_PENETRATION);
        defense = defense * (action.getIntParam(PARAMS.DEFENSE_MOD)) / 100;
        defense += action.getIntParam(PARAMS.DEFENSE_BONUS);
        if (attacked instanceof Unit)
        if (WatchRule.checkWatched((Unit) attacked, attacker)) {
            //increase defense if attacked watches attacker
            //TODO add reverse mods - 'defense when watched on attack' for trickster
            int bonus = MathMaster.applyMod(WatchRule.DEFENSE_MOD, attacked
             .getIntParam(PARAMS.WATCH_DEFENSE_MOD));
            defense += bonus;
        }
        return defense;
    }

    public static int getAttackValue(boolean offhand, Unit attacker, BattleFieldObject attacked,
                                     DC_ActiveObj action) {
        int attack = attacker.getIntParam((offhand) ? PARAMS.OFF_HAND_ATTACK : PARAMS.ATTACK);
        Boolean flying_mod = null;
        if (!action.isRanged()) {
            if (attacker.isFlying()) {
                if (!attacked.isFlying()) {
                    flying_mod = true;
                }
            }
            if (!attacker.isFlying()) {
                if (attacked.isFlying()) {
                    flying_mod = false;
                }
            }
        }
        attack = MathMaster.applyMod(attack,
         action.getIntParam(PARAMS.ATTACK_MOD));
        attack += action.getIntParam(PARAMS.ATTACK_BONUS);

        if (flying_mod != null) {
            int bonus = FlyingRule.getAttackBonus(attack, flying_mod);
            attack += bonus;
        }

        if (WatchRule.checkWatched(attacker, attacked)) {
            //increase attack if attacker watches attacked
            int bonus = MathMaster.applyMod(WatchRule.ATTACK_MOD, attacker
             .getIntParam(PARAMS.WATCH_ATTACK_MOD));
            attack += bonus;
        }

        return attack;
    }

    public static int getMissChance(int attack, int defense, DC_ActiveObj action) {
        return getChance(action, action.getOwnerObj(), null, attack, defense, false);
    }

    public static int getCritChance(int attack, int defense, DC_ActiveObj action) {
        return getChance(action, action.getOwnerObj(), null, attack, defense, true);
    }

    // returns true if dodged, false if critical, otherwise null
    public static Boolean checkDodgedOrCrit(Attack attack) {
        return checkDodgedOrCrit(attack.getAttacker(), attack.getAttacked(), attack.getAction(),
         attack.getRef(), attack.isOffhand(), attack.getAnimation());
    }

    public static Boolean checkDodgedOrCrit(Unit attacker, BattleFieldObject attacked,
                                            DC_ActiveObj action, Ref ref,
                                            boolean offhand
     , PhaseAnimation animation

    ) {
        return checkDodgedOrCrit(attacker, attacked, action, ref,
         offhand, animation, true);
    }

    public static Boolean checkDodgedOrCrit(Unit attacker, BattleFieldObject attacked,
                                            DC_ActiveObj action, Ref ref,
                                            boolean offhand
     , PhaseAnimation animation
     , boolean logged
    ) {
        if (attacked.checkPassive(UnitEnums.STANDARD_PASSIVES.IMMATERIAL)) {
            if (!attacker.checkPassive(UnitEnums.STANDARD_PASSIVES.IMMATERIAL)) {
                if (!RollMaster.roll(GenericEnums.ROLL_TYPES.IMMATERIAL, ref)) {
                    return true; // TODO ANIM
                }
            }
        }

        boolean crit = false;
        int attack = getAttackValue(offhand, attacker, attacked, action);
        int defense = getDefenseValue(attacker, attacked, action);
        float diff = defense - attack;
        if (diff < 0) {
            crit = true;
        }
        int chance = getChance(action, attacker, attacked, attack, defense, crit);
        if (logged) {
            main.system.auxiliary.log.LogMaster.log(1, ""
                    + (crit ? "...chance for critical strike: " : "..." + attacked.getName()
                    + "'s chance to dodge: ") + String.valueOf(chance) + "%");
        }
        boolean result = RandomWizard.chance(chance);
        if (isCRIT_TEST()) {
            result = true;
            crit = true;
        }
        if (result) {
            if (crit) {
                if (animation != null) {
                    animation.addPhase(new AnimPhase(PHASE_TYPE.ATTACK_CRITICAL, chance));
                }
                return false;
            } else {
                if (animation != null) {
                    animation.addPhase(new AnimPhase(PHASE_TYPE.ATTACK_DODGED, chance));
                }
                return true;
            }
        }
        chance = action.getIntParam(PARAMS.AUTO_CRIT_CHANCE);
        if (chance > 0) {
            if (!RandomWizard.chance(chance)) {
                return null;
            }
            animation.addPhase(new AnimPhase(PHASE_TYPE.ATTACK_CRITICAL, chance, true));
            return false;
        }
        return null;
    }

    private static int getChance(DC_ActiveObj action, Unit attacker,
                                 BattleFieldObject attacked, int attack, int defense, boolean critOrDodge) {
        int diff = defense - attack;
        // first preCheck ARITHMETIC difference...
        diff = Math.abs(diff)
         - ((critOrDodge) ? DC_Formulas.ATTACK_DMG_INCREASE_LIMIT
         : DC_Formulas.DEFENSE_DMG_DECREASE_LIMIT);
        if (diff <= 0) {
            diff = 0;
        }
        // add PROPORTION BASED
        diff += getProportionBasedChance(attack, defense, critOrDodge);

        float mod = (critOrDodge) ? DC_Formulas.ATTACK_CRIT_CHANCE :
         DC_Formulas.DEFENSE_DODGE_CHANCE;
        int chance = Math.round(diff * mod);

        if (critOrDodge) {
            chance += action.getFinalBonusParam(PARAMS.AUTO_CRIT_CHANCE);
            // TODO chance modifiers?
        } else {
            if (attacker.checkPassive(UnitEnums.STANDARD_PASSIVES.TRUE_STRIKE)) {
                chance = 0;
            }
            if (attacked != null) {
                chance += attacked.getIntParam(PARAMS.EVASION);
            }
            chance += -action.getFinalBonusParam(PARAMS.ACCURACY);

        }

        chance = Math.min(100, chance);
        if (action. getGame().getCombatMaster().isChancesOff()) {
            RULE rule = (critOrDodge) ? RULE.CRITICAL_ATTACK : RULE.DODGE;
            if (RuleMaster.isRuleTestOn(rule)|| chance > 50)
                chance= 100;
            else chance= 0 ;

        }
        return chance;
    }

    private static boolean isCRIT_TEST() {
        return false;
    }

    public static float getProportionBasedChance(int attack, int defense, boolean crit) {
        if (attack <= 0) {
            defense += -attack;
            attack = 1;
        }
        if (defense <= 0) {
            attack += -defense;
            defense = 1;
        }
        float advantage = new Float(new Float(attack) / new Float(defense));
        if (Math.abs(advantage) < 1) {
            advantage = new Float(new Float(defense) / new Float(attack));
        }
        Float chance;
        if (crit) {
            chance = new Float(Math.min(DC_Formulas.ATTACK_PROPORTION_CRIT_MAX, Math
             .sqrt(DC_Formulas.ATTACK_PROPORTION_CRIT_SQRT_BASE_MULTIPLIER * advantage)));
        } else {
            chance = new Float(Math.min(DC_Formulas.DEFENSE_PROPORTION_CRIT_MAX, Math
             .sqrt(DC_Formulas.DEFENSE_PROPORTION_CRIT_SQRT_BASE_MULTIPLIER
              * advantage)));
        }
        return chance;
    }

    public static int getCritOrDodgeChance(DC_ActiveObj entity, BattleFieldObject target) {
        Attack attack = DC_AttackMaster.getAttackFromAction(entity);
        attack.setAttacked( target);
        int atk = getAttackValue(attack);
        int def = getDefenseValue(attack);
        return atk < def ? -getMissChance(atk, def, entity) : getCritChance(atk, def, entity);
    }
}
