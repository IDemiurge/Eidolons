package main.elements.conditions;

import main.system.auxiliary.StringMaster;
import main.system.math.Property;

public class EmptyStringCondition extends MicroCondition {

    private String string;

    public EmptyStringCondition(String string) {
        this.string = string;
    }

    @Override
    public boolean check() {
        return StringMaster.isEmpty(new Property(string).getStr(ref));
    }

}
