package elements.exec.condition;

import elements.exec.EntityRef;
import elements.stats.generic.Stat;

/**
 * Created by Alexander on 10/24/2023
 */
public class PropCondition extends ConditionImpl{

    private Stat prop;

    public PropCondition(Stat prop) {
        this.prop = prop;
    }

    @Override
    protected boolean checkThis(EntityRef ref) {
        return ref.getMatch().isTrue(prop);
    }
}
