package framework.data;

import content.LinkedStringMap;
import system.MapMaster;

import java.util.Map;

/**
 * Created by Alexander on 8/25/2023
 */
public abstract class Datum {
    protected final Map<String, Object> data;

    public Datum() {
        data = new LinkedStringMap<>();
    }

    public void addAll(Datum datum){
        addAll(datum.data);
    }

    public void addAll(Map map){
        for (Object key : map.keySet()) {
            data.put(key.toString(), map.get(key));
        }
    }

    public Object get(Object key) {
        return data.get(key);
    }

    public Object __log__(String key, Object value) {
        return data.put(key, value);
    }

    public Object remove(Object key) {
        return data.remove(key);
    }

    @Override
    public String toString() {
        return MapMaster.represent(data);
    }

    // public TypeData getData() {
    //     return data;
    // }
    //wrapper methods?
}
