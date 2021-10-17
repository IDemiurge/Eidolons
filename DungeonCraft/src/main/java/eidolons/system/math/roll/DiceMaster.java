package eidolons.system.math.roll;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
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
    public static final Map<Integer, GenericEnums.DieType > dieMap = new LinkedHashMap<>();
    public static final GenericEnums.DieType DEFAULT_DIE = d10;

    static {
        dieValueMap.put(d4, 4);
        dieValueMap.put(d6, 6);
        dieValueMap.put(d8, 8);
        dieValueMap.put(d10, 10);
        dieValueMap.put(d12, 12);
        dieValueMap.put(d20, 20);

        dieMap.put(4, d4);
        dieMap.put(6, d6);
        dieMap.put(8, d8);
        dieMap.put(10, d10);
        dieMap.put(12, d12);
        dieMap.put(20, d20);
    }
//TODO add comment for log
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

    public static int roll(Dice dice, Unit unit, boolean log) {
        return roll(dice.type, unit, dice.dice, log);
    }

    public static void log(GenericEnums.DieType dieType, Obj source, int i) {
        String msg = source.getNameIfKnown() + " rolls " +
                dieType + " for [" + i + "]";
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

    public static int average(GenericEnums.DieType dieType, int dice) {
        return (dieValueMap.get(dieType) + 1) * dice / 2;
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

    public static int getDefaultDieNumber(BattleFieldObject unit) {
        return unit.getGame().getState().getChaosLevel() + 1 + getDieNumberBonus(unit);
    }

    private static int getDieNumberBonus(BattleFieldObject unit) {
        return 0;
    }

    public static GenericEnums.DieType getDie(Integer size) {
        return dieMap.get(size);
    }

}
