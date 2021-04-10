package main.ability;

import main.system.data.DataUnit;
import main.system.data.DataUnitFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public final class StringsContainer {
    //TODO support all primitives! And FORMULA
    private final Boolean format= false;
    DataUnit data;

    public StringsContainer(List<String> names, List<String> values) {
         init(names.toArray(),values.toArray(new String[0]));
        }

    public StringsContainer() {
        data = new DataUnit();
    }

    public DataUnit setValue(String name, String value) {
        return data.setValue(name, value);
    }

    public String getValue(String name) {
        return data.getValue(name);
    }

    public StringsContainer init(Object[] names, String[] vals) {
        data = new DataUnitFactory<>(format).setValueNames(names).setValues(vals).create();
        return this;
    }

    public void unpack(Object o, Class<?> clazz) {
        for (Field field : clazz.getFields()) {
            if (field.getType() == String.class) {
                String value = data.getValue(field.getName());
                field.setAccessible(true);
                try {
                    field.set(o, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}

