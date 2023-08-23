package framework.entity;

import content.LinkedStringMap;
import elements.stats.generic.Stat;

import java.util.Map;

/**
 * Created by Alexander on 8/22/2023
 * maybe separate basic version from one with _Base and _Cur?
 */
public class EntityData extends framework.data.TypeData {

    private final Map<String, Integer> intMapCur = new LinkedStringMap<>();

    private  Map<String, Integer> intMapBase ;
    private  Map<String, String> stringMapBase ;
    private  Map<String, Boolean> boolMapBase ;


    public EntityData(Map<String, Object> valueMap) {
        super(valueMap);
    }

    protected Object initValue(String key, Object o) {
        Object val = super.initValue(key, o);
        setBase(key, val);
        return val;
    }
    public void setCur(String key, int val) {
        intMapCur.put(key, val);
    }

    public Integer getInt(String key) {
        Integer integer = intMapCur.get(key);
        if (integer != null)
            return integer;
        return super.getInt(key);
    }

    private void setBase(String s, Object o) {
        if (stringMapBase==null)
            stringMapBase = new LinkedStringMap<>();
        if (intMapBase==null)
            intMapBase = new LinkedStringMap<>();
        if (boolMapBase==null)
            boolMapBase = new LinkedStringMap<>();

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
        //what if there are some new values?.. all via continuous fx only
        stringMap.putAll(stringMapBase);
    }

    //////////////// region MODIFY ///////////////////
    public void addCurValue(Stat key, int i) {
        intMapCur.put(key.getName(), intMapCur.get(key) + i);
    }


    //endregion
}
