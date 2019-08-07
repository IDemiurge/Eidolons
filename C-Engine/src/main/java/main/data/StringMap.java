package main.data;

import java.util.HashMap;
import java.util.Map;

public class StringMap<T> extends HashMap<String, T> {

    @Override
    public T get(Object key) {
        return super.get(key.toString().toLowerCase());
    }

    @Override
    public T put(String key, T value) {
        return super.put(key.toLowerCase(), value);
    }
}
