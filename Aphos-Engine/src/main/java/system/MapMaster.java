package system;

import content.LinkedStringMap;
import elements.stats.UnitParam;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public static String represent(Map map) {
        StringBuilder builder = new StringBuilder("");
        for (Object o : map.keySet()) {
            builder.append(o).append(": ").append(map.get(o)).append("\n");
        }
        return builder.toString();
    }


    public static <T> Map<String, T> mapReverse(Function<T, String> func, T... values) {
        Map<String, T> map = Arrays.stream(values).collect(Collectors.toMap(t -> func.apply(t), r -> r));
        LinkedStringMap<T> stringMap = new LinkedStringMap<>();
        stringMap.putAll(map);
        return stringMap;
    }

}
