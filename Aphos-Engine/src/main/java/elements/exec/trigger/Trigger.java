package elements.exec.trigger;

import elements.exec.EntityRef;
import elements.exec.condition.Condition;
import system.log.SysLog;

public interface Trigger<T> {
    void apply(T arg);
    Condition getCondition();
    EntityRef getTargetRef();
    default Condition getRetainCondition(){
        return null;
    }
    default boolean check(EntityRef ref) {
        return getCondition().check(ref);
    }
    default boolean checkVsOriginalRef(EntityRef ref) {
        SysLog.printOut(SysLog.LogChannel.Main, "Applied", "on", ref);
        getTargetRef().setEventRef(ref);
        boolean check = getCondition().check(getTargetRef());
        return check;
    }
}
