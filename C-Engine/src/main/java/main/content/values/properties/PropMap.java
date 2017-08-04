package main.content.values.properties;

import main.content.ContentManager;
import main.content.VALUE;
import main.content.ValueMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class PropMap implements ValueMap {
    Map<PROPERTY, String> map = new HashMap<>();



    @Override
    public String toString() {
        return map.toString();
    }

    public Map<PROPERTY, String> getMap() {
        return map;
    }

    public void setMap(Map<PROPERTY, String> map) {
        this.map = map;
    }

    @Override
    public String get(VALUE valueName) {
        if (valueName == null) {
            return "";
        }
        if (map.get(valueName) == null) {
            return "";
        }
        return map.get(valueName);
    }

    @Override
    public String get(String valueName) {
        PROPERTY p = ContentManager.getPROP(valueName);
        return get(p);
    }

    @Override
    public String put(String valueName, String value) {
        PROPERTY p = ContentManager.getPROP(valueName);

        String previous = map.get(p);
        map.put(p, value);
        return previous;
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public String put(PROPERTY key, String value) {
        return map.put(key, value);
    }

    public String remove(Object key) {
        return map.remove(key);
    }

    public void putAll(Map<? extends PROPERTY, ? extends String> m) {
        map.putAll(m);
    }

    public void clear() {
        map.clear();
    }

    public Set<PROPERTY> keySet() {
        return map.keySet();
    }

    public Collection<String> values() {
        return map.values();
    }

    public Set<Entry<PROPERTY, String>> entrySet() {
        return map.entrySet();
    }

}
