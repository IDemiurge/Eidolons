package main.elements.conditions.standard;

import main.elements.conditions.ConditionImpl;
import main.entity.Ref;

public class ImpossibleCondition extends ConditionImpl {
    @Override
    public boolean check(Ref ref) {
        return false;
    }
}
