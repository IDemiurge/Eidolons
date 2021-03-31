package eidolons.system.math.roll;

import main.content.enums.GenericEnums;
import main.entity.obj.Obj;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.log.LogMaster;

import java.util.LinkedHashMap;
import java.util.Map;

import static main.content.enums.GenericEnums.DieType.*;

public class DiceMaster {

    public static final Map<GenericEnums.DieType, Integer> dieValueMap = new LinkedHashMap<>();

    static {
        dieValueMap.put(d4, 4);
        dieValueMap.put(d6, 6);
        dieValueMap.put(d8, 8);
        dieValueMap.put(d10, 10);
        dieValueMap.put(d12, 12);
        dieValueMap.put(d20, 20);
    }

    public static int roll(GenericEnums.DieType dieType, Obj source, boolean log) {
        Integer value = NumberUtils.getInt(dieType.name().substring(1));
        Boolean minMax = checkMinMax(source);
        if (minMax == null) {
            int i = RandomWizard.getRandomInt(value - 1) + 1;
            if (log) log(dieType, source, i);
            return i;
        }
        // if (log) log(getCheatMessage(source, minMax));
        return minMax ? 1 : value;
    }


    public static void log(GenericEnums.DieType dieType, Obj source, int i) {
        String msg = source + " has rolled a " +
                dieType + " die for [" + i + "]";
        source.getGame().getLogManager().log(LogMaster.LOG.GAME_INFO, msg);
    }

    public static int roll(GenericEnums.DieType die, Obj source, int dice, boolean log) {
        dice = checkModifyDice(source, dice, die);
        int result = 0;
        for (int i = 0; i < dice; i++) {
            result += roll(die, source , log);
        }
        return result;
    }

    /**
     * @return true if target wins, false otherwise
     */
    public static boolean roll(GenericEnums.DieType die, Obj source, Obj target,
                               int sDice, int tDice, int sValue, int tValue, boolean log) {
        int sRoll = roll(die, source, sDice , log);
        int tRoll = roll(die, target, tDice , log);

        return tRoll + tValue > sRoll + sValue;
    }


    //TODO
    private static int checkModifyDice(Obj source, int dice, GenericEnums.DieType dieType) {
        return dice;
    }

    //TODO
    private static Boolean checkMinMax(Obj source) {
        return null;
    }

    public static int d20(Obj source, int dice) {
        //control max/min values?
        return roll(d20, source, dice, true);

    }

}
