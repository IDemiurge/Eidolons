package main.system.data;

import main.data.XLinkedMap;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;

import java.util.Map;

/**
 * Created by JustMe on 5/11/2017.
 */
public class DataUnitFactory<E extends DataUnit> {
    Boolean format = true;
    private Object[] valueNames;
    private String[] values;

    public DataUnitFactory(Boolean format) {
        this.format = format;
    }

    public DataUnitFactory() {
    }

    public static String getContainerSeparator(Boolean std_alt_map) {
        return std_alt_map ?
         StringMaster.AND_SEPARATOR : StringMaster.getSeparator();
    }

    public static String getSeparator(Boolean std_alt_map) {
        return std_alt_map ? StringMaster.getSeparator() : StringMaster.getAltSeparator();
    }

    public static String getPairSeparator(Boolean std_alt_map) {
        return std_alt_map ? StringMaster.getPairSeparator() : StringMaster.getAltPairSeparator();
    }

    public static String getKeyValueString(Boolean format, Object o, String value) {
        StringBuilder builder = new StringBuilder();
        builder.append(o.toString());
        builder.append(getPairSeparator(format));
        builder.append(value);
        builder.append(getSeparator(format));
        return builder.toString();

    }

    public void setValueNames(Object... valueNames) {
        this.valueNames = valueNames;
    }

    public DataUnitFactory<E> setValues(String... values) {
        this.values = values;
        return this;
    }

    public void setFormat(Boolean format) {
        this.format = format;
    }

    public Map<String, String> deconstructDataString(String dataString) {
        Map<String, String> map = new XLinkedMap<>();
        for (String substring : ContainerUtils.openContainer(dataString)) {
            String[] s = substring.split(getPairSeparator(format));
            if (s.length<2){
                continue;
            }
            String key = s[0];
            String value = s[1];
            map.put(key, value);
        }
        return map;
    }

    public String constructDataString() {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (Object o : valueNames) {
            builder.append(getKeyValueString(format, o, values[i]));
            builder.append(o.toString());
            builder.append(getPairSeparator(format));
            builder.append(values[i]);
            builder.append(getSeparator(format));
            i++;
        }
        return builder.toString();
    }

}
