package main.elements.conditions;

public class NeitherCondition extends OrConditions {

    @Override
    public boolean check() {
        return !super.check();
    }
}
