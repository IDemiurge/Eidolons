package main.game.logic.event;

import java.util.Arrays;

public class MultiEventType implements Event.EVENT_TYPE {

    public MultiEventType(Event.EVENT_TYPE... types) {
    }

    @Override
    public boolean equals(Event.EVENT_TYPE e) {
        if (e==null) {
            return false;
        }
        return false;
    }

    @Override
    public String name() {
        return "Multi event: " + Arrays.asList();
    }

    @Override
    public String getArg() {
        return null;
    }
}
