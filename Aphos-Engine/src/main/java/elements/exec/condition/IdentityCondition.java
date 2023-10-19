package elements.exec.condition;

import elements.exec.EntityRef;

/**
 * Created by Alexander on 8/25/2023
 */
public class IdentityCondition extends ConditionImpl {
    private final String key;

    public IdentityCondition(String key) {
        this.key = key;
    }

    @Override
    protected boolean checkThis(EntityRef ref) {
        return ref.getMatch() == ref.get(key);
    }

    @Override
    public String[] getArgs() {
        return new String[0];
    }


}
