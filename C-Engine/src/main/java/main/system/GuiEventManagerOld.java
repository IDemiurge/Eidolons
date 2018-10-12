package main.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static main.system.GuiEventType.DISPOSE_TEXTURES;
import static main.system.GuiEventType.SCREEN_LOADED;
import static main.system.GuiEventType.SWITCH_SCREEN;

public class GuiEventManagerOld {
    private static final GuiEventType[] savedBindings = new GuiEventType[]{
     SWITCH_SCREEN,
     SCREEN_LOADED,
     DISPOSE_TEXTURES,
    };
    private static GuiEventManagerOld instance;
    private static boolean isInitialized;
    private static Lock initLock = new ReentrantLock();
    private Map<EventType, EventCallback> eventMap = new HashMap<>();
    private List<Runnable> eventQueue = new ArrayList<>();
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private Map<EventType, EventCallbackParam> onDemand = new ConcurrentHashMap<>();

    public static void cleanUp() {
        getInstance()._cleanUp();
    }

    public static void bind(EventType type, final EventCallback event) {
        getInstance().bind_(type, event);
    }

    public static void removeBind(EventType type) {
        getInstance().removeBind_(type);
    }

    public static void trigger(final EventType type, Object obj) {
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

    public static GuiEventManagerOld getInstance() {
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
        instance = new GuiEventManagerOld();
    }

    private void removeBind_(EventType type) {
        eventMap.remove(type);
    }

    private void _cleanUp() {
//        try {
//            condition.await();
//        } catch (InterruptedException e) {
//            main.system.ExceptionMaster.printStackTrace(e);
//        }
        Map<EventType, EventCallback> cache = new HashMap<>();
        for (EventType eventType : savedBindings) {
            EventCallback saved = eventMap.get(eventType);
            cache.put(eventType, saved);
        }
        eventMap.clear();
        eventQueue.clear();
        onDemand.clear();

        for (EventType eventType : cache.keySet()) {
            eventMap.put(eventType, cache.get(eventType));
        }
    }

    public void bind_(EventType type, final EventCallback event) {
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
            if (onDemand.containsKey(type)) {
                EventCallbackParam r = onDemand.remove(type);
                eventQueue.add(() -> event.call(r));
//                main.system.auxiliary.log.LogMaster.log(1,
//                 "onDemand triggered for " + type);
//                r.call(null);

//                lock.lock();
//                EventCallback r = onDemand.remove(type);
//                try {
//                    eventQueue.add(() -> r.call(event));
//                } catch (Exception e) {
//                    main.system.ExceptionMaster.printStackTrace(e);
//                } finally {
//                    lock.unlock();
//                }
            }
        } else {
            if (eventMap.containsKey(type)) {
                eventMap.remove(type);
            }
        }
    }

    public void trigger_(final EventType type, final EventCallbackParam obj) {
        EventCallback event = eventMap.get(type);
        if (event != null) {
            lock.lock();
            try {
                eventQueue.add(() -> event.call(obj));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            } finally {
                lock.unlock();

            }
        } else {
//            if (obj instanceof OnDemandCallback) {
            onDemand.put(type, obj);
//            }
        }
    }

    public void processEvents_() {
        if (eventQueue.size() > 0) {
            lock.lock();
            List<Runnable> list = eventQueue;
            eventQueue = new ArrayList<>();
            lock.unlock();

//            list.forEach(Runnable::run); apparently we still need crutches
            for (Runnable runnable : list) {
                try {
                    runnable.run();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
        }
    }

}