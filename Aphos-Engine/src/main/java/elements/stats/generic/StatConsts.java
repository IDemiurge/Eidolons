package elements.stats.generic;

import elements.stats.UnitParam;

/**
 * Created by Alexander on 8/23/2023
 */
public class StatConsts {
    public static final UnitParam[] unitCurrentVals = {
            UnitParam.Moves_Max,
            UnitParam.AP_Max,
            UnitParam.Sanity_Max,
            UnitParam.Faith_Max,
            UnitParam.Hp_Max,
            UnitParam.Armor_Max,
            UnitParam.Soul_Max,
    };
    public static final UnitParam[] roundlyParams = {
            UnitParam.Moves,
            UnitParam.AP,
            UnitParam.Sanity,
            UnitParam.Faith
    };
    //OBJ_TYPE ?
    // Stat[][] ARRAYS =

    static {
        arraysToMaps(); //can work for specific class...
    }

    private static void arraysToMaps() {
        // ARRAYS.for
    }

    public static final UnitParam getAtkVal( Boolean minBaseMax) {
        if (minBaseMax==null)
            return UnitParam.Attack_Base;
        return minBaseMax ? UnitParam.Attack_Max : UnitParam.Attack_Min;
    }
    public static final UnitParam getDefVal( Boolean minBaseMax) {
        if (minBaseMax==null)
            return UnitParam.Defense_Base;
        return minBaseMax ? UnitParam.Defense_Max : UnitParam.Defense_Min;
    }
    public static final UnitParam getResVal( Boolean minBaseMax) {
        if (minBaseMax==null)
            return UnitParam.Resistance_Base;
        return minBaseMax ? UnitParam.Resistance_Max : UnitParam.Resistance_Min;
    }
}
