package main.elements.conditions.standard;

import main.elements.conditions.MicroCondition;
import main.entity.Ref;

public class ZLevelCondition extends MicroCondition {
    private boolean defaultReturn;

    public ZLevelCondition(boolean defaultReturn) {
        this.defaultReturn = defaultReturn;
    }

    public boolean check(Ref ref) {
        if (ref.getMatchObj() == null) {
            return defaultReturn;
        }
        if (ref.getSourceObj() == null) {
            return defaultReturn;
        }
        return ref.getSourceObj().getZ() == ref.getMatchObj().getZ();
    }

}
