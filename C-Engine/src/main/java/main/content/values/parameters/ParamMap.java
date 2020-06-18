package main.content.values.parameters;

import com.badlogic.gdx.utils.ObjectMap;
import main.content.ContentValsManager;
import main.content.VALUE;
import main.content.ValueMap;

import java.util.Map;

public class ParamMap implements ValueMap {
    ObjectMap<PARAMETER, String> map = new ObjectMap<>();

    @Override
    public String toString() {
        // java.util.Collections.sort(list)
        return map.toString();
    }

    @Override
    public String get(VALUE valueName) {
        String value = map.get((PARAMETER) valueName);
        if (value == null) {
            return "";
        }

        return value;
    }

    @Override
    public String get(String valueName) {
        PARAMETER param = ContentValsManager.getPARAM(valueName);
        return get(param);
    }

    @Override
    public String put(String valueName, String value) {
        PARAMETER p = ContentValsManager.getPARAM(valueName);
        return put(p, value);
    }

    public int size() {
        return map.size;
    }

    public boolean isEmpty() {
        return map.size==0;
    }

    public boolean containsKey(Object key) {
        return map.containsKey((PARAMETER) key);
    }

    public String put(PARAMETER key, String value) {
        String previous = map.put(key, value);
        return previous;
    }

    public void putAll(Map<? extends PARAMETER, ? extends String> m) {
        // map.putAll(m);
        // EA check - loading needs a fix..
    }

    public void clear() {
        map.clear();
    }

    public ObjectMap.Keys<PARAMETER> keySet() {
        return map.keys() ;
    }

    public ObjectMap<PARAMETER, String> getMap() {
        return map;
    }

    public void setMap(ObjectMap<PARAMETER, String> map) {
        this.map = map;
    }

}
