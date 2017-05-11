package main.system;

import main.data.XLinkedMap;
import main.system.auxiliary.log.LogMaster;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 2/1/2017.
 */
public class AnimEventMaster extends GuiEventManager {
//    private static AnimEventMaster instance;

    private static Map<main.system.GuiEventType, List<EventCallbackParam>> queue = new XLinkedMap<>();
    private static List<main.system.GuiEventType> waiting = new LinkedList<>();

    public static void triggerQueued(main.system.GuiEventType e) {

        LogMaster.log(LogMaster.ANIM_DEBUG, "Triggering queued " + e);

        List<EventCallbackParam> list = queue.get(e);
        if (list == null) {
            return;
        }
        EventCallbackParam p = list.remove(0);
        if (list.isEmpty()) {
            waiting.remove(e);
        }

        LogMaster.log(LogMaster.ANIM_DEBUG, e + " trigger queued with " + p);
//        trigger(e, portrait);
    }

    public static void queue(main.system.GuiEventType e) {
        waiting.add(e);
        LogMaster.log(LogMaster.ANIM_DEBUG, e + " waiting for anim: " + waiting);
    }

    private static void trigger(GuiEventType e, EventCallbackParam p) {

// if (waiting.contains(type)) {
//        LogMaster.log(LogMaster.ANIM_DEBUG, type + " added to queue: " + queue);
//        MapMaster.addToListMap(queue, type, obj);
//        return;
//    }
    }

    public enum GuiEventType {
        UPDATE_PHASE_ANIM,
        UPDATE_PHASE_ANIMS,
    }
}
