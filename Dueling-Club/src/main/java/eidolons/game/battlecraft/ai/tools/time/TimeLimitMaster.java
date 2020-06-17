package eidolons.game.battlecraft.ai.tools.time;

import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import main.system.math.MathMaster;

public class TimeLimitMaster {
    public static final long CRITICAL_FAIL_FACTOR = 10;
    private static final double TIME_LIMIT_FACTOR = 5;
    private static final int AI_TIME_LIMIT_MIN = 3000;
    private static final int AI_TIME_LIMIT_MAX = 7000;
    private static final int AI_TIME_LIMIT_PER_POWER = 150;
    private static final int AI_TIME_LIMIT_MIN_EXPLORE = 500;
    private static final int AI_TIME_LIMIT_PER_POWER_EXPLORE = 15;
    private static final int ACTION_TIME_LIMIT = 3000;
    private static final int PATH_TIME_LIMIT = 2000;
    private static final int CELL_PATH_TIME_LIMIT = 500;
    private static final int PATH_STEP_TIME_LIMIT = 250;
    private static Float timeLimitMod;

    public TimeLimitMaster(UnitAI ai) {
        // init factors!
    }

    public static void createMetric() {
        /*
         * on unit action,
		 *
		 */

    }

    public static boolean isFastPickCell() {
        return true;
    }

    public static long getTimeLimitForCellPath() {
        return Math.round(CELL_PATH_TIME_LIMIT * TIME_LIMIT_FACTOR);
    }

    public static long getTimeLimitForPathBuilding() {
        return Math.round(PATH_TIME_LIMIT * TIME_LIMIT_FACTOR);
    }

    public static long getTimeLimitForPathStep() {
        return Math.round(PATH_STEP_TIME_LIMIT * TIME_LIMIT_FACTOR);
    }

    public static long getTimeLimitForAction() {
        return Math.round(ACTION_TIME_LIMIT * TIME_LIMIT_FACTOR);
    }

    public static Long getTimeLimitMetric(METRIC metric) {
        return new Long(Math.round(getTimeLimitMetricBase(metric) * TIME_LIMIT_FACTOR));
    }

    public static int getTimeLimitMetricBase(METRIC metric) {
        switch (metric) {
            case ACTION:
                return ACTION_TIME_LIMIT;
            case PATH:
                return PATH_TIME_LIMIT;
            case PATH_CELL:
                return CELL_PATH_TIME_LIMIT;
            case PATH_STEP:
                return PATH_STEP_TIME_LIMIT;
        }
        return 0;
    }

    public static boolean checkTimeLimit(METRIC metric, String string) {
        if (CoreEngine.TEST_LAUNCH) {
            return true;
        }
        boolean result = Chronos.getTimeElapsedForMark(string) < TimeLimitMaster
         .getTimeLimitMetric(metric);
        if (!result) {
            LogMaster.log(1, "***** Time elapsed for " + metric + " [" + string + "] - "
             + Chronos.getTimeElapsedForMark(string) + ", limit ("
             + TimeLimitMaster.getTimeLimitMetric(metric) + ")");
        }
        return result;
    }

    public static boolean checkTimeLimitForPathStep(String string) {
        boolean result = Chronos.getTimeElapsedForMark(string) < TimeLimitMaster
         .getTimeLimitForPathStep();
        if (!result) {
            LogMaster.log(1, "Time elapsed for Path Building Step" + string + " - "
             + Chronos.getTimeElapsedForMark(string) + ", limit ("
             + TimeLimitMaster.getTimeLimitForPathStep() + ")");
        }
        return result;
    }

    public static boolean checkTimeLimitForAi(UnitAI ai) {
        if (OptionsMaster.getGameplayOptions().getBooleanValue(GAMEPLAY_OPTION.AI_DEBUG)) {
        // if (CoreEngine.TEST_LAUNCH) {
            return true;
        }
        if (getTimeLimitForAi(ai) > getMarkForAi(ai))
            return true;
        LogMaster.log(1, "*********** TIME ELAPSED FOR  "
         + ai + StringMaster.wrapInParenthesis(getMarkForAi(ai) + ""));
        return false;
    }

    private static long getMarkForAi(UnitAI ai) {
        return Chronos.getTimeElapsedForMark("ai limit for " + ai.getUnit().getName());
    }

    public static long getTimeLimitForAi(UnitAI ai) {
        if (ExplorationMaster.isExplorationOn()) {
            return (long) (Math.max(AI_TIME_LIMIT_MIN_EXPLORE, ai.getUnit().calculatePower() *
                         AI_TIME_LIMIT_PER_POWER_EXPLORE) * getTimeLimitMod());
        }
        return (long) (MathMaster.getMinMax(  ai.getUnit().calculatePower() * AI_TIME_LIMIT_PER_POWER,
         AI_TIME_LIMIT_MIN, AI_TIME_LIMIT_MAX)
                 * getTimeLimitMod())
         ;
    }

    public static void markTimeForAI(UnitAI ai) {
        Chronos.mark(
         "ai limit for " + ai.getUnit().getName());
    }

    public static float getTimeLimitMod() {
        if (timeLimitMod==null )
            timeLimitMod =new Float( OptionsMaster.getGameplayOptions().getIntValue(GAMEPLAY_OPTION.AI_TIME_LIMIT_MOD))/100;
        return timeLimitMod;
    }

    public static void setTimeLimitMod(float timeLimitMod) {
        TimeLimitMaster.timeLimitMod = timeLimitMod;
    }

    public enum METRIC {
        ACTION, PATH, PATH_CELL, PATH_STEP,
    }

}
