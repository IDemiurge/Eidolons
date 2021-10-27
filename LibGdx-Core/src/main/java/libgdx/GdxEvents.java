package libgdx;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ObjectMap;
import libgdx.screens.GameScreen;
import libgdx.screens.handlers.ScreenMaster;
import main.system.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static main.system.GuiEventType.*;

public class GdxEvents implements GenericGuiEventManager {
    private static final GuiEventType[] savedBindings = new GuiEventType[]{
            SWITCH_SCREEN,
            SCREEN_LOADED,
    };
    private final ObjectMap<EventType, EventCallback> eventMap = new ObjectMap<>(100);
    private List<Runnable> eventQueue = new LinkedList<>();
    private final Lock lock = new ReentrantLock();

    private final ObjectMap<EventType, EventCallbackParam> onDemand = new ObjectMap<>();
    private final ObjectMap<EventType, List<EventCallbackParam>> onDemandMap = new ObjectMap<>();

    public void cleanUp() {
        _cleanUp();
    }

    public void bind(EventType type, final EventCallback event) {
        if (type.isScreenCheck()) {
            GameScreen screen = ScreenMaster.getScreen();
            EventCallback checked = new EventCallback() {
                @Override
                public void call(EventCallbackParam obj) {
                    if (ScreenMaster.getScreen() != getScreen()) {
                        //                        main.system.auxiliary.log.LogMaster.log(1,type+"Screen check failed " +screen);
                        return;
                    }
                    event.call(obj);
                }

                @Override
                public Screen getScreen() {
                    return screen;
                }
            };
            bind_(type, checked);
        } else
            bind_(type, event);

    }

    public void removeBind(EventType type) {
        removeBind_(type);
    }

    @Override
    public void triggerWithMinDelayBetween(GuiEventType eventType, Object obj, int millis) {
        // Map<EventCallback, Float> event = delayedMap.get(eventType);
        // if (event != null) {
        //     //TODO
        //
        // }
        EventCallbackParam eventCallback;
        if (obj instanceof EventCallbackParam) {
            eventCallback = (EventCallbackParam) obj;
        } else {
            eventCallback = new EventCallbackParam(obj);
        }
        trigger_(eventType, eventCallback);
    }

    public void trigger(final EventType type, Object obj) {
        EventCallbackParam eventCallback;
        if (obj instanceof EventCallbackParam) {
            eventCallback = (EventCallbackParam) obj;
        } else {
            eventCallback = new EventCallbackParam(obj);
        }
        trigger_(type, eventCallback);
    }

    public void processEvents() {
        processEvents_();
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
        ObjectMap<EventType, EventCallback> cache = new ObjectMap<>();
        for (EventType eventType : savedBindings) {
            EventCallback saved = eventMap.get(eventType);
            cache.put(eventType, saved);
        }
        eventMap.clear();
        eventQueue.clear();
        onDemand.clear();

        for (EventType eventType : cache.keys()) {
            eventMap.put(eventType, cache.get(eventType));
        }
    }

    public void bind_(EventType type, final EventCallback event) {
        if (event != null) {

            if (eventMap.containsKey(type)) {
                EventCallback old = eventMap.remove(type);

                eventMap.put(type, (obj) -> {
                    wrap(type, old).call(obj);
                    wrap(type, event).call(obj);
                });
            } else {
                eventMap.put(type, wrap(type, event));
            }
            List<EventCallbackParam> callbacks = onDemandMap.get(type);
            if (callbacks != null) {
                onDemandMap.remove(type);
                for (EventCallbackParam callback : callbacks) {
                    eventQueue.add(() -> wrap(type, event).call(callback));
                }
            }
        } else {
            eventMap.remove(type);
        }
    }

    private EventCallback wrap(EventType type, EventCallback event) {
        if (type.isMultiArgsInvocationSupported()) {
            return (obj) -> {
                if (obj.get() instanceof Collection) {
                    Collection list = (Collection) obj.get();
                    for (Object o : list) {
                        event.call(new EventCallbackParam(o));
                    }
                } else {
                    event.call(obj);
                }
            };
        }
        return event;
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
            if (isOnDemandCallback(type)) {
                List<EventCallbackParam> eventCallbackParams = onDemandMap.get(type);
                if (eventCallbackParams == null) {
                    onDemandMap.put(type, eventCallbackParams = new LinkedList<>());
                }
                eventCallbackParams.add(obj);
            }
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
                run(list.get(i));
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