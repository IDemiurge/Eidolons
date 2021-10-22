package eidolons.game.battlecraft.ai.tools;

import eidolons.content.PARAMS;
import eidolons.content.values.ValuePages;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.ai.advanced.machine.AiConst;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.tools.priority.ParamPriorityAnalyzer;
import eidolons.game.battlecraft.rules.UnitAnalyzer;
import eidolons.game.battlecraft.rules.buff.DC_BuffRule;
import eidolons.system.math.DC_MathManager;
import main.content.ContentValsManager;
import main.content.values.parameters.PARAMETER;
import main.elements.costs.Cost;
import main.elements.costs.Costs;
import main.entity.Entity;
import main.system.math.MathMaster;

import java.util.ArrayList;
import java.util.List;

public class ParamAnalyzer extends AiHandler {
    public final String COST_PENALTY_FORMULA =
     // "100/lg({AMOUNT})";
     // "10+180/(2+sqrt({AMOUNT}*10))";
     // "100-(sqrt({AMOUNT}*10)-{AMOUNT}/15)+ ({AMOUNT}*{AMOUNT}/1000)";
     // //min
     // max!
     "100-sqrt({AMOUNT}*5)-{AMOUNT}/20 ";
    public final String ACTION_FORMULA = "1000/(({AMOUNT}+1)*5)";
    // "100/ {AMOUNT}*{AMOUNT}  x^2-bX = 100 " ;
    // "sqrt({AMOUNT}*10) -100/(100-{AMOUNT})"; TODO perhaps I should have a
    // separate formula for each cost param!


    public ParamAnalyzer(AiMaster master) {
        super(master);
    }

    public static boolean checkStatus(Boolean low_critical, Unit unit,
                                      BUFF_RULE rule) {
        return checkStatus(low_critical, unit, unit.getGame().getRules().getBuffRule(rule));
    }

    public static boolean checkStatus(Boolean critical_low_high, Unit unit, DC_BuffRule rule) {
        Integer buffLevel = rule.getBuffLevel(unit);
        if (buffLevel == null)
            return false;
        if (buffLevel < 0)
            return false;
        return getStatus(unit, rule).bool == critical_low_high;
    }

    public static BUFF_RULE_STATUS getStatus(Unit unit, BUFF_RULE rule) {
        return getStatus(unit, unit.getGame().getRules().getBuffRule(rule));
    }
    public static BUFF_RULE_STATUS getStatus(Unit unit, DC_BuffRule rule) {
        Integer buffLevel = rule.getBuffLevel(unit);
        if (buffLevel == null)
            return BUFF_RULE_STATUS.NONE;
        if (buffLevel < 0)
            return BUFF_RULE_STATUS.NONE;

        if (buffLevel == rule.getMaxLevel()) {
            return BUFF_RULE_STATUS.HIGH;
        }
        if (buffLevel == rule
         .getMinLevel()) {
            return BUFF_RULE_STATUS.CRITICAL;
        }
        return BUFF_RULE_STATUS.LOW;
    }

    public static boolean isFatigued(Unit unit) {
        int buffLevel = unit.getGame().getRules().getStaminaRule()
         .getBuffLevel(unit);
        if (buffLevel < 0) {
            return false;
        }
        return buffLevel != unit.getGame().getRules().getStaminaRule()
         .getMaxLevel();
    }

    public static boolean isHazed(Unit unit) {
        int buffLevel = unit.getGame().getRules().getFocusBuffRule()
         .getBuffLevel(unit);
        if (buffLevel < 0) {
            return false;
        }
        return buffLevel != unit.getGame().getRules().getFocusBuffRule().getMaxLevel();
    }

    public static boolean checkStatus(boolean low_critical, Unit unit, PARAMETER p) {
        if (p == PARAMS.C_TOUGHNESS) {
            return checkStatus(low_critical, unit, unit.getGame().getRules().getStaminaRule());
        }
        if (p == PARAMS.C_FOCUS) {
            return checkStatus(low_critical, unit, unit.getGame().getRules().getFocusBuffRule());

        }
        if (p == PARAMS.C_ESSENCE) {
            if (low_critical) {
                return MathMaster.getCentimalPercent(
                 unit.getIntParam(ContentValsManager.getPercentageParam(p))) < 30;
            } else {
                return MathMaster.getCentimalPercent(
                 unit.getIntParam(ContentValsManager.getPercentageParam(p))) < 10;
            }

        }
        return false;
    }

    private static boolean isScared(Unit unit) {
        return checkStatus(true, unit, unit.getGame().getRules().getEssenceBuffRule());
    }

    public static boolean isParamIgnored(Unit unit, PARAMETER p) {
        if (p == PARAMS.C_FOCUS) {
            return isStaminaIgnore(unit);
        }
        if (p == PARAMS.C_ESSENCE) {
            return isEssenceIgnore(unit);
        }
        return false;

    }

    public static boolean isEssenceIgnore(Unit unit) {
        return !unit.getAI().getType().isCaster();
    }

    public static boolean isMoraleIgnore(Unit unit) {
        return !unit.getGame().getRules().getEssenceBuffRule().check(unit);
    }

    public static boolean isStaminaIgnore(Unit unit) {
        return !unit.getGame().getRules().getStaminaRule().check(unit);

    }

    public static boolean isFocusIgnore(Unit unit) {
        return !unit.getGame().getRules().getFocusBuffRule().check(unit);
    }

    public static int getParamMinValue(PARAMETER param) {
        if (param instanceof PARAMS) {
            PARAMS p = (PARAMS) param;
            switch (p) {
                case C_FOCUS:
                case C_ESSENCE:
                    return 0;

            }
        }
        return Integer.MIN_VALUE;
    }

    public static int getMaxParam(PARAMS p, List<Entity> ArrayList) {
        int max = Integer.MIN_VALUE;
        for (Entity e : ArrayList) {
            if (e.getIntParam(p) > max) {
                max = e.getIntParam(p);
            }
        }

        return max;

    }

    public static Entity getMaxParamUnit(PARAMS p, List<Entity> ArrayList) {
        Entity unit = null;
        int max = Integer.MIN_VALUE;
        for (Entity e : ArrayList) {
            if (e.getIntParam(p) > max) {
                max = e.getIntParam(p);
                unit = e;
            }
        }

        return unit;

    }

    private String getCOST_PENALTY_FORMULA() {
        return "100-sqrt({AMOUNT}*" +
         getConstInt(AiConst.COST_SQUARE) +
         ")-{AMOUNT}/" +
         getConstInt(AiConst.COST_DIVIDER);
    }

    public int getCostPriorityFactor(Costs cost, Unit unit) {
        // if (!cost.canBePaid(unit.getRef()))
        // return -100;
        //TODO ai revamp - this math just sucks, and it's too much!
        int penalty = 0;
        for (Cost c : cost.getCosts()) {
            PARAMETER p = c.getPayment().getParamToPay();
            int base_value = getParamPriority(p, unit); // return a *formula*
            // perhaps?
            Integer costAmount = c.getPayment().getAmountFormula()
                    .getInt(unit.getRef());
            if (p == PARAMS.C_ATB) {
                penalty+=costAmount*ParamPriorityAnalyzer.getParamNumericPriority((PARAMS) p);
                continue;
            }
            if (base_value <= 0) {
                continue;
            }
            int perc = DC_MathManager.getCentimalPercentage(costAmount, unit.getIntParam(p));
            if (perc <= 0) {
                continue;
            }

            // speaking of real numbers, stamina/foc should have a non-linear
            // formula I reckon
            //

            int amount = MathMaster.getFractionValueCentimal(base_value, perc);
            penalty += (amount);
        }
        return MathMaster.calculateFormula(getCOST_PENALTY_FORMULA(), penalty);
    }

    public int getActionNumberFactor(int size) {
        return MathMaster.calculateFormula(ACTION_FORMULA, size);
    }

    public int getParamPriority(PARAMETER p, Unit unit) {
        // int percentage = DC_MathManager.getParamPercentage(unit, portrait);
        // if (percentage == 0) {
        // return -100;
        // }
        int base_priority = 0;
        if (p == PARAMS.C_ESSENCE)
        // return getCastingPriority(unit);
        {
            return 50;
        }
        if (p == PARAMS.C_FOCUS) {
            if (ParamAnalyzer.isFocusIgnore(unit)) {
                return 0;
            }
            if (!UnitAnalyzer.hasFocusBlockedActions(unit)) {
                return 50;
            } else {
                return 100;
            }
        }
        // preCheck actions
        if (p == PARAMS.C_ENDURANCE) {
            return 125;
        }
        // preCheck toughness
        return (base_priority);
        // * 100 - percentage / MathManager.MULTIPLIER) / 100;
    }

    public List<PARAMS> getRelevantParams(Unit unit) {
        List<PARAMS> list = new ArrayList<>();
        for (PARAMS p : ValuePages.UNIT_DYNAMIC_PARAMETERS_RESTORABLE) {
            if (!isParamIgnored(unit, p)) {
                list.add(p);
            }
        }
        return list;
    }

    public enum BUFF_RULE {
        MORALE,
        FOCUS,
        STAMINA,
        WOUNDS,

    }

    public enum BUFF_RULE_STATUS {
        CRITICAL(true),
        LOW(false),
        NONE(null),
        HIGH(null),;
        Boolean bool;

        BUFF_RULE_STATUS(Boolean bool) {
            this.bool = bool;
        }
    }

}
