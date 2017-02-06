package main.ability.conditions;

import main.elements.conditions.Condition;
import main.elements.conditions.MicroCondition;
import main.system.DC_ConditionMaster.STD_DC_CONDITIONS;

public class DC_StdCondition extends MicroCondition {
    Condition condition;
    private STD_DC_CONDITIONS c;

    public DC_StdCondition(STD_DC_CONDITIONS c) {
        this.c = c;
    }

    @Override
    public boolean check() {

        if (condition == null) {
            condition = c.getCondition();
        }
        return condition.check(ref);
    }

}
