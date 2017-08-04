package main.content.values.parameters;

import main.content.ContentManager;
import main.content.VALUE;
import main.content.ValueMap;
import main.data.XLinkedMap;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ParamMap implements ValueMap {
    Map<PARAMETER, String> map = new XLinkedMap<>();


    @Override
    public String toString() {
        // java.util.Collections.sort(list)

        return map.toString();
    }

    @Override
    public String get(VALUE valueName) {
        if (map.get(valueName) == null) {
            return "";
        }

        return map.get(valueName);
    }

    @Override
    public String get(String valueName) {
        PARAMETER param = ContentManager.getPARAM(valueName);
        return get(param);
    }

    @Override
    public String put(String valueName, String value) {
        PARAMETER p = ContentManager.getPARAM(valueName);
        return put(p, value);
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

    public String put(PARAMETER key, String value) {

        String previous = map.put(key, value);
        // if (key.getName().contains("Damage")) {
        // if (value.equals("150"))
        // if (previous.equals("100"))
        // return null;
        // }
        return previous;
    }

    public String remove(Object key) {
        return map.remove(key);
    }

    public void putAll(Map<? extends PARAMETER, ? extends String> m) {
        map.putAll(m);
    }

    public void clear() {
        map.clear();
    }

    public Set<PARAMETER> keySet() {
        return map.keySet();
    }

    public Collection<String> values() {
        return map.values();
    }

    public Set<Entry<PARAMETER, String>> entrySet() {
        return map.entrySet();
    }

    public Map<PARAMETER, String> getMap() {
        return map;
    }

    public void setMap(Map<PARAMETER, String> map) {
        this.map = map;
    }

}
