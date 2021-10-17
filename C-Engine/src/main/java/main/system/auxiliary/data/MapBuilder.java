package main.system.auxiliary.data;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapBuilder<T, T1> {

    private T[] keys;
    private T1[] values;

    public MapBuilder<T, T1> keys(T... keys) {
        this.keys = keys;
        return this;
    }
    public MapBuilder<T, T1> values(T1... values) {
        this.values = values;
        return this;
    }

    public Map<T,T1> build() {
        Map<T, T1> map = new LinkedHashMap<>();
        int i = 0;
        for (T key : keys) {
            map.put(key, values[i++]);
        }
        return map;
    }
}
