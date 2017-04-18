package main.elements.conditions;

import main.entity.Ref;

public class NotCondition extends ConditionImpl {
    Condition c;
    private Boolean switcher;

    public NotCondition(Condition c) {
        this.c = c;
    }

    public NotCondition(Boolean b, Condition c) {
        this(c);
        this.switcher = b;
    }

    @Override
    public boolean preCheck(Ref ref) {
        if (switcher != null) {
            setTrue((switcher) == !c.preCheck(ref));
        } else
            setTrue(!c.preCheck(ref));
        return isTrue();
    }


    @Override
    public String toString() {
        return "NOT " + c.toString();
    }

    @Override
    public boolean check(Ref ref) {
        return false;
    }

}
