package main.elements.conditions.standard;

import main.elements.conditions.MicroCondition;
import main.entity.Ref;

public class EmptyCondition extends MicroCondition {
    public EmptyCondition() {

    }

    @Override
    public boolean check(Ref ref) {
        return true;
    }

}
