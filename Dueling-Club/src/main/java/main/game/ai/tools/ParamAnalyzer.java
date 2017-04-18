package main.game.ai.tools;

import main.content.ContentManager;
import main.content.PARAMS;
import main.content.ValuePages;
import main.content.values.parameters.PARAMETER;
import main.elements.costs.Cost;
import main.elements.costs.Costs;
import main.entity.Entity;
import main.entity.obj.unit.Unit;
import main.game.ai.elements.generic.AiHandler;
import main.rules.UnitAnalyzer;
import main.rules.buff.DC_BuffRule;
import main.system.math.DC_MathManager;
import main.system.math.MathMaster;

import java.util.LinkedList;
import java.util.List;

public class ParamAnalyzer extends AiHandler {
    public final String COST_PENALTY_FORMULA =
            // "100/lg({AMOUNT})";
            // "10+180/(2+sqrt({AMOUNT}*10))";
            // "100-(sqrt({AMOUNT}*10)-{AMOUNT}/15)+ ({AMOUNT}*{AMOUNT}/1000)";
            // //min
            // max!
            "100-sqrt({AMOUNT}*5)-{AMOUNT}/20 ";
    public final String ACTION_FORMULA = "1000/(({AMOUNT}+1)*10/2)";
    // "100/ {AMOUNT}*{AMOUNT}  x^2-bX = 100 " ;
    // "sqrt({AMOUNT}*10) -100/(100-{AMOUNT})"; TODO perhaps I should have a
    // separate formula for each cost param!


    public ParamAnalyzer(AiHandler master) {
        super(master);
    }

    public int getCostPriorityFactor(Costs cost, Unit unit) {
        // if (!cost.canBePaid(unit.getRef()))
        // return -100;
        int penalty = 0;
        for (Cost c : cost.getCosts()) {
            PARAMETER p = c.getPayment().getParamToPay();
            int base_value = getParamPriority(p, unit); // return a *formula*
            // perhaps?
            if (base_value <= 0) {
                continue;
            }
            int perc = DC_MathManager.getCentimalPercentage(c.getPayment().getAmountFormula()
             .getInt(unit.getRef()), unit.getIntParam(p));
            if (perc > 100) {
                // not enough
                if (p != PARAMS.C_N_OF_ACTIONS) {
                    // actions can be gained on next  round
                    return 0;
                }
            }
            if (perc <= 0) {
                continue;
            }

            // speaking of real numbers, stamina/foc should have a non-linear
            // formula I reckon
            //

            int amount = MathMaster.getFractionValueCentimal(base_value, perc);
            penalty += (amount);
        }
        return MathMaster.calculateFormula(COST_PENALTY_FORMULA, penalty);
    }
    public static boolean checkStatus(boolean low_critical, Unit unit, DC_BuffRule rule) {
        Integer buffLevel = rule.getBuffLevel(unit);
        if (buffLevel==null ){
            return false;
        }
        if (buffLevel < 0) {
            return false;
        }
        if (buffLevel == rule
                .getMaxLevel()) {
            return false;
        }
        if (buffLevel == rule
                .getMinLevel()) {
            if (low_critical) {
                return true;
            } else {
                return false;
            }
        }
        if (low_critical) {
            return false;
        }
        return true;
    }

    public static boolean isFatigued(Unit unit) {
        int buffLevel = unit.getGame().getRules().getStaminaRule()
                .getBuffLevel(unit);
        if (buffLevel < 0) {
            return false;
        }
        if (buffLevel == unit.getGame().getRules().getStaminaRule()
                .getMaxLevel()) {
            return false;
        }
        return true;
    }

    public static boolean isHazed(Unit unit) {
        int buffLevel = unit.getGame().getRules().getFocusRule()
                .getBuffLevel(unit);
        if (buffLevel < 0) {
            return false;
        }
        if (buffLevel == unit.getGame().getRules().getFocusRule().getMaxLevel()) {
            return false;
        }
        return true;
    }

    public static boolean checkStatus(boolean low_critical, Unit unit, PARAMETER p) {
        if (p == PARAMS.C_STAMINA) {
            return checkStatus(low_critical, unit, unit.getGame().getRules().getStaminaRule());

        }
        if (p == PARAMS.C_FOCUS) {
            return checkStatus(low_critical, unit, unit.getGame().getRules().getFocusBuffRule());

        }
        if (p == PARAMS.C_MORALE) {
            return checkStatus(low_critical, unit, unit.getGame().getRules().getMoraleBuffRule());

        }
        if (p == PARAMS.C_ESSENCE) {
            if (low_critical) {
                return MathMaster.getCentimalPercent(
                        unit.getIntParam(ContentManager.getPercentageParam(p))) < 30;
            } else {
                return MathMaster.getCentimalPercent(
                        unit.getIntParam(ContentManager.getPercentageParam(p))) < 10;
            }

        }
        return false;
    }

    private static boolean isScared(Unit unit) {
        return checkStatus(true, unit, unit.getGame().getRules().getMoraleBuffRule());
    }

    public static boolean isParamIgnored(Unit unit, PARAMETER p) {
        if (p == PARAMS.C_STAMINA) {
            return isStaminaIgnore(unit);
        }
        if (p == PARAMS.C_FOCUS) {
            return isStaminaIgnore(unit);
        }
        if (p == PARAMS.C_MORALE) {
            return isMoraleIgnore(unit);
        }
        return false;

    }

    public static boolean isMoraleIgnore(Unit unit) {
        return !unit.getGame().getRules().getMoraleRule().check(unit);
    }

    public static boolean isStaminaIgnore(Unit unit) {
        return !unit.getGame().getRules().getStaminaRule().check(unit);
    }

    public static boolean isFocusIgnore(Unit unit) {
        return !unit.getGame().getRules().getFocusRule().check(unit);
    }

    public static int getParamMinValue(PARAMETER param) {
        if (param instanceof PARAMS) {
            PARAMS p = (PARAMS) param;
            switch (p) {
                case C_FOCUS:
                    return 0;
                case C_STAMINA:
                    return 0;
                case C_ESSENCE:
                    return 0;

            }
        }
        return Integer.MIN_VALUE;
    }

    public static int getMaxParam(PARAMS p, List<Entity> linkedList) {
        int max = Integer.MIN_VALUE;
        for (Entity e : linkedList) {
            if (e.getIntParam(p) > max) {
                max = e.getIntParam(p);
            }
        }

        return max;

    }

    public static Entity getMaxParamUnit(PARAMS p, List<Entity> linkedList) {
        Entity unit = null;
        int max = Integer.MIN_VALUE;
        for (Entity e : linkedList) {
            if (e.getIntParam(p) > max) {
                max = e.getIntParam(p);
                unit = e;
            }
        }

        return unit;

    }


    public int getParamPriority(PARAMETER p, Unit unit) {
        // int percentage = DC_MathManager.getParamPercentage(unit, p);
        // if (percentage == 0) {
        // return -100;
        // }
        int base_priority = 0;
        if (p == PARAMS.C_STAMINA) {
            if (ParamAnalyzer.isStaminaIgnore(unit)) {
                return 0;
            }
            return 125; // actions
        }
        if (p == PARAMS.C_N_OF_ACTIONS) {
            return 150;
        }
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
        List<PARAMS> list = new LinkedList<>();
        for (PARAMS p : ValuePages.UNIT_DYNAMIC_PARAMETERS_RESTORABLE) {
            if (!isParamIgnored(unit, p)) {
                list.add(p);
            }
        }
        return list;
    }
}
