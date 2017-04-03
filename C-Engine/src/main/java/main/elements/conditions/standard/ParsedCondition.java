package main.elements.conditions.standard;

import main.elements.conditions.Condition;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.system.entity.ConditionMaster;

public class ParsedCondition extends MicroCondition {

    private String string;
    private Condition conditions;

    public ParsedCondition(String string) {
        this.string = string;
    }

    @Override
    public boolean check(Ref ref) {
        if (conditions == null) {
            conditions = ConditionMaster.toConditions(string);
        }

        return conditions.preCheck(ref);
    }

}
