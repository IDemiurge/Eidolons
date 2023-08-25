package elements.exec.condition;

import elements.exec.EntityRef;
import framework.entity.Entity;

/**
 * Created by Alexander on 8/25/2023
 */
public class IdentityCondition extends ConditionImpl {
    @Override
    protected boolean checkThis(EntityRef ref) {
        return false;
    }

    @Override
    public String[] getArgs() {
        return new String[0];
    }


}
