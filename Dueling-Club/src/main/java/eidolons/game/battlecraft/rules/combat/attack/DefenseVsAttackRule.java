package eidolons.game.battlecraft.rules.combat.attack;

import eidolons.content.PARAMS;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.RuleKeeper;
import eidolons.game.battlecraft.rules.RuleKeeper.RULE;
import eidolons.game.battlecraft.rules.action.WatchRule;
import eidolons.game.battlecraft.rules.perk.FlyingRule;
import eidolons.game.core.EUtils;
import eidolons.libgdx.stage.GuiStage;
import eidolons.system.DC_Formulas;
import eidolons.system.math.roll.RollMaster;
import main.content.enums.GenericEnums;
import main.content.enums.entity.UnitEnums;
import main.entity.Ref;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.math.MathMaster;
import main.system.text.LogManager;

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
    public static int getDefenseValue(BattleFieldObject attacker, BattleFieldObject attacked, DC_ActiveObj action) {
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

    public static int getAttackValue(boolean offhand, BattleFieldObject attacker, BattleFieldObject attacked,
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
        if (attacker instanceof Unit)
            if (WatchRule.checkWatched((Unit) attacker, attacked)) {
                //increase attack if attacker watches attacked
                int bonus = MathMaster.applyMod(WatchRule.ATTACK_MOD, attacker
                        .getIntParam(PARAMS.WATCH_ATTACK_MOD));
                attack += bonus;
            }

        return attack;
    }

    public static int getMissChance(int attack, int defense, DC_ActiveObj action) {
        return getChance(action, action.getOwnerUnit(), null, attack, defense, false);
    }

    public static int getCritChance(int attack, int defense, DC_ActiveObj action) {
        return getChance(action, action.getOwnerUnit(), null, attack, defense, true);
    }

    // returns true if dodged, false if critical, otherwise null
    public static Boolean checkDodgedOrCrit(Attack attack) {
        return checkDodgedOrCrit(attack.getAttacker(), attack.getAttacked(), attack.getAction(),
                attack.getRef(), attack.isOffhand());
    }

    public static Boolean checkDodgedOrCrit(BattleFieldObject attacker, BattleFieldObject attacked,
                                            DC_ActiveObj action, Ref ref,
                                            boolean offhand


    ) {
        return checkDodgedOrCrit(attacker, attacked, action, ref,
                offhand, true);
    }

    public static Boolean checkDodgedOrCrit(BattleFieldObject attacker, BattleFieldObject attacked,
                                            DC_ActiveObj action, Ref ref,
                                            boolean offhand

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
            String msg = (crit ? "...chance for critical strike: " : "..." + attacked.getName()
                    + "'s chance to dodge: ") + (chance) + "%";

            main.system.auxiliary.log.LogMaster.log(1, msg);
            action.getGame().getLogManager().log(
//                    attacker.isPlayerCharacter() ? LogManager.LOGGING_DETAIL_LEVEL.ESSENTIAL :
                    LogManager.LOGGING_DETAIL_LEVEL.ESSENTIAL
                    , msg);
        }
        boolean result = RandomWizard.chance(chance);
        if (isCRIT_TEST()) {
            result = true;
            crit = true;
        }
        if (result) {
            String msg = (crit ? attacker.getNameIfKnown() + " makes a Critical Strike against "
                    + attacked.getNameIfKnown()
                    : attacked.getNameIfKnown() + " has dodged an attack from " + attacker.getNameIfKnown()
                    + StringMaster.wrapInParenthesis(chance + "%"));

            main.system.auxiliary.log.LogMaster.log(1, msg);
            action.getGame().getLogManager().
                    log(msg);
            if (crit) {
                EUtils.showInfoTextStyled(GuiStage.LABEL_STYLE.AVQ_LARGE, attacker.getName()+ ": Critical hit!");
                return false;
            } else {
                EUtils.showInfoTextStyled(GuiStage.LABEL_STYLE.AVQ_LARGE, attacked.getName()+ ": Dodge!");
                return true;
            }
        }
        chance = action.getIntParam(PARAMS.AUTO_CRIT_CHANCE);
        if (chance > 0) {
            if (!RandomWizard.chance(chance)) {
                return null;
            }
            String msg =  attacker.getNameIfKnown() + " makes a Critical Strike (bonus chance) against "+
                     StringMaster.wrapInParenthesis(chance + "%");
            main.system.auxiliary.log.LogMaster.log(1, msg);
            action.getGame().getLogManager().
                    log(msg);

            return false;
        }
        return null;
    }

    private static int getChance(DC_ActiveObj action, BattleFieldObject attacker,
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
        if (action.getGame().getCombatMaster().isChancesOff()) {
            RULE rule = (critOrDodge) ? RULE.CRITICAL_ATTACK : RULE.DODGE;
            if (RuleKeeper.isRuleTestOn(rule) || chance > 50)
                chance = 100;
            else chance = 0;

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
        attack.setAttacked(target);
        int atk = getAttackValue(attack);
        int def = getDefenseValue(attack);
        return atk < def ? -getMissChance(atk, def, entity) : getCritChance(atk, def, entity);
    }
}
