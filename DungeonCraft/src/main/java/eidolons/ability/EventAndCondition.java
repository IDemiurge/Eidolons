package eidolons.ability;

import main.elements.conditions.Condition;
import main.system.EventType;

public class EventAndCondition {
    EventType event;
    Condition condition;

    public EventAndCondition(EventType event, Condition condition) {
        this.event = event;
        this.condition = condition;
    }

    public EventType getEvent() {
        return event;
    }

    public Condition getCondition() {
        return condition;
    }
}
