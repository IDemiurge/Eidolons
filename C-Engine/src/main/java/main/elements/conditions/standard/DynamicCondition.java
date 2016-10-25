package main.elements.conditions.standard;

import main.elements.conditions.Condition;
import main.elements.conditions.MicroCondition;

public class DynamicCondition extends MicroCondition {

    private String s;

    public DynamicCondition(String s) {
        this.s = s;
    }

    @Override
    public boolean check() {
        Condition c = ref.getGame().getConditionMaster().getDynamicCondition(s);
        if (c != null)
            return c.check();
        return false;
    }

}
