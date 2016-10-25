package main.elements.conditions.standard;

import main.elements.conditions.MicroCondition;

public class EmptyCondition extends MicroCondition {
    public EmptyCondition() {

    }

    @Override
    public boolean check() {
        return true;
    }

}
