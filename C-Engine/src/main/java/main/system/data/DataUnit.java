package main.system.data;

import main.data.ConcurrentMap;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;

import java.util.*;

public class DataUnit<T extends Enum<T>> {
    public static final String TRUE = "TRUE";
    protected Class<? extends Enum<T>> enumClass;
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
       if (values.size()<=index)
           return null ;
        return values.get(index);
    }
        public List<String> getContainerValues(T t) {
      return
            StringMaster.openContainer(getValue(t));
    }public boolean getBooleanValue(T t) {
        return StringMaster.getBoolean(getValue(t));
    }

    public int getIntValue(String value) {
        String val = getValue(value);
        if (StringMaster.isEmpty(val)) {
            return 0;
        }
        return StringMaster.getInteger(val);
    }

    public int getIntValue(T value) {
        return getIntValue(value.name());
    }

    public T getEnumConst(String string) {
        if (enumClass == null) {
            return null;
        }
        return (T) new EnumMaster<>().getEnum(string, enumClass.getEnumConstants());
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



    public void setValue(T name, String value) {
        if (value == null) {
            removeValue(name);
        }
        values.put(name.name(), value);
    }
    public void addValue(T name, String value) {
      MapMaster.addToStringMap(values, name.name(), value);
    }

    public void setValue(String name, String value) {
        values.put(name, value);
    }

    public String getValue(T t) {
        return values.get(t.name());
    }

    public String getValue(String name) {
        return values.get(name);
    }



    public void setData(String data, Boolean std_alt_map) {
        String[] entries = data.split(DataUnitFactory.getSeparator(std_alt_map));
        for (String entry : entries) {
            String[] pair = entry.split(DataUnitFactory.getPairSeparator(std_alt_map));
            if (pair.length != 2) {
                LogMaster.log(4, "malformed data:" + data);
                continue;
            }

            String name = pair[0];

            setValue(name, pair[1]);
        }
    }

    public Map<Coordinates, ObjType> buildObjCoordinateMapFromString(String string) {

        Map<Coordinates, ObjType> objMap = new LinkedHashMap<>();
        for (String item : string.split(StringMaster.getAltSeparator())) {

            Coordinates c = new Coordinates(item.split(StringMaster.getAltPairSeparator())[0]);
            String objTypeName = item.split(StringMaster.getAltPairSeparator())[1];
            ObjType objType = DataManager.getType(objTypeName);
            objMap.put(c, objType);
        }
        return objMap;
    }

    protected String createObjCoordinateString(Map<Coordinates, ObjType> objMap, boolean invert) {
        String string = "";
        for (Coordinates c : objMap.keySet()) {
            String objTypeName = objMap.get(c).getName();
            if (invert) {
                c.invert();
            }
            string += c.toString();
            string += StringMaster.getAltPairSeparator();
            string += objTypeName;
            string += StringMaster.getAltSeparator();

        }
        string = string.substring(0, string.length() - 1);
        string += StringMaster.getDataUnitSeparator();
        return string;
    }

    public String getData() {

        return getData(values.keySet());
    }

    public void setData(String data) {
        setData(data, getFormat());
    }

    public String getData(Boolean format) {
        return getData(values.keySet(), format);
    }

    public String getData(Set<String> set) {
        return getData(set, getFormat());
    }

    // std_alt_map
    public Boolean getFormat() {
        return true;
    }

    public String getData(Set<String> set, Boolean format) {
        String data = "";
        for (String v : set) {
            data += v + DataUnitFactory.getPairSeparator(format) + values.get(v) + DataUnitFactory.getSeparator(format);
        }
        return data;
    }

    public String getDataMapFormat() {
        String data = "";
        for (String p : getValues().keySet()) {
            data += p + StringMaster.wrapInParenthesis(getValue(p)) + StringMaster.getSeparator();
        }
        return data;
    }

    public String getDataAltFormat() {
        String data = "";
        for (String p : getValues().keySet()) {
            data += p + StringMaster.getAltPairSeparator() + getValue(p)
                    + StringMaster.getAltSeparator();
        }
        return data;

    }

    public String getRelevantData() {
        return getData(new HashSet<>(Arrays.asList(relevantValues)));

    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DataUnit) {
            DataUnit unit = (DataUnit) o;
            if (unit.getData().equals(this.getData())) {
                return true;
            }
        }
        return false;
    }

    public enum GAME_VALUES {
        HOST_NAME, TITLE, HOST_IP, STARTED, PLAYERS_NUMBER,
    }
}
