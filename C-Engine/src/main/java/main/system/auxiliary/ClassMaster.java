package main.system.auxiliary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ClassMaster<T> {

    public static boolean isInstanceOf(Object object, Class<?> CLASS) {
        if (object == null || CLASS == null) {
            return false;
        }
        Class<? extends Object> OBJ_CLASS = object.getClass();
        while (OBJ_CLASS != null) {
            if (OBJ_CLASS != Object.class)
                if (OBJ_CLASS.equals(CLASS)) {
                    return true;
                }
            OBJ_CLASS = OBJ_CLASS.getSuperclass();
        }
        return false;
    }

    public static  <T> List<T> getInstancesFromCollection(Object object, Class<?> CLASS) {
        return new ClassMaster<T>().getInstancesFromCollection_(object, CLASS);
    }
    public   List<T> getInstancesFromCollection_(Object object, Class<?> CLASS) {
        List<T> list = new ArrayList<>();

        if (isInstanceOf(object, CLASS)) {
            return
                    (List<T>) Collections.singletonList(object);
            // return new ListMaster<T>().asList((T) object);
        }
        if (object instanceof Iterable) {
            Iterable collection = (Iterable) object;
            for (Object o : collection) {
                list.addAll(getInstancesFromCollection_(o, CLASS));
            }
        }

        return list;
    }
    public static boolean hasInstanceOf(Object object, Class<?> CLASS) {
        if (isInstanceOf(object, CLASS)) {
            return true;
        }
        if (object instanceof Collection) {
            Collection collection = (Collection) object;
            for (Object o : collection) {
                if (isInstanceOf(o, CLASS)) {
                    return true;
                }
            }
        }
        return false;
    }



}
