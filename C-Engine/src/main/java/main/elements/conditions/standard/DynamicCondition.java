package main.elements.conditions.standard;

import main.elements.conditions.Condition;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;

public class DynamicCondition extends MicroCondition {

    private String s;

    public DynamicCondition(String s) {
        this.s = s;
    }

    @Override
    public boolean check(Ref ref) {
        Condition c = ref.getGame().getConditionMaster().getDynamicCondition(s);
        if (c != null) {
            return c.check(ref);
        }
        return false;
    }

}
