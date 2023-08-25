package elements.exec.condition;

import elements.exec.EntityRef;
import framework.entity.Entity;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Alexander on 8/25/2023
 * <p>
 * THIS IS NOT FOR TARGETING CONDITIONS!
 */

public class ConditionContext {
    private Function<EntityRef, Entity> keyFunc;

    public ConditionContext(String key) {
        this.keyFunc = ref -> ref.get(key);
    }

    public ConditionContext(Function<EntityRef, Entity> keyFunc) {
        this.keyFunc = keyFunc;
    }

    public void init(EntityRef ref) {
        ref.setMatch(keyFunc.apply(ref));
    }

}
