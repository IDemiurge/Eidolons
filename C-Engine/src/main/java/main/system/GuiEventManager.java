package main.system;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
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
    private static Lock initLock = new ReentrantLock();
    private Map<GuiEventType, EventCallback> eventMap = new HashMap<>();
    private List<Runnable> eventQueue = new ArrayList<>();
    private Lock lock = new ReentrantLock();
    private Map<GuiEventType, List<EventCallback>> onceBinds = new HashMap<>();

    public static void bind(GuiEventType type, final EventCallback event) {
        getInstance().bind_(type, event);
    }

    public static void once(GuiEventType type, final EventCallback event) {
        getInstance().once_(type, event);
    }

    private void once_(GuiEventType type, EventCallback event) {
        onceBinds
                .getOrDefault(type, new LinkedList<>())
                .add(event);
    }

    public static void trigger(final GuiEventType type) {
        trigger(type, new EventCallbackParam(null));
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

    private static GuiEventManager getInstance() {
        if (instance == null) {
            try {
                initLock.lock();
                if (instance == null) {
                    instance = new GuiEventManager();
                }
            } finally {
                initLock.unlock();
            }
        }
        return instance;
    }

    public static void clear() {
        instance = null;
    }

    public void bind_(GuiEventType type, final EventCallback event) {
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

    List<Pair<GuiEventType, EventCallbackParam>> triggerList = new LinkedList<>();

    public void trigger_(final GuiEventType type, final EventCallbackParam obj) {
        triggerList.add(new ImmutablePair<>(type, obj));
    }

    public void processEvents_() {
        if (onceBinds.size() > 0) {
            lock.lock();
            final Map<GuiEventType, List<EventCallback>> map = onceBinds;
            triggerList.forEach(pair -> {
                final List<EventCallback> callbacks = map.get(pair.getKey());
                if (callbacks != null) {
                    callbacks.forEach(
                            eventCallback -> eventCallback.call(pair.getValue())
                    );
                    map.remove(pair.getKey());
                }
            });
        }

        if (triggerList.size() > 0) {
            lock.lock();
            List<Runnable> list = eventQueue;
            eventQueue = new ArrayList<>();
            lock.unlock();

            list.forEach(Runnable::run);
        }
    }
}
