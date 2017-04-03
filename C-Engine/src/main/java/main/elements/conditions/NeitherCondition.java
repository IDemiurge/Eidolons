package main.elements.conditions;

import main.entity.Ref;

public class NeitherCondition extends OrConditions {

    @Override
    public boolean check(Ref ref) {
        return !super.check(ref);
    }
}
