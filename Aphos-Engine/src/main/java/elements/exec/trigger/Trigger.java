package elements.exec.trigger;

import elements.exec.EntityRef;
import elements.exec.condition.Condition;

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
        getTargetRef().setEventRef(ref);
        return getCondition().check(getTargetRef());
    }
}
