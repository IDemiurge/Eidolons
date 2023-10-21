package framework.data;

import content.LinkedStringMap;
import elements.content.enums.EnumFinder;
import elements.stats.generic.Stat;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import system.ListMaster;
import system.consts.MathConsts;
import system.consts.StringConsts;

import java.util.*;
import java.util.function.Function;

import static framework.data.DataManager.getRawValue;

/**
 * Created by Alexander on 8/23/2023
 */
public class TypeData {
    protected final Map<String, Integer> intMap = new LinkedStringMap<>();
    protected final Map<String, String> stringMap = new LinkedStringMap<>();
    protected final Map<String, Boolean> boolMap = new LinkedStringMap<>();
    protected final Map<Class, Map> maps = new HashMap<>();
    private final Map<String, Function<String, Object>> getterCache = new LinkedStringMap<>();

    public TypeData() {
        this(new LinkedStringMap<>());
    }

    public TypeData(Map<String, Object> valueMap) {
        maps.put(Boolean.class, boolMap);
        maps.put(String.class, stringMap);
        maps.put(Integer.class, intMap);

        for (String key : valueMap.keySet()) {
            initValue(key, valueMap.get(key));
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Data: ");
        return builder.append("\n---String: ").append(system.MapMaster.represent(stringMap))
                .append("\n---Integers: ").append(system.MapMaster.represent(intMap))
                .append("\n---Booleans: ").append(system.MapMaster.represent(boolMap)).toString();
    }

    protected Object initValue(String key, Object o) {
        Object val = getRawValue(o);

        String s = StringConsts.MIN_BASE_MAX_SEPARATOR;
        if (o.toString().contains(s) && !o.toString().contains("=")){
            //triplet value
            Iterator<String> iterator = Arrays.asList(new String[]{"min", "base", "max"}).iterator();
            for (String str : o.toString().split(s)) {
                key = StringConsts.checkValueNameReplacement(key);
                initValue( key + "_"+iterator.next(), NumberUtils.getInt(str));
            }
        } else {
            set(key, val);
        }
        return val;
    }

    public void addIntValue(String valueName, Integer value) {
        String[] values = null ;
        if (checkTripleValue(valueName)) {
             values = getTriplet(valueName);
        } else
            values = new String[]{valueName};

        for (String name : values) {
            int prev =getIntOrZero(name);
            int newVal = prev + value;
            intMap.put(name, newVal);
        }
    }

    private String[] getTriplet(String valueName) {
        /*
        blocks, res/def/atk , action value?
        */
        valueName = valueName.toLowerCase();
        if (valueName.endsWith("_all")) {
            String root = valueName.replace("_all", "");
            return new String[]{ root+"_min",root+"_base",root+"_max" };
        }
        return new String[0];
    }

    private boolean checkTripleValue(String valueName) {
        if (valueName.endsWith("_all"))
            return true;
        return false;
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


    public void multiply(String key, Object val) {

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
        if (getterCache.containsKey(key)) {
            return getterCache.get(key).apply(key);
        }
        Integer i = getInt(key);
        if (i != MathConsts.minValue) {
            getterCache.put(key, s -> getInt(s));
            return i;
        }
        Boolean b = getB(key);
        if (b != null) {
            getterCache.put(key, s -> getB(s));
            return b;
        }
        String str = getS(key);
        if (str != null) {
            getterCache.put(key, s -> getS(s));
            return str;
        }
        //TODO NOTE ERROR
        return "";
    }

    public Integer getInt(String key) {
        Integer integer = intMap.get(key.toString());
        if (integer == null) {
            return MathConsts.minValue;
        }
        return integer;
    }

    private Boolean getB(String name) {
        return boolMap.get(name);
    }

    public String getS(Stat stat) {
        return getS(stat.getName());
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
        return  Boolean.TRUE.equals(getB(s));
    }

    public boolean has(String key) {
        return getterCache.containsKey(key);
    }

    public Set<String> keySet() {
        return ListMaster.mergeToSet(intMap.keySet(),
                boolMap.keySet(),
                stringMap.keySet() );
    }
}
