package elements.exec.condition;

import elements.exec.EntityRef;
import framework.data.TypeData;
import framework.entity.field.Unit;

import java.util.function.Supplier;

/**
 * Created by Alexander on 8/22/2023
 */
@FunctionalInterface
public interface Condition  {
    boolean check(EntityRef ref);

    default boolean check(Supplier<Unit> matchFunc) {
        return check(new EntityRef(matchFunc.get()).setMatch(matchFunc.get()));
    }

    default String[] getArgs() {
        return new String[0];
    }
    default TypeData getData() {
        return null;
    }

    default Condition setContext(ConditionContext context) {
        return this;
    }
    default Condition setContext(Supplier<String> keySupplier) {
        return this;
    }

    default ConditionContext getContext() {
        return null;
    }
}
