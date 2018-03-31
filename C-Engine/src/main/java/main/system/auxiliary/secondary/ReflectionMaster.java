package main.system.auxiliary.secondary;

import java.lang.reflect.Field;

/**
 * Created by JustMe on 1/27/2017.
 */
public class ReflectionMaster<T> {
    public T getFieldValue(String fieldName, Object thisObject, Class source) {
        try {
            Field field = source.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(thisObject);

            field.setAccessible(false);

            if (value == null) {
                return null;
            }
//            else if (clazz.isAssignableFrom(value.getClass())) {
            return (T) value;
//            }
//            throw new RuntimeException("Wrong value");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setValue(String fieldName, T value, Object obj) {
        setValue(fieldName, value, obj, obj == null ? null : obj.getClass());
    }

    public void setValue(String fieldName, T value, Object obj, Class<?> clazz) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz != null)
                if (clazz.getSuperclass() != null) {
                    setValue(fieldName, value, obj, clazz.getSuperclass());
                    return;
                } else {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
        }
        field.setAccessible(true);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }
}
