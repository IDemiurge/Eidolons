package eidolons.game.battlecraft.rules;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.counter.generic.DamageCounterRule;
import eidolons.game.battlecraft.rules.counter.generic.timed.TimedRule;
import eidolons.game.core.game.DC_Game;
import eidolons.game.netherflame.main.misc.PaleAspect;

/**
 * Created by JustMe on 4/9/2018.
 */
public class DC_RuleMaster {
    private final DC_Rules rules;
    private final DC_Game game;

    public DC_RuleMaster(DC_Game game, DC_Rules rules) {
        this.game = game;
        this.rules = rules;
    }

    public static boolean isFocusReqsOff() {
        return true;
    }

    public static boolean isToughnessReduced() {
        return true;
    }

    public static void applyCompensation(Unit unit) {

    }

    public static String getRuleLogText(RuleEnums.TURN_RULES rule, int amount) {
        switch (rule) {
            case MORALE:
                // if (amount > 0)
                // return " regains " + amount + " Morale!";
                // return " calms down, giving up " + amount + " Morale";
                if (amount > 0) {
                    return "'s Fright subsides, " + amount + " Morale regained!";
                }
                return "'s Inspiration subsides, Morale reduced by " + amount;
            case FOCUS:
                if (amount > 0) {
                    return "'s Dizziness subsides, " + amount + " Focus regained!";
                }
                return "'s Sharpness subsides, Focus reduced by " + amount;

            case BLEEDING:
                if (amount != 0) {
                    return " suffers " + amount + " damage from bleeding!";
                }
                return " is bleeding!";
            case DISEASE:
                if (amount != 0) {
                    return " suffers " + amount + " damage from disease!";
                }
                return " is diseased!";
            case POISON:
                if (amount != 0) {
                    return " suffers " + amount + " damage from poison!";
                }
                return " is poisoned!";
            default:
                break;

        }
        return null;

    }

    public static String getRuleLogText(RuleEnums.COMBAT_RULES rule, int level) {
        switch (rule) {
            case FOCUS:
                if (level == 2) {
                    return "'s focus is razorsharp!";
                }
                if (level == 1) {
                    return " is dizzy!";
                }
                if (level == 0) {
                    return " is confused!";
                }
            case ESSENCE:
                if (level == 2) {
                    return " takes heart!";
                }
                if (level == 1) {
                    return " loses heart!";
                }
                if (level == 0) {
                    return " panics!";
                }
            case TOUGHNESS:
                if (level == 2) {
                    return " is full of energy!";
                }
                if (level == 1) {
                    return " is fatigued!";
                }
                if (level == 0) {
                    return " is exhausted!";
                }
            case WEIGHT:
                if (level == 1) {
                    return " is encumbered!";
                }
                if (level == 0) {
                    return " is overburdened!";
                }
            case WOUNDS:
                if (level == 1) {
                    return " is wounded!";
                }
                if (level == 0) {
                    return " is critically wounded!";
                }
            default:
                break;

        }
        return null;
    }

    public void timePassed(Float time) {
        for (BattleFieldObject object : game.getBfObjects()) {

            if (object.isPale() != PaleAspect.ON)
                continue;

            if (object.isResetIgnored()) {
                continue;
            }
            for (DamageCounterRule rule : rules.getTimedRules().keySet()) {
                if (rule.checkApplies(object)) {
                    TimedRule timedRule = rules.getTimedRules().get(rule);
                    timedRule.timePassed(time, object);
                }

            }
        }
    }
}
