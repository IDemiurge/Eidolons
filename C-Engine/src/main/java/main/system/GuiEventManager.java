package main.system;

import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.EnumMaster;
import main.system.launch.CoreEngine;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * Date: 04.11.2016
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class GuiEventManager {
    private static boolean vertx;

    public static boolean isVertx() {
        return vertx;
    }

    public static void setVertx(boolean vertx) {
        GuiEventManager.vertx = vertx;
    }

    public static void bind(EventType type, final EventCallback event) {
        bind(false, type, event);
    }

    public static void bind(boolean removePreviousBind, EventType type, final EventCallback event) {
        if (CoreEngine.isGraphicsOff())
            return;
        if (removePreviousBind)
            GuiEventManagerImpl.removeBind(type);
        GuiEventManagerImpl.bind(type, event);
    }

    public static void cleanUp() {
        GuiEventManagerImpl.cleanUp();

    }

    public static void bindSound(EventType type, final EventCallback event) {

    }

    private static void checkSoundEvent(EventType type, Object obj) {

    }

    public static void trigger(final EventType type) {
        trigger(type, null);
    }

    public static void trigger(final String type, Object... params) {
        trigger(GuiEventManager.getEvent(type) ,params );
    }
    public static void trigger(final EventType type, Object... params) {
        if (CoreEngine.isGraphicsOff())
            return;
        Object obj = null;
        if (params != null) {
            if (params.length == 1) {
                obj = params[0];
            } else {
                obj = new EventCallbackParam(Arrays.asList(params));
            }
        }
//        checkSoundEvent(type, obj);
        GuiEventManagerImpl.trigger(type, obj);
    }

    public static void processEvents() {
        GuiEventManagerImpl.processEvents();
    }

    public static boolean checkEventIsGuiHandled(Event event) {
        if (event.getType() instanceof STANDARD_EVENT_TYPE) {
            switch ((STANDARD_EVENT_TYPE) event.getType()) {
                case UNIT_HAS_BEEN_DEALT_PURE_DAMAGE:
                case EFFECT_HAS_BEEN_APPLIED:
                case UNIT_HAS_CHANGED_FACING:
                case UNIT_HAS_TURNED_CLOCKWISE:
                case UNIT_HAS_TURNED_ANTICLOCKWISE:
                case UNIT_HAS_FALLEN_UNCONSCIOUS:
                case UNIT_HAS_RECOVERED_FROM_UNCONSCIOUSNESS:
                case UNIT_HAS_BEEN_KILLED:
                case UNIT_BEING_MOVED:
                case UNIT_FINISHED_MOVING:
                    return true;
            }

        } else {
            if (event.getType().name().startsWith("PARAM_MODIFIED")) {
                return isParamEventAlwaysFired(event.getType().getArg());

            }
        }

        return false;
    }
    public static EventType getEvent(String s ) {
        return new EnumMaster<GuiEventType>().retrieveEnumConst(GuiEventType.class, s);
    }

    public static boolean isParamEventAlwaysFired(String param) {
        switch (param) {
            case "Endurance":
            case "C Endurance":
            case "Toughness":
            case "C Toughness":
                //            case "Illumination":
                return true;
        }
        return false;
    }

}

