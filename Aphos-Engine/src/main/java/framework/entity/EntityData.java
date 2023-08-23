package framework.entity;

import content.LinkedStringMap;
import elements.content.enums.EnumFinder;
import elements.stats.generic.Stat;
import main.system.auxiliary.StringMaster;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Alexander on 8/22/2023
 */
public class EntityData {

    private final Map<String, Integer> intMap = new LinkedStringMap<>();
    private final Map<String, String> stringMap = new LinkedStringMap<>();
    private final Map<String, Boolean> boolMap = new LinkedStringMap<>();

    private static final int minValue = Integer.MIN_VALUE;
    private final Map<String, Integer> intMapCur = new LinkedStringMap<>();

    private final Map<String, Integer> intMapBase = new LinkedStringMap<>();
    private final Map<String, String> stringMapBase = new LinkedStringMap<>();
    private final Map<String, Boolean> boolMapBase = new LinkedStringMap<>();

    private final Map<Class, Map> maps = new HashMap<>();
    private final Map<String, Function<String, Object>> cache = new LinkedStringMap<>();


    public EntityData(Map<String, Object> valueMap) {
        maps.put(Boolean.class, boolMap);
        maps.put(String.class, stringMap);
        maps.put(Integer.class, intMap);
        for (String key : valueMap.keySet()) {
            Object o = valueMap.get(key);
            set(key, o);
            setBase(key, o);
        }
    }

    private void setBase(String s, Object o) {
        if (o instanceof Integer) {
            intMapBase.put(s, (Integer) o);
        }
        if (o instanceof Boolean) {
            boolMapBase.put(s, (Boolean) o);
        } else {
            stringMapBase.put(s, o.toString());
        }
    }

    public void toBase() {
        intMap.clear();
        intMap.putAll(intMapBase);
        boolMap.clear();
        boolMap.putAll(boolMapBase);
        stringMap.clear();
        stringMap.putAll(stringMapBase);
    }

    //////////////// region MODIFY ///////////////////
    public void addCurValue(Stat key, int i) {
        intMapCur.put(key.getName(), intMapCur.get(key) + i);
    }

    //endregion
    //////////////// region SETTERS ///////////////////
    public void set(Stat key, Object val) {
        set(key.getName(), val);
    }
    public void setCur(String key, int val) {
        intMapCur.put(key, val);
    }

    public void set(String key, Object val) {
        Map<String, Object> map = maps.get(val.getClass());
        map.put(key.toString(), val);
    }
    //endregion
    //////////////// region GETTERS ///////////////////
    public <T> T getEnum(String name, Class<T> className) {
        String value = getS(name);
        if (StringMaster.isEmpty(value))
            return null;
        return EnumFinder.get(className, value);
    }

    public Object get(String key) {
        if (cache.containsKey(key)) {
            return cache.get(key).apply(key);
        }
        Integer i = getInt(key);
        if (i != minValue) {
            cache.put(key, s -> getInt(s));
            return i;
        }
        Boolean b = getB(key);
        if (b != null) {
            cache.put(key, s -> getB(s));
            return b;
        }
        String str = getS(key);
        if (str != null) {
            cache.put(key, s -> getS(s));
            return str;
        }
        //TODO NOTE ERROR
        return "";
    }

    public Integer getInt(String key) {
        Integer integer = intMapCur.get(key);
        if (integer != null)
            return integer;
        integer = intMap.get(key.toString());
        if (integer == null) {
            return minValue; //MIN_VALUE?
        }
        return integer;
    }

    private Boolean getB(String name) {
        return  boolMap.get(name);
    }

    private String getS(String name) {
        return  stringMap.get(name);
    }

    public Object get(Stat stat) {
        return get(stat.getName());
    }

    public int getInt(Stat stat) {
        return getInt(stat.getName());
    }

    public boolean isTrue(Stat stat) {
        return isTrue(stat.getName());
    }
    public boolean isTrue(String s) {
        return new Boolean (true).equals(getB(s));
    }

    //endregion
}
