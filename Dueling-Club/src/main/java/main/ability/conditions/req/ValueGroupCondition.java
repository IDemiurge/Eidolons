package main.ability.conditions.req;

import main.content.DC_ValueManager.VALUE_GROUP;
import main.content.values.parameters.PARAMETER;
import main.elements.conditions.NumericCondition;
import main.entity.Ref;
import main.system.entity.ConditionMaster;

public class ValueGroupCondition extends NumericCondition {

    boolean total;
    private VALUE_GROUP template;

    public ValueGroupCondition(VALUE_GROUP template, String amount, boolean total) {
        super("", amount);
        this.total = total;
        this.template = template;
    }

    @Override
    public String toString() {
        return total ? "Total " : "Any " + template.toString() + " greater than "
                + getComparingValue();
    }

    @Override
    public boolean check(Ref ref) {
        Integer sum = 0;
        for (PARAMETER p : template.getParams()) {
            if (total) {
                sum += ref.getSourceObj().getIntParam(p);
            } else if (check(p)) {
                return true;
            }
        }
        if (total) {
            return sum <= (getComparingValue().getInt());
        }
        return false;
    }

    private boolean check(PARAMETER p) {
        return ConditionMaster.getParamCondition(p.getName(), getComparingValue().toString())
                .preCheck(ref);
    }

}
