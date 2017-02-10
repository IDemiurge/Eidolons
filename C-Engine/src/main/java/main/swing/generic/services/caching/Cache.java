package main.swing.generic.services.caching;

import java.util.HashMap;
import java.util.Map;

public class Cache {

    private static Map<Object, Object> objTypeComponents = new HashMap<>();

    public static Map<Object, Object> getObjTypeComponents() {
        return objTypeComponents;
    }

}
