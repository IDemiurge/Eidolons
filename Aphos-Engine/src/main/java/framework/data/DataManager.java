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
    static Map<String, Map<String, Object>> dataMap = new LinkedStringMap<>();

    public static void init(String[][] data) {
        for (String[] datum : data) {
            Map<String, Object> collect = stringArrayToMap(datum);
            dataMap.put(collect.get("name").toString(), collect);
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

    private static Object getRawValue(String value) {
        if (NumberUtils.isNumber(value, true)) {
            return  NumberUtils.getInt(value);
        } else if (value.equals("true")) {
           return  true;
        } else if (value.equals("false")) {
           return  false;
        } else {
            return  value.toString();
        }
    }

    public static Map<String, Object> stringArrayToMap(String[] datum) {
        return
                Arrays.asList(datum).stream()
                        .map(s -> new ImmutablePair(s.split("=")[0], s.split("=")[1]))
                        .collect(Collectors.toMap(pair -> pair.getLeft().toString(), pair -> getRawValue(pair.getRight().toString())));
    }

    public static Map<String, Object> getEntityData(String key) {
        return dataMap.get(key);
    }
}
