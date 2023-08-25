package elements.exec.trigger;

import elements.exec.condition.Condition;

public interface Trigger<T> {
    void apply(T arg);
    Condition getCondition();
}
