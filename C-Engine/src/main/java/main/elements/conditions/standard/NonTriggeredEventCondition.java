package main.elements.conditions.standard;

import main.elements.conditions.MicroCondition;
import main.entity.Ref;

public class NonTriggeredEventCondition extends MicroCondition {

    @Override
    public boolean check(Ref ref) {
        if (ref.getEvent() == null) {
            return true;
        }
        return !ref.getEvent().isTriggered();

    }

}
