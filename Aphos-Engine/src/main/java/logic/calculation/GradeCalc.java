package logic.calculation;

import elements.content.enums.types.CombatTypes;
import elements.stats.sub.OmenRollFx;
import logic.calculation.grade_rolls.RollData;
import main.system.math.MathMaster;
import system.math.Rolls;

/**
 * Created by Alexander on 8/19/2023
 */
public class GradeCalc {
    static int[] diffToAdv = new int[100];
    static CombatTypes.RollGrade[] grades = CombatTypes.RollGrade.values();

    private static int calcOffset(int diff) {
        //if (diff>=100) ...
        return diffToAdv[diff];
    }

    static {
        //1 3 6
        int[] boundaries = new int[15];
        int boundary;
        int n = 1;
        for (int i = 0; i < 15; i++) {
            boundary = i + n;
            n += i + 1;
            boundaries[i] = boundary;
        }
        int adv = 0;
        for (int i = 0; i < 100; i++) {
            diffToAdv[i] = adv;
            if (i >= boundaries[adv]) {
                adv++;
            }
        }
    }
    /*
    omen effects
     */
    public static CombatTypes.RollGrade calculateGrade(OmenRollFx rollFx,
            RollData data) {
        //just extract some numeric param from this early on!..
        Boolean dis_adv;

        //note that it is possible that BOTH units have an OMEN!
        switch (rollFx) {

        }
        // RollData - alter it?

        return null;
    }
    public static CombatTypes.RollGrade calculateGrade(int atk, int def, int die, int autoGradeOffset) {
       //TODO   read auto success/fails
        int successes = 0, failures = 0;
        int diff = atk - def;
        boolean miss = diff < 0;
        diff = Math.abs(diff);
        int success_offset = 0, failure_offset = 0;
        if (miss) {
            failure_offset = calcOffset(diff);
        } else {
            success_offset = calcOffset(diff);
        }
        while (true) {
            Boolean result = Rolls.rollSuccessFailOrNothing(die, failure_offset, success_offset);
            if (result == null)
                break;
            if (result) successes++;
            else failures++;
        }
        return getGradeConst(MathMaster.getMinMax(successes - failures + autoGradeOffset, -3, 3));
    }

    private static CombatTypes.RollGrade getGradeConst(int offset) {
        int inverted = 3 - offset;
        return grades[inverted];
    }


}
