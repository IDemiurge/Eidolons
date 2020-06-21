package eidolons.system.options;

import eidolons.system.options.Options.OPTION;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.data.DataUnit;

public abstract class Options<E extends Enum<E>, T extends OPTION> extends DataUnit<E> {


    public T getKey(String name) {
        return new EnumMaster<T>().retrieveEnumConst(getOptionClass(), name);
    }

//    @Override
//    public boolean getBooleanValue(E e) {
//        boolean bool = getBoolMap().getVar(t);
//        if (bool==null )
//        {
//            bool=super.getBooleanValue(e);
//            getVar
//        }
//        return bool;
//    }

    protected abstract Class<? extends T> getOptionClass();

    @Override
    public String getValue(E e) {
        String v = super.getValue(e);
        if (v == null) {
            return "";
        }
        return v;
    }

    @Override
    public String getValue(String name) {
        String v = super.getValue(name);
        if (v == null) {
            return "";
        }
        return v;
    }

    public Class<?> getValueClass(OPTION option) {
        if (option.isExclusive() != null) {
            return Boolean.class;
        }
        if (option.getMax() != null) {
            return Integer.class;
        }
        if (option.getOptions() != null) {
            return String.class;
        }

        if (option.getDefaultValue() != null) {
            return option.getDefaultValue().getClass();
        }
        return null;
    }

    public interface OPTION {
        Integer getMin();

        Integer getMax();

        Object getDefaultValue();

        Boolean isExclusive();

        Object[] getOptions();

        default String getName() {
            return StringMaster.format(toString());
        }

        default boolean isHidden(){
            return false;
        }
        default boolean isDevOnly(){
            return false;
        }

        default String getTooltip(){
            return getName();
        }
    }
}
