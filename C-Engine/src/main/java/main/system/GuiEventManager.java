package main.system;

import main.data.XLinkedMap;
import main.system.auxiliary.LogMaster;
import main.system.auxiliary.MapMaster;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * Date: 04.11.2016
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class GuiEventManager<T> {
    private static Map<GraphicEvent, EventCallback> eventMap = new HashMap<>();
    private static List<Runnable> eventQueue = new ArrayList<>();
    private static Lock lock = new ReentrantLock();
    private static Map<GraphicEvent, List<EventCallbackParam>> queue = new XLinkedMap<>();
    private static List<GraphicEvent> waiting = new LinkedList<>();

    public static void bind(GraphicEvent type, final EventCallback event) {
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

    public static void triggerQueued(GraphicEvent e) {

        main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG, e +
         " trigger queued ");

        List<EventCallbackParam> list = queue.get(e);
        if (list == null) {
            return;
        }
        EventCallbackParam p = list.remove(0);
        if (list.isEmpty())
            waiting.remove(e);

        main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG, e +
         " trigger queued with " + p);
        trigger(e, p);
    }

    public static void queue(GraphicEvent e) {
        waiting.add(e);
        main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG, e + " waiting for anim: " + waiting);
    }

    public static void trigger(final GraphicEvent type, final EventCallbackParam obj) {
//        main.system.auxiliary.LogMaster.log(1,
//         type + " triggering with: " + obj == null ? "" : obj.toString());
        if (waiting.contains(type)) {
            main.system.auxiliary.LogMaster.log(LogMaster.ANIM_DEBUG, type + " added to queue: " + queue);
            new MapMaster<>().addToListMap(queue, type, obj);
            return;
        }
        if (eventMap.containsKey(type)) {
            lock.lock();
            try {
                eventQueue.add(() -> eventMap.get(type).call(obj));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
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
