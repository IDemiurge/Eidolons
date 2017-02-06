package main.game.event;

import main.entity.Ref;
import main.game.event.Event.EVENT_TYPE;

public interface Rule {

    EVENT_TYPE getEventType();

    boolean check(Event e);

    void apply(Ref ref);

    boolean isOn();


}
