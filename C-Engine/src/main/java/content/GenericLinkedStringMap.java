package content;

import java.util.LinkedHashMap;
import java.util.Map;

public class GenericLinkedStringMap<T> extends LinkedHashMap<Object, T> {

    @Override
    public T get(Object key) {
        return super.get(key.toString().toUpperCase());
    }

    @Override
    public T put(Object key, T value) {
        return super.put(key.toString().toUpperCase(), value);
    }

    @Override
    public void putAll(Map<? extends Object, ? extends T> m) {
        for (Object s : m.keySet()) {
            put(s, m.get(s));
        }
    }
}
