package main.system.options;

import main.system.data.DataUnit;

public abstract class Options<T extends Enum<T>> extends DataUnit<T> {

    public abstract Class<Boolean> getValueClass(Enum option);

}
