package elements.exec.condition;

import elements.exec.EntityRef;
import framework.data.TypeData;
import framework.entity.Entity;

/**
 * Created by Alexander on 8/22/2023
 */
@FunctionalInterface
public interface Condition  {
    boolean check(EntityRef ref);

    default String[] getArgs() {
        return new String[0];
    }
    default TypeData getData() {
        return null;
    }

    default Condition setContext(ConditionContext context) {
        return this;
    }

}
