package main.system.auxiliary.data;

import main.system.auxiliary.ContainerUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapConverter<T, T1> {
    String s_pair, s_item;
    Converter<String, T> keyFunc;
    Converter<String, T1> valueFunc;

    public MapConverter(String s_pair, String s_item, Converter<String, T> keyFunc,
                        Converter<String, T1> valueFunc) {
        this.s_pair = s_pair;
        this.s_item = s_item;
        this.keyFunc = keyFunc;
        this.valueFunc = valueFunc;
    }

    public Map<T, T1> build(String nodeContents) {
        Map<T, T1> map = new LinkedHashMap<>();
        for (String sub : ContainerUtils.openContainer(nodeContents, s_item)) {
            String[] split = sub.split(s_pair);
            map.put(keyFunc.convert(split[0]), valueFunc.convert(split[1]));
        }
        return map;
    }

}
