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

}
