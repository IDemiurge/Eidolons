package main.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * Date: 04.11.2016
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class GuiEventManager {
    private static GuiEventManager instance;
    private static boolean isInitialized;
    private static Lock initLock = new ReentrantLock();
    private Map<GuiEventType, EventCallback> eventMap = new HashMap<>();
    private List<Runnable> eventQueue = new ArrayList<>();
    private Lock lock = new ReentrantLock();
    private Map<GuiEventType, OnDemandCallback> onDemand = new ConcurrentHashMap<>();

    public static void bind(GuiEventType type, final EventCallback event) {
        getInstance().bind_(type, event);
    }

    public static void trigger(final GuiEventType type, Object obj) {
        EventCallbackParam eventCallback;

        if (obj instanceof EventCallbackParam) {
            eventCallback = (EventCallbackParam) obj;
        } else {
            eventCallback = new EventCallbackParam(obj);
        }

        getInstance().trigger_(type, eventCallback);
    }

    public static void processEvents() {
        getInstance().processEvents_();
    }

    public static GuiEventManager getInstance() {
        if (instance == null) {
            if (!isInitialized) {
                try {
                    initLock.lock();
                    if (!isInitialized) {
                        init();
                        isInitialized = true;
                    }
                } finally {
                    initLock.unlock();
                }
            }
        }
        return instance;
    }

    private static void init() {
        instance = new GuiEventManager();
    }

    public void bind_(GuiEventType type, final EventCallback event) {
        if (event != null) {
            if (onDemand.containsKey(type)) {
                lock.lock();
                OnDemandCallback r = onDemand.remove(type);
                try {
                    eventQueue.add(() -> r.call(event));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
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

    public void trigger_(final GuiEventType type, final EventCallbackParam obj) {

        if (eventMap.containsKey(type)) {
            lock.lock();
            try {
                eventQueue.add(() -> eventMap.get(type).call(obj));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        } else {
            if (obj instanceof OnDemandCallback) {
                onDemand.put(type, (callback) -> callback.call(obj));
            }
        }
    }

    public void processEvents_() {
        if (eventQueue.size() > 0) {
            lock.lock();
            List<Runnable> list = eventQueue;
            eventQueue = new ArrayList<>();
            lock.unlock();

            list.forEach(Runnable::run);
        }
    }

}
