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
}
