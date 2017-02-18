package main.system.auxiliary;

import main.system.auxiliary.data.ListMaster;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ClassMaster {

    public static boolean isInstanceOf(Object object, Class<?> CLASS) {
        if (object == null || CLASS == null) {
            return false;
        }
        Class<? extends Object> OBJ_CLASS = object.getClass();
        while (OBJ_CLASS != null) {
            if (OBJ_CLASS.equals(CLASS)) {
                return true;
            }
            OBJ_CLASS = OBJ_CLASS.getSuperclass();
        }
        return false;
    }

    public static List<Object> getInstances(Object object, Class<?> CLASS) {
        List<Object> list = new LinkedList<>();

        if (isInstanceOf(object, CLASS)) {
            return ListMaster.toList(object);
        }
        if (object instanceof Collection) {
            Collection collection = (Collection) object;
            for (Object o : collection) {
                list.add(getInstances(o, CLASS));
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
