package main.content.values.properties;

import com.badlogic.gdx.utils.ObjectMap;
import main.content.ContentValsManager;
import main.content.VALUE;
import main.content.ValueMap;

import java.util.Map;

public class PropMap implements ValueMap {
    ObjectMap<PROPERTY, String> map = new ObjectMap<>();

    @Override
    public String toString() {
        return map.toString();
    }

    public ObjectMap<PROPERTY, String> getMap() {
        return map;
    }

    public void setMap(ObjectMap<PROPERTY, String> map) {
        this.map = map;
    }

    @Override
    public String get(VALUE valueName) {
        if (valueName == null) {
            return "";
        }
        String value = map.get((PROPERTY) valueName);
        if (value == null) {
            return "";
        }
        return value ;
    }

    @Override
    public String get(String valueName) {
        PROPERTY p = ContentValsManager.getPROP(valueName);
        return get(p);
    }

    @Override
    public String put(String valueName, String value) {
        PROPERTY p = ContentValsManager.getPROP(valueName);

        String previous = map.get(p);
        map.put(p, value);
        return previous;
    }


    public boolean containsKey(Object key) {
        return map.containsKey((PROPERTY) key);
    }

    public String put(PROPERTY key, String value) {
        if (key == null || value == null)
        {
            return null;
        }
        return map.put(key, value);
    }

    public String remove(Object key) {
        return map.remove((PROPERTY) key);
    }

    public void putAll(Map<? extends PROPERTY, ? extends String> m) {
        // map.putAll(m);
    }

    public void clear() {
        map.clear();
    }

    public ObjectMap.Keys<PROPERTY> keySet() {
        return map.keys();
    }


}
