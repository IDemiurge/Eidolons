package main.game.logic.event;

import java.lang.reflect.Array;
import java.util.Arrays;

public class MultiEventType implements Event.EVENT_TYPE {
    private final Event.EVENT_TYPE[] t;

    public MultiEventType(Event.EVENT_TYPE... types) {
        this.t = types;
    }

    @Override
    public boolean equals(Event.EVENT_TYPE e) {
        if (e==null) {
            return false;
        }
        return Arrays.asList().contains(e);
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
