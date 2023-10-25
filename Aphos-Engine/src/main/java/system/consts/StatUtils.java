package system.consts;

import content.LinkedStringMap;
import elements.stats.UnitParam;
import elements.stats.UnitProp;
import framework.entity.field.Unit;
import system.MapMaster;

import java.util.Map;


/**
 * Created by Alexander on 10/25/2023
 */
public class StatUtils {

    private static Map<String, UnitParam> paramMap = MapMaster.mapReverse(v->v.getName(), UnitParam.values());

    public static UnitParam getParam(String s) {
        return paramMap.get(s);
    }

    public static String getRetain(UnitParam param) {
        return param.getName() + "_retain";
    }

    public static String getBroken(UnitParam param) {
        return param.getName() + "_broken";
    }
}
