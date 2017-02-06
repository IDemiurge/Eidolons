package main.elements.conditions.standard;

import main.elements.conditions.MicroCondition;

public class NonTriggeredEventCondition extends MicroCondition {

    @Override
    public boolean check() {
        if (ref.getEvent() == null) {
            return true;
        }
        return !ref.getEvent().isTriggered();

    }

}
