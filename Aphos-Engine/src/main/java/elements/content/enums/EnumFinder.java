package elements.content.enums;

import elements.content.enums.types.EntityTypes;

/**
 * Created by Alexander on 8/21/2023
 */
public class EnumFinder {
    public static <T> T get(Class<T> clazz, Object value) {
        // EntityTypes.class.getDeclaredClasses()
        T[] array = clazz.getEnumConstants();
        for (T t : array) {
            if (t.toString().equalsIgnoreCase(value.toString()))
                return t;
        }
        return null;
    }

    //     for (Class<?> CLASS : CONSTS_CLASS.getDeclaredClasses()) {
    //     if (CLASS.isEnum()) {
    //         if (CLASS.getSimpleName().equalsIgnoreCase(name)) {
    //             return CLASS;
    //         }
    //     }
    // }
}
