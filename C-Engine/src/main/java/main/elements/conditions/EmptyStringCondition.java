package main.elements.conditions;

import main.entity.Ref;
import main.system.auxiliary.StringMaster;
import main.system.math.Property;

public class EmptyStringCondition extends MicroCondition {

    private String string;

    public EmptyStringCondition(String string) {
        this.string = string;
    }

    @Override
    public boolean check(Ref ref) {
        return StringMaster.isEmpty(new Property(string).getStr(ref));
    }

}
