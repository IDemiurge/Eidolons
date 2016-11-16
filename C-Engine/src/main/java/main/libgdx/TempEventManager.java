package main.libgdx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * Date: 04.11.2016
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class TempEventManager {
    private static Map<String, EventCallback> eventMap = new HashMap<>();
    private static List<Runnable> eventQueue = new ArrayList<>();
    private static Lock lock = new ReentrantLock();

    public static void bind(String name, EventCallback event) {
        if (event != null) {
            if (eventMap.containsKey(name)) {
                eventMap.remove(name);
            }
            eventMap.put(name, event);
        } else {
            if (eventMap.containsKey(name)) {
                eventMap.remove(name);
            }
        }
    }

    public static void trigger(final String name, final Object obj) {
        if (eventMap.containsKey(name)) {
            lock.lock();
            eventQueue.add(new Runnable() {
                @Override
                public void run() {
                    eventMap.get(name).call(obj);
                }
            });
            lock.unlock();
        }
    }

    public static void processEvents() {
        if (eventQueue.size() > 0) {
            lock.lock();
            List<Runnable> list = eventQueue;
            eventQueue = new ArrayList<>();
            lock.unlock();
            for (Runnable runnable : list) {
                runnable.run();
            }
        }
    }
}
