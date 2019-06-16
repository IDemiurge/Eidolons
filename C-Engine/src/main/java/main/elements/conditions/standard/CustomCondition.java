package main.elements.conditions.standard;

import main.elements.conditions.MicroCondition;
import main.entity.Ref;

public abstract class CustomCondition extends MicroCondition {
    @Override
    public abstract boolean check(Ref ref);
}
