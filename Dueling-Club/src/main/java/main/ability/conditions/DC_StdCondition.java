package main.ability.conditions;

import main.elements.conditions.Condition;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.system.DC_ConditionMaster.STD_DC_CONDITIONS;
@Deprecated
public class DC_StdCondition extends MicroCondition {
    Condition condition;
    private STD_DC_CONDITIONS c;

    public DC_StdCondition(STD_DC_CONDITIONS c) {
        this.c = c;
    }

    @Override
    public boolean check(Ref ref) {

        if (condition == null) {
            condition = c.getCondition();
        }
        return condition.preCheck(ref);
    }

}
