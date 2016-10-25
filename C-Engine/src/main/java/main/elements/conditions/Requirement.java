package main.elements.conditions;

public class Requirement extends ConditionImpl {

    private Condition condition;
    private String text;

    public Requirement(Condition c, String t) {
        this.text = t;
        this.condition = c;
    }

    @Override
    public boolean check() {
        return getCondition().check();
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
