package main.libgdx;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Date: 04.11.2016
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class TempEventManager {
    private static Map<Class, EventCallback> eventMap = new HashMap<>();


    public static void bind(Class tClass, EventCallback event) {
        if (event != null) {
            if (eventMap.containsKey(tClass)) {
                eventMap.remove(tClass);
            }
            eventMap.put(tClass, event);
        } else {
            if (eventMap.containsKey(tClass)) {
                eventMap.remove(tClass);
            }
        }
    }

    public static void trigger(Object obj) {
        Class c = obj.getClass();
        if (eventMap.containsKey(c)) {
            eventMap.get(c).call(obj);
        }
    }
}
