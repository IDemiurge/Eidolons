package eidolons.system.options;

import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.data.DataUnit;
import eidolons.system.options.Options.OPTION;

public abstract class Options<E extends Enum<E>, T extends OPTION> extends DataUnit<E> {


    public T getKey(String name) {
        return new EnumMaster<T>().retrieveEnumConst(getOptionClass(), name);
    }

//    @Override
//    public boolean getBooleanValue(E e) {
//        boolean bool = getBoolMap().get(t);
//        if (bool==null )
//        {
//            bool=super.getBooleanValue(e);
//            get
//        }
//        return bool;
//    }

    protected abstract Class<? extends T> getOptionClass();


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
            return StringMaster.getWellFormattedString(toString());
        }
    }
}
