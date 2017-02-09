package main.elements.conditions.standard;

import main.elements.conditions.Condition;
import main.elements.conditions.MicroCondition;
import main.system.ConditionMaster;

public class ParsedCondition extends MicroCondition {

    private String string;
    private Condition conditions;

    public ParsedCondition(String string) {
        this.string = string;
    }

    @Override
    public boolean check() {
        if (conditions == null) {
            conditions = ConditionMaster.toConditions(string);
        }

        return conditions.check(ref);
    }

}
