package framework.data;

import content.LinkedStringMap;
import elements.content.enums.EnumFinder;
import elements.stats.generic.Stat;
import main.system.auxiliary.StringMaster;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static framework.data.DataManager.getRawValue;

/**
 * Created by Alexander on 8/23/2023
 */
public class TypeData {
    private static final int minValue = Integer.MIN_VALUE;
    protected final Map<String, Integer> intMap = new LinkedStringMap<>();
    protected final Map<String, String> stringMap = new LinkedStringMap<>();
    protected final Map<String, Boolean> boolMap = new LinkedStringMap<>();
    protected final Map<Class, Map> maps = new HashMap<>();
    private final Map<String, Function<String, Object>> cache = new LinkedStringMap<>();

    public TypeData(Map<String, Object> valueMap) {
        maps.put(Boolean.class, boolMap);
        maps.put(String.class, stringMap);
        maps.put(Integer.class, intMap);

        for (String key : valueMap.keySet()) {
            initValue(key, valueMap.get(key));
        }
    }

    protected Object initValue(String key, Object o) {
        Object val = getRawValue(o);
        set(key, val);
        return val;
    }

    public void addIntValue(String valueName, Integer value) {
        int prev =getIntOrZero(valueName);
        int newVal = prev + value;
        intMap.put(valueName, newVal);
    }

    private int getIntOrZero(String valueName) {
        Integer integer =intMap.get(valueName);
        if (integer!=null)
            return integer;
        return 0;
    }

    //endregion
    //////////////// region SETTERS ///////////////////
    public void set(Stat key, Object val) {
        set(key.getName(), val);
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
        Integer integer = intMap.get(key.toString());
        if (integer == null) {
            return minValue;
        }
        return integer;
    }

    private Boolean getB(String name) {
        return boolMap.get(name);
    }

    public String getS(String name) {
        return stringMap.get(name);
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
        return new Boolean(true).equals(getB(s));
    }
}
