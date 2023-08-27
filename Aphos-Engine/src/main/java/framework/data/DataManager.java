package framework.data;

import content.LinkedStringMap;
import main.data.XLinkedMap;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Alexander on 8/22/2023
 */
public class DataManager {
    /*
    on the whole, our XML stuff was a success - albeit bloated!
    However, what is the purpose of XML exactly if all we need is a bunch of name=val pairs?
    nested stuff - ?
    abils - can just insert xml as string value?
     */
    private static Map<String, Map<String, Object>> unitMap = new LinkedStringMap<>();
    private static Map<String, Map<String, Object>> actionMap = new LinkedStringMap<>();
    private static Map<String, Map<String, Object>> passiveMap = new LinkedStringMap<>();

    public static Map<String, Object> getUnitData(String key) {
        return unitMap.get(key);
    }
    public static Map<String, Object> getActionData(String key) {
        return actionMap.get(key);
    }
    public static Map<String, Object> getPassiveData(String key) {
        return passiveMap.get(key);
    }

    public static void addTypeData(String typeKey,String name, Map map){
        if (typeKey.equalsIgnoreCase("unit")){
            unitMap.put(name, map);
        }
        if (typeKey.equalsIgnoreCase("action")){
            actionMap.put(name, map);
        }
        if (typeKey.equalsIgnoreCase("passive")){
            passiveMap.put(name, map);
        }

    }
    public static Map<String, Object> deconstructDataString(String dataString) {
        Map<String, Object> map = new XLinkedMap<>();
        for (String substring : ContainerUtils.openContainer(dataString)) {
            String[] s = substring.split("=");
            String key = s[0];
            String value = s[1];
            Object o = getRawValue(value);
            map.put(key, o);
        }
        return map;
    }

    public static Object getRawValue(Object value) {
        if (value instanceof Integer || value instanceof Boolean)
            return value;
        String string = (String) value;
        if (NumberUtils.isNumber(string, true)) {
            return  NumberUtils.getInt(string);
        } else if (string.equals("true")) {
            return  true;
        } else if (string.equals("false")) {
            return  false;
        } else {
            return  string;
        }
    }
    public static Map<String, Object> stringToMap(String s) {
        return stringArrayToMap(s.split(";"));
    }

    public static Map<String, Object> stringArrayToMap(String[] datum) {
        return
                Arrays.asList(datum).stream()
                        .map(s -> new ImmutablePair(s.split("=")[0], s.split("=")[1]))
                        .collect(Collectors.toMap(pair -> pair.getLeft().toString(), pair -> getRawValue(pair.getRight().toString())));
    }

    public static Map<String, Map<String, Object>> getUnitMap() {
        return unitMap;
    }

    public static Map<String, Map<String, Object>> getActionMap() {
        return actionMap;
    }

    public static Map<String, Map<String, Object>> getPassiveMap() {
        return passiveMap;
    }
    // public static void init(String[][] data) {
    //     for (String[] datum : data) {
    //         Map<String, Object> collect = stringArrayToMap(datum);
    //         dataMap.put(collect.get("name").toString(), collect);
    //     }
    // }
}
