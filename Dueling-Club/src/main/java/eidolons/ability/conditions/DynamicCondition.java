package eidolons.ability.conditions;

import main.data.ability.OmittedConstructor;
import main.elements.conditions.ConditionImpl;
import main.entity.Ref;

import java.util.function.Predicate;

public class DynamicCondition<T> extends ConditionImpl {
    private Predicate<T> predicate;
    private T arg;

    @OmittedConstructor
    public DynamicCondition(Predicate<T> predicate, T arg) {
        this.predicate = predicate;
        this.arg = arg;
    }
    @OmittedConstructor
    public DynamicCondition() {
    }

    @Override
    public boolean check(Ref ref) {
        if (predicate == null) {
            return predicate.test(arg);
        }
        return false;
    }

}
