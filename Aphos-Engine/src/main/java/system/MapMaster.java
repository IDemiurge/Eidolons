package system;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 8/22/2023
 */
public class MapMaster {
    public static Map<Class, Object> toClassMap(Object[] args) {
        Map<Class, Object> map = new HashMap<>();
        for (Object arg : args) {
            if (map.put(arg.getClass(), arg) != null)
                throw new RuntimeException("Duplicate class in args[]!");
        }
        return map;
    }
}