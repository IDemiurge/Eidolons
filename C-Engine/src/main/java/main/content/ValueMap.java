package main.content;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class ValueMap<T extends VALUE> implements Serializable {

   protected Map<T, String> map = new LinkedHashMap<>();

    public String toString() {
        return map.toString();
    }

    public String get(T valueName) {
        String value = map.get(valueName);
        if (value == null) {
            return "";
        }
        return value;
    }


    public abstract String get(String valueName);

    
    public abstract String put(String valueName, String value);

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.size()==0;
    }

    public boolean containsKey(Object key) {
        return map.containsKey( key);
    }

    public String put(T key, String value) {
        String previous = map.put(key, value);
        return previous;
    }

    public void putAll(Map<? extends T, ? extends String> m) {
        // map.putAll(m);
        // EA check - loading needs a fix..
    }

    public void clear() {
        map.clear();
    }

    public Set<T> keySet() {
        return map.keySet() ;
    }

    public  Map<T, String> getMap() {
        return map;
    }

    public void setMap( Map<T, String> map) {
        this.map = map;
    }
}
