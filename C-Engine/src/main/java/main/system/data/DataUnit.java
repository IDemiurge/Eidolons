package main.system.data;

import main.data.ConcurrentMap;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.WeightMap;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class DataUnit<T extends Enum<T>> implements Serializable {
    public static final String TRUE = "TRUE";
    protected Class<? extends Enum<T>> enumClass;
    protected Class<? extends T> enumClazz;
    protected Map<String, String> values = new ConcurrentMap<>();
    // Map<T, String>
    protected String[] relevantValues;

    public DataUnit() {

    }

    public DataUnit(String text) {
        setData(text);
    }

    public String getContainerValue(T t, int index) {
        List<String> values = getContainerValues(t);
        if (values.size() <= index)
            return null;
        return values.get(index);
    }

    public <S> S getEnum(T value, Class<S> clazz) {
        return getEnum(value.toString(), clazz);
    }

    public <S> S getEnum(String value, Class<S> clazz) {
        return new EnumMaster<S>().retrieveEnumConst(clazz,
                getValue(value));
    }

    public List<String> getContainerValues(T t) {
        return
                ContainerUtils.openContainer(getValue(t));
    }

    public T getKeyConst(String name) {
        return new EnumMaster<T>().retrieveEnumConst(getEnumClazz(), name);
    }

    public Class<? extends T> getEnumClazz() {
        return enumClazz;
    }

    public boolean getBooleanValue(T t) {
        return StringMaster.getBoolean(getValue(t));
    }

    public boolean getBooleanValue(String t) {
        return StringMaster.getBoolean(getValue(t));
    }

    public int getIntValue(String value) {
        String val = getValue(value);
        if (StringMaster.isEmpty(val)) {
            return 0;
        }
        if (!NumberUtils.isInteger(val))
            return 0;
        return NumberUtils.getInteger(val);
    }

    public float getFloatValue(T value) {
        float val;
        try {
            val = Float.parseFloat(getValue(value));
        } catch (Exception e) {
            val = new Float(getIntValue(value));
        }
        return val;
    }

    public String[] getRelevantValues() {
        if (relevantValues == null) {
            return getValueConsts();
        }
        return relevantValues;
    }

    protected String[] getValueConsts() {
        return Arrays.stream(getEnumClazz().getEnumConstants()).map(constant -> constant.toString()).
                collect(Collectors.toList()).toArray(new String[0]);
    }

    public int getIntValue(T value) {
        return getIntValue(value.name());
    }

    public T getEnumConst(String string) {
        if (enumClass == null) {
            enumClass = getEnumClazz();
            if (enumClass == null)
                return null;
        }
        return (T) new EnumMaster<>().getEnum(string, getEnumClazz().getEnumConstants());
    }

    @Override
    public String toString() {
        return getData();
    }

    // email, ip .. ??

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;

    }

    public String removeValue(T t) {
        return removeValue(t.name());
    }

    public String removeValue(String name) {
        return values.remove(name);
    }


    public DataUnit<T> setValue(T name, String value) {
        if (value == null) {
            removeValue(name);
        }
        return setValue(name.name(), value);
    }


    public void addValue(T name, String value) {
        MapMaster.addToStringMap(values, name.name(), value, ",");
    }

    public DataUnit<T> setValue(String name, String value) {
        values.put(name, value);
        return this;
    }

    public String getValue(T t) {
        if (t == null) {
            return "";
        }
        String val = values.get(t.name());
        if (val == null) {
            return "";
        }
        return val;
    }

    public String getValue(String name) {
        return values.get(name);
    }


    protected String getPairSeparator() {
        return DataUnitFactory.getPairSeparator(getFormat());
    }

    protected String getSeparator() {
        return DataUnitFactory.getSeparator(getFormat());
    }

    public DataUnit<T> setData(String data) {
        String[] entries = data.split(getSeparator());
        for (String entry : entries) {
            String[] pair = entry.split(getPairSeparator());
            if (pair.length != 2) {
                //                format=
                LogMaster.log(0, "malformed data:" + entry);
                handleMalformedData(entry);
                continue;
            }

            String name = pair[0];
            name = name.trim();
            setValue(name, pair[1]);
        }
        return this;
    }

    protected void handleMalformedData(String entry) {
    }

    public void addAverage(T stat, int val, int count) {
        int prev = getIntValue(stat);
        int newVal = (prev * (count - 1) + val) / count;
        setValue(stat, newVal + "");

    }

    public void addCount(T stat, String val) {
        addCount(stat, val, 1);
    }

    public void addToInt(T stat, int n) {
        setValue(stat, (getIntValue(stat) + n) + "");
    }

    public void addCount(T stat, String val, int max) {
        addValue(stat, val);
        if (val.contains(": ")) {
            val = val.split(": ")[0];
        }
        T mapVal = getEnumConst(stat + "_MAP");
        if (mapVal == null) {
            return;
        }
        WeightMap<String> map = new WeightMap<>(getValue(mapVal), String.class);
        MapMaster.addToIntegerMap(map, val, 1);
        if (max != 0)
            if (map.get(val) > max)
                map.put(val, 0);
        map.setSeparator(DataUnitFactory.getSeparator(false));
        setValue(mapVal, map.toString());

    }

    public Map<Coordinates, ObjType> buildObjCoordinateMapFromString(String string) {

        Map<Coordinates, ObjType> objMap = new LinkedHashMap<>();
        for (String item : string.split(StringMaster.getAltSeparator())) {

            Coordinates c = Coordinates.get(item.split(StringMaster.getAltPairSeparator())[0]);
            String objTypeName = item.split(StringMaster.getAltPairSeparator())[1];
            ObjType objType = DataManager.getType(objTypeName);
            objMap.put(c, objType);
        }
        return objMap;
    }

    protected String createObjCoordinateString(Map<Coordinates, ObjType> objMap, boolean invert) {
        StringBuilder string = new StringBuilder();
        for (Coordinates c : objMap.keySet()) {
            String objTypeName = objMap.get(c).getName();
            if (invert) {
                c.invert();
            }
            string.append(c.toString());
            string.append(StringMaster.getAltPairSeparator());
            string.append(objTypeName);
            string.append(StringMaster.getAltSeparator());

        }
        string = new StringBuilder(string.substring(0, string.length() - 1));
        string.append(StringMaster.getDataUnitSeparator());
        return string.toString();
    }

    public String getData() {

        return getData(values.keySet());
    }


    public Boolean getFormat() {
        return true;
    }

    public String getData(Boolean format) {
        return getData(values.keySet(),
                DataUnitFactory.getPairSeparator(format),
                DataUnitFactory.getSeparator(format));
    }

    public String getData(Set<String> set) {
        return getData(set, getPairSeparator(), getSeparator());
    }


    public String getData(Set<String> set, String pairSeparator, String separator) {
        StringBuilder data = new StringBuilder();
        for (String v : set) {
            data.append(v).append(pairSeparator).append(values.get(v)).append(separator);
        }
        return data.toString();
    }

    public String getDataMapFormat() {
        StringBuilder data = new StringBuilder();
        for (String p : getValues().keySet()) {
            data.append(p).append(StringMaster.wrapInParenthesis(getValue(p))).append(StringMaster.getSeparator());
        }
        return data.toString();
    }

    public String getDataAltFormat() {
        StringBuilder data = new StringBuilder();
        for (String p : getValues().keySet()) {
            data.append(p).append(StringMaster.getAltPairSeparator()).append(getValue(p)).append(StringMaster.getAltSeparator());
        }
        return data.toString();

    }

    public String getRelevantData() {
        return getData(new HashSet<>(Arrays.asList(getRelevantValues())));

    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DataUnit) {
            DataUnit unit = (DataUnit) o;
            return unit.getData().equals(this.getData());
        }
        return false;
    }

    public WeightMap getWeightMapValue(T val) {
        return new WeightMap(getValue(val), String.class);
    }

    public void setValue(T name, Object val) {
        if (val == null) {
            removeValue(name);
        } else
            setValue(name, val.toString());
    }

    public DataUnit<T> clear() {
        values.clear();
        return this;
    }

    public String getDataExcept(String... exceptions) {
        Set<String> set = new LinkedHashSet<>(values.keySet());
        for (String exception : exceptions) {
            set.remove(exception);
        }
        return getData(set);
    }


    public enum GAME_VALUES {
        HOST_NAME, TITLE, HOST_IP, STARTED, PLAYERS_NUMBER,
    }
}
