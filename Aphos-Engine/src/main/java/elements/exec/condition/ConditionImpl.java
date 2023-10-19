package elements.exec.condition;

import content.LinkedStringMap;
import elements.exec.EntityRef;
import framework.data.TypeData;

/**
 * Created by Alexander on 8/22/2023
 */
public abstract class ConditionImpl implements Condition {
    ConditionContext context;
    TypeData data;

    protected abstract boolean checkThis(EntityRef ref);
    // public abstract String[] getArgs( );

    @Override
    public boolean check(EntityRef ref) {
        if (context != null)
        {
            ref = ref.copy();
            context.init(ref);
        }
        return checkThis(ref);
    }

    @Override
    public TypeData getData() {
        if (data == null){
            data = new TypeData();
        }
        return data;
    }

    public ConditionImpl setData(TypeData data) {
        this.data = data;
        return this;
    }


    public ConditionImpl setContext(ConditionContext context) {
        this.context = context;
        return this;
    }
    // abstract boolean checkEntity(T entity);
}
