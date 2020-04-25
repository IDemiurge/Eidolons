package main.system;

import main.system.auxiliary.data.MapMaster;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static main.system.GuiEventType.*;

public class GuiEventManagerImpl {
    private static final GuiEventType[] savedBindings = new GuiEventType[]{
            SWITCH_SCREEN,
            SCREEN_LOADED,
            DISPOSE_TEXTURES,
    };
    private static GuiEventManagerImpl instance;
    private static boolean isInitialized;
    private static Lock initLock = new ReentrantLock();
    private Map<EventType, EventCallback> eventMap = new HashMap<>();
    private List<Runnable> eventQueue = new ArrayList<>();
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private Map<EventType, EventCallbackParam> onDemand = new ConcurrentHashMap<>();
    private Map<EventType, List<EventCallbackParam>> onDemandMap = new ConcurrentHashMap<>();

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

    public static GuiEventManagerImpl getInstance() {
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
        instance = new GuiEventManagerImpl();
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
               if (type.isMultiArgsInvocationSupported()){
                    eventMap.put(type, (obj) -> {
                        if (obj.get() instanceof Collection) {
                            Collection list = (Collection) obj.get();
                            main.system.auxiliary.log.LogMaster.log(1,">>>>> MultiArgs Invocation with elements: " +list.size());
                            for (Object o : list) {
                                event.call(new EventCallbackParam(o));
                            }
                        } else {
                            event.call(obj);
                        }
                    });
                } else {
                    eventMap.put(type, event);
                }
            }
//            if (onDemand.containsKey(type)) {
//                EventCallbackParam r = onDemand.remove(type);
//                eventQueue.add(() -> event.call(r));
//            }
            List<EventCallbackParam> callbacks = onDemandMap.get(type);
            if (callbacks != null) {
                onDemandMap.remove(type);
                for (EventCallbackParam callback : callbacks) {
                    if (type.isMultiArgsInvocationSupported()  &&
                    callback.get() instanceof Collection) {
                        eventQueue.add(() -> {
                            for (Object o : (Collection) callback.get()) {
                                event.call(new EventCallbackParam(o));
                            }
                        });
                    } else
                     eventQueue.add(() -> event.call(callback));
                }
            }
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
        } else {
            eventMap.remove(type);
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
//            onDemand.put(type, obj);
            if (isOnDemandCallback(type))
                MapMaster.addToListMap(onDemandMap, type, obj);
//            }
        }
    }

    protected boolean isOnDemandCallback(EventType type) {
        return type != UNIT_CREATED;
    }

    public void processEvents_() {
        if (eventQueue.size() > 0) {
            lock.lock();
            List<Runnable> list = eventQueue;
            eventQueue = new ArrayList<>();
            lock.unlock();

//            list.forEach(Runnable::run); apparently we still need crutches
            for (int i = 0, listSize = list.size(); i < listSize; i++) {
                run( list.get(i));
//                try {
//                    list.get(i).run();
//                } catch (Exception e) {
//                    ExceptionMaster.printStackTrace(e);
//                }
            }
        }
    }

    private void run(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }
    }

}