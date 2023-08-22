package framework.entity;

import content.LinkedStringMap;
import elements.content.enums.EnumFinder;
import elements.stats.Stat;
import main.data.XLinkedMap;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.data.DataUnitFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 8/22/2023
 */
public class EntityData {

    private final Map<String, Integer> intMap = new LinkedStringMap<>();
    private final Map<String, String> stringMap = new LinkedStringMap<>();
    private final Map<String, Boolean> boolMap = new LinkedStringMap<>();

    //TODO
    private final Map<String, Integer> intMapCur = new LinkedStringMap<>();

    private final Map<String, Integer> intMapBase = new LinkedStringMap<>();
    private final Map<String, String> stringMapBase = new LinkedStringMap<>();
    private final Map<String, Boolean> boolMapBase = new LinkedStringMap<>();

    private final Map<Class, Map> maps = new HashMap<>();
    private final Map<String, Map<String, Object>> cache = new HashMap<>();

    public static Map<String, Object> deconstructDataString(String dataString) {
        Map<String, Object> map = new XLinkedMap<>();
        for (String substring : ContainerUtils.openContainer(dataString)) {
            String[] s = substring.split("=");
            String key = s[0];
            String value = s[1];
            if (NumberUtils.isNumber(value, true)) {
                map.put(key, NumberUtils.getInt(value));
            } else
            if (value.equals("true")){
                map.put(key, true);
            } else
            if (value.equals("false")){
                map.put(key, false);
            } else {
                map.put(key, value);
            }
        }
        return map;
    }
    public EntityData(String data) {
        this(deconstructDataString(data));

    }
    public EntityData(Map<String, Object> valueMap) {
        for (String key : valueMap.keySet()) {
            Object o = valueMap.get(key);
            set(key, o);
            setBase(key, o);
        }
        maps.put(Boolean.class, boolMap);
        maps.put(String.class, stringMap);
        maps.put(Integer.class, intMap);
//TODO
        // initCurrentValues();
    }

    private void setBase(String s, Object o) {
        if (o instanceof Integer) {
            // if (isCurrent(s))
            //     intMapCur.put(s, (Integer) o)
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

    //////////////// SETTERS ///////////////////

    public void set(Stat key, Object val) {
        set(key.getName(), val);
    }
    public void set(String key, Object val) {
        Map<String, Object> map = maps.get(val.getClass());
        if (map == null)
        {
            map = new LinkedStringMap<>();
            cache.put(key, map);
        }
        map.put(key.toString(), val);
    }

    //////////////// GETTERS ///////////////////
    public <T> T getEnum(String name, Class<T> className) {
        Object value = get(name);
        if (StringMaster.isEmpty(value))
            return null;
        return EnumFinder.get(className, value);
    }

    public Object get(Stat stat) {
        return get(stat.getName());
    }
    public Object get(String key) {
        return cache.get(key).get(key);
    }

    public Boolean getB(String name) {
        return boolMap.get(name);
    }
    public Boolean getB(Stat stat) {
        return boolMap.get(stat.getName());
    }

    public String getS(String name) {
        return stringMap.get(name);
    }
    public String getS(Stat stat) {
        return stringMap.get(stat.getName());
    }
}
