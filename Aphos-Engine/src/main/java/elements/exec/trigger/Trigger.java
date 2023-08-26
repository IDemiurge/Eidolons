package elements.exec.trigger;

import elements.exec.EntityRef;
import elements.exec.condition.Condition;

public interface Trigger<T> {
    void apply(T arg);
    Condition getCondition();
    default Condition getRetainCondition(){
        return null;
    }

    EntityRef getTargetRef();
}
