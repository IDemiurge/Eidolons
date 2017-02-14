package main.system.auxiliary.secondary;

import main.elements.conditions.Condition;
import main.elements.conditions.Conditions;
import main.entity.Ref;
import main.system.auxiliary.log.LogMaster;

public class VerboseChecker {

    public boolean check(Condition c, Ref ref) {
        LogMaster.log(LogMaster.VERBOSE_CHECK, c.toString()
                + " being checked with ref: " + ref);
        if (c instanceof Conditions) {
            boolean result = true;
            for (Condition condition : ((Conditions) c)) {
                result &= log(c, c.check(ref));
            }
            return result;
        }
        return log(c, c.check(ref));
    }

    private boolean log(Condition c, boolean check) {
        LogMaster.log(LogMaster.VERBOSE_CHECK, c.toString() + " checked "
                + check);
        return check;
    }
}
