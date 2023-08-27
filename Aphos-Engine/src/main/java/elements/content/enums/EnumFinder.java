package elements.content.enums;

import elements.content.enums.types.CombatTypes;
import elements.content.enums.types.EntityTypes;
import elements.content.enums.types.MiscTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 8/21/2023
 */
public class EnumFinder {
    public static final Class[] enumClasses = {
            CombatTypes.class,
            EntityTypes.class,
            MiscTypes.class,
            FieldConsts.class
    };
    private static Map<String, Object> enumMap= new HashMap<>();

    //is it any use? Maybe not :)
    public static void initEnumMap(){
        for (Class clazz : enumClasses) {
            Class[] array = clazz.getDeclaredClasses();
            for (Class enumClass : array) {
                Object[] enumConsts = enumClass.getEnumConstants();
                for (Object t : enumConsts) {
                    //Can we be sure there are no 2 same names?
                    if (enumMap.put(t.toString(), t)!=null){
                        throw new RuntimeException("Duplicate Enum const: " + t);
                    }
                }
            }
        }
    }
    public static <T> T get(Class<T> clazz, Object value) {
        T[] array = clazz.getEnumConstants();
            String s = value.toString();
        for (T t : array) {
            if (t.toString().equalsIgnoreCase(s))
                return t;
        }

        s = s.replace(" ", "_");
        for (T t : array) {
            if (t.toString().equalsIgnoreCase(s))
                return t;
        }
        return null;
    }
}
