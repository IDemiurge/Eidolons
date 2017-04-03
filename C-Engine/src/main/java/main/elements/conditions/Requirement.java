package main.elements.conditions;

import main.entity.Ref;

public class Requirement extends ConditionImpl {

    private Condition condition;
    private String text;

    public Requirement(Condition c, String t) {
        this.text = t;
        this.condition = c;
    }

    @Override
    public boolean check(Ref ref) {
        return getCondition().check(ref);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

}
