package main.system.data;

import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 5/11/2017.
 */
public class DataUnitFactory<E extends DataUnit> {
    private Object[] valueNames;
    private String[] values;
    Boolean format = true;

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
            builder.append(value );
            builder.append(getSeparator(format));
        return builder.toString();

    }
    public void setValueNames(Object... valueNames) {
        this.valueNames = valueNames;
    }

    public void setValues(String... values) {
        this.values = values;
    }

    public void setFormat(Boolean format) {
        this.format = format;
    }

    public String constructDataString() {
        StringBuilder builder = new StringBuilder();
    int i =0;
        for (Object o : valueNames) {
            builder.append(getKeyValueString(format, o, values[i]));
            i++;
        }
        return builder.toString();
    }

}
