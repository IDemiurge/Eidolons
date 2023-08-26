package elements.exec.condition;

import elements.exec.EntityRef;
import framework.entity.Entity;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Alexander on 8/26/2023
 */
public class AdvancedContext extends ConditionContext {
    private Consumer<EntityRef> advancedFunc;

    public AdvancedContext(Consumer<EntityRef> advancedFunc) {
        super("");
        this.advancedFunc = advancedFunc;
    }

    public void init(EntityRef ref) {
        advancedFunc.accept(ref);
    }
}
