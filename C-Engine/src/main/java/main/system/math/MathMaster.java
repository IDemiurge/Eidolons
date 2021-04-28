package main.system.math;

import main.content.values.parameters.PARAMETER;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.core.game.Game;
import main.game.core.game.GenericGame;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.datatypes.WeightMap;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class MathMaster {

    public static final Integer PERCENTAGE = 1000000;
    public static final Integer MAX_REASONABLE_PERCENTAGE = 3000000;
    public static final Integer MULTIPLIER = 10000;
    public static final double NUMBERS_AFTER_PERIOD = 2;
    private static final boolean autoResolveParseExceptions = true;
    protected GenericGame game;

    // public abstract Integer getStartingFocus(Obj obj);
    public static float getFloatWithDigitsAfterPeriod(int digitsAfterPeriod, float v) {
        return Float.parseFloat(NumberUtils.formatFloat(digitsAfterPeriod, v));
    }
    public static Integer getAverage(Integer... integers) {
        Integer sum = 0;
        for (Integer i : integers) {
            sum += i;
        }
        return Math.round(sum / integers.length);
    }

    public static int round(float i) {
        return Math.round(i);
    }

    public static int getCentimalPercentage(Integer numenator, Integer denominator) {
        return getPercentage(numenator, denominator) / MULTIPLIER;
    }

    public static int getPercentage(Integer numenator, Integer denominator) {
        if (numenator == 0) {
            return 0;
        }
        if (denominator == 0) {
            return 0;
        }
        // return PERCENTAGE;

        return numenator * PERCENTAGE / denominator;

    }

    public static int getFractionValueCentimal(int base_value, int c_percentage) {
        return round(new Float(base_value) * new Float(c_percentage) / 100);
    }

    public static Double getFractionValueCentimalDouble(Double amount, Double c_percentage) {
        return amount * c_percentage / 100;
    }

    public static int getFractionValue(int base_value, int c_percentage) {
        // if (c_percentage<100){
        // =100;
        // }
//        if (c_percentage * 100 / PERCENTAGE == 0) {
//TODO ?
//        }
        return round(getFractionValueFloat(base_value, c_percentage));
    }

    public static float getFractionValueFloat(int base_value, int c_percentage) {
        return new Float(base_value) * new Float(c_percentage) / PERCENTAGE;
    }

    public static int getCentimalPercent(Integer percentage) {
        return percentage / MULTIPLIER;
    }

    public static int getFullPercent(Integer perc) {
        return perc * MULTIPLIER;
    }

    public static int calculateFormula(String formula, int amount) {
        Ref ref = new Ref(Game.game);
        ref.setAmount(amount);
        return new Formula(formula).getInt(ref);
    }

    // applyFactor
    public static int applyPercent(int amount, Integer mod) {
        // amount = amount +
        amount = Math.round(new Float(amount * mod) / 100);
        return amount;
    }

    public static Integer applyModIfNotZero(int amount, Integer mod) {
        if (amount == 0) {
            return 100;
        }
        if (mod == 0) {
            return amount;
        }
        return applyPercent(amount, mod);
    }

    public static Double applyModDouble(Double amount, Integer mod) {
        return new Double(amount * mod) / 100;
    }

    public static Double addFactorDouble(Double amount, Integer factor) {
        return amount * (factor + 100) / 100;
    }

    public static int addFactor(int amount, Integer factor) {
        // amount = amount +
        amount = Math.round(amount * (factor + 100) / 100);
        return amount;
    }

    public static int getMaxX(String data) {
        return getMaxCoordinateFromUnitGroupData(true, data);
    }

    public static int getMaxY(String data) {
        return getMaxCoordinateFromUnitGroupData(false, data);
    }

    public static boolean compare(int n, int n1, Boolean greater_less_equal) {
        if (greater_less_equal == null) {
            return n == n1;
        }
        return greater_less_equal ? n > n1 : n < n1;
    }

    public static int getMaxCoordinateFromUnitGroupData(boolean x_y, String data) {
        int max = 0; // x-y=name;
        for (String substring : ContainerUtils.open(data, ",")) {
            Integer c = NumberUtils.getIntParse(substring.split("=")[x_y ? 0 : 1]);
            if (c > max) {
                max = c;
            }
        }
        return max;
    }

    public static int applyModOrFactor(int amount, Obj obj, PARAMETER param) {
        Integer value = obj.getIntParam(param);
        return isModOrFactor(param) ? applyModIfNotZero(amount, value) : addFactor(amount, value);
    }

    private static boolean isModOrFactor(PARAMETER param) {
        if (param.isMod()) {
            return true;
        }
        Integer i = NumberUtils.getIntParse(param.getDefaultValue());
        return i == 100;
    }

    public static String formatFormula(String formula) {
        formula = formula.toLowerCase();
        if (formula.charAt(0) == '-') {
            while (formula.startsWith("(", 1)) {
                formula = StringMaster.replaceFirst(formula, "(", "");
                formula = StringMaster.replaceFirst(formula, ")", "");
                // int endIndex = formula.lastIndexOf(")");
                // formula = "-1*" + formula.substring(1, endIndex);
            }

        }
        if (formula.contains("(--"))
            formula = formula.replace("(--", "(");

        if (formula.contains("--"))
            formula = formula.replace("--", "+");
        return formula;
    }

    public static boolean isMaskAlt(int modifiers) {
        int i = modifiers & ActionEvent.ALT_MASK;
        return (i == ActionEvent.ALT_MASK);
    }

    public static boolean isShiftMask(int modifiers) {
        int i = modifiers & ActionEvent.SHIFT_MASK;
        return (i == ActionEvent.SHIFT_MASK);
    }

    public static boolean isCtrlMask(int modifiers) {
        int i = modifiers & ActionEvent.CTRL_MASK;
        return (i == ActionEvent.CTRL_MASK);
    }

    public static boolean isAutoResolveParseExceptions() {
        return autoResolveParseExceptions;
    }

    public static int getMinMax(int i, int min, int max) {
        if (i >= max) {
            return max;
        }
        if (i <= min) {
            return min;
        }
        return i;
    }

    public static float getMinMax(float i, float min, float max) {
        if (i >= max) {
            return max;
        }
        if (i <= min) {
            return min;
        }
        return i;
    }

    public static float minMax(float i, float min, float max) {
        if (i >= max) {
            return max;
        }
        if (i <= min) {
            return min;
        }
        return i;
    }

    public static int getPole(int wallWidth, Integer fieldLength, boolean flipped) {
        if (!flipped) {
            return wallWidth;
        }
        return fieldLength - wallWidth;
    }

    public static int getPlusMinusRandom(Boolean mode, int n) {
        if (mode == null) {
            return RandomWizard.getRandomIntBetween(-n, n);
        }
        return mode ? n : -n;
    }

    public static int getTotalOrMinMax(Boolean mode, int n, int n2) {
        if (mode == null) {
            return (n + n2);
        }
        if (mode)
            return Math.min(n, n2);
        return Math.max(n, n2);
    }

    public static int getAverageMinMax(Boolean mode, int n, int n2) {
        if (mode == null) {
            return getAverage(n, n2);
        }
        if (mode)
            return Math.min(n, n2);
        return Math.max(n, n2);
    }

    public static int applyMods(int amount, Integer... mods) {
        for (int mod : mods) {
            amount = applyPercent(amount, mod);
        }
        return amount;
    }

    public static List<Integer> getIntsInRange(int first, int last) {
        List<Integer> list = new ArrayList<>();
        for (int i = first; i < last; i++) {
            list.add(i);
        }
        return list;

    }

    public static float getBalanceCoef(WeightMap map) {
        //average difference, min/max difference TODO
        for (Object o : map.keySet()) {
            Integer val = (Integer) map.get(o);
        }
        return 0;
    }

    public static int sum(int value) {
        int r=0;
        for (int i = 1; i <= value; i++) {
            r+=i;
        }
        return r;
    }

    public static int getArithmeticSequenceSum(int start, int end, int level) {
        return (start+end)/2*level;
    }

    public abstract Integer getStartingEssence(Obj obj);




}
