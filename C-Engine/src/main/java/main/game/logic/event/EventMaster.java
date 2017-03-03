package main.game.logic.event;

import main.entity.Ref;
import main.game.logic.event.Event.EVENT_TYPE;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

/**
 * Created by JustMe on 3/1/2017.
 */
public class EventMaster {
    public static boolean fireStandard(STANDARD_EVENT_TYPE type, Ref ref) {
        return fire (type, ref) ;
    }
    public static boolean fire (EVENT_TYPE type, Ref ref) {
        return ref.getGame().fireEvent(new Event(type, ref));
    }
}
