package main.system;

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
public class GuiEventManager<T> {
    private static Map<GuiEventType, EventCallback> eventMap = new HashMap<>();
    private static List<Runnable> eventQueue = new ArrayList<>();
    private static Lock lock = new ReentrantLock();

    public static void bind(GuiEventType type, final EventCallback event) {
        if (event != null) {
            if (eventMap.containsKey(type)) {
                final EventCallback old = eventMap.remove(type);
                eventMap.put(type, (obj) -> {
                    old.call(obj);
                    event.call(obj);
                });
            } else {
                eventMap.put(type, event);
            }
        } else {
            if (eventMap.containsKey(type)) {
                eventMap.remove(type);
            }
        }
    }


    public static void trigger(final GuiEventType type, final EventCallbackParam obj) {
        if (eventMap.containsKey(type)) {
            lock.lock();
            eventQueue.add(() -> eventMap.get(type).call(obj));
            lock.unlock();
        }
    }

    public static void processEvents() {
        if (eventQueue.size() > 0) {
            lock.lock();
            List<Runnable> list = eventQueue;
            eventQueue = new ArrayList<>();
            lock.unlock();

            list.forEach(Runnable::run);
        }
    }
}
