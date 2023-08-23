package framework.data;

import content.LinkedStringMap;
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

    public static Map<String, Object> stringArrayToMap(String[] datum) {
        return
                Arrays.asList(datum).stream()
                        .map(s -> new ImmutablePair(s.split("=")[0], s.split("=")[1]))
                        .collect(Collectors.toMap(pair -> pair.getLeft().toString(), pair -> pair.getRight()));
    }

    public static Map<String, Object> getEntityData(String key) {
        return dataMap.get(key);
    }
}
