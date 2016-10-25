package main.elements.conditions.standard;

import main.elements.conditions.MicroCondition;

public class ZLevelCondition extends MicroCondition {
    private boolean defaultReturn;

    public ZLevelCondition(boolean defaultReturn) {
        this.defaultReturn = defaultReturn;
    }

    public boolean check() {
        if (ref.getMatchObj() == null)
            return defaultReturn;
        if (ref.getSourceObj() == null)
            return defaultReturn;
        return ref.getSourceObj().getZ() == ref.getMatchObj().getZ();
    }

}
