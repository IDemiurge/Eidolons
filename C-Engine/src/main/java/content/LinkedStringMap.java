package content;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedStringMap<T> extends LinkedHashMap<String, T> {

    @Override
    public T get(Object key) {
        return super.get(key.toString().toUpperCase());
    }

    @Override
    public T put(String key, T value) {
        return super.put(key.toUpperCase(), value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends T> m) {
        for (String s : m.keySet()) {
            put(s, m.get(s));
        }
    }
}
