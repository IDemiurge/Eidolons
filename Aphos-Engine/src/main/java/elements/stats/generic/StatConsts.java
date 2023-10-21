package elements.stats.generic;

import elements.stats.UnitParam;

/**
 * Created by Alexander on 8/23/2023
 */
public class StatConsts {
    private static final Integer DEFAULT_AP_RETAIN = 1;

    public static final UnitParam[] unitCurrentVals = {
            UnitParam.Moves,
            UnitParam.AP,
            UnitParam.Sanity,
            UnitParam.Faith,
            UnitParam.Health,
            UnitParam.Armor,
            UnitParam.Soul,
    };
    public static final UnitParam[] roundlyParams = {
            UnitParam.Moves,
            UnitParam.AP,
            UnitParam.Sanity,
            UnitParam.Faith
    };
    public static UnitParam[] unitDefaultVals= {
            UnitParam.DR,
            UnitParam.DR_Soul,
            UnitParam.Ap_retain,
            UnitParam.Moves_retain,
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

    public static Integer getDefault(UnitParam param) {
        return switch(param){
            case Ap_retain -> DEFAULT_AP_RETAIN;
            default -> 0;
        };
    }
}
