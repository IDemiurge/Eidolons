package logic.calculation;

import elements.content.enums.types.CombatTypes;
import main.system.math.MathMaster;

import java.util.Random;

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

    public static CombatTypes.RollGrade calculateGrade(int atk, int def, int die, int autoGradeOffset) {
        int diff = atk - def;
        boolean miss = diff < 0;
        diff = Math.abs(diff);
        int success_offset = 0, failure_offset = 0;
        if (miss) {
            failure_offset = calcOffset(diff);
        } else {
            success_offset = calcOffset(diff);
        }
        int successes = 0, failures = 0;
        while (true) {
            Boolean result = roll(die, failure_offset, success_offset);
            if (result == null)
                break;
            if (result) successes++;
            else failures++;
        }
        return getGrade(MathMaster.getMinMax(successes - failures + autoGradeOffset, -3, 3));
    }

    public static CombatTypes.RollGrade getGrade(int offset) {
        int inverted = 3 - offset;
        return grades[inverted];
    }


    private static Boolean roll(int die, int failureOffset, int successOffset) {
        int i = new Random().nextInt(die);
        if (i >= die - successOffset - 1)
            return true;
        if (i <= failureOffset)
            return false;
        return null;
    }

}
