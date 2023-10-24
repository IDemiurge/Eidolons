package elements.exec.effect.framework;

import elements.exec.EntityRef;
import elements.exec.condition.Condition;
import elements.exec.effect.Effect;

/**
 * Created by Alexander on 8/25/2023
 */
public class TargetedEffect {
    private final EntityRef ref;
    private final Effect effect;
    private Condition retainCondition;

    public TargetedEffect(EntityRef ref, Effect effect) {
        this.ref = ref;
        this.effect = effect;
    }

    public TargetedEffect setRetainCondition(Condition retainCondition) {
        this.retainCondition = retainCondition;
        if (retainCondition.getContext() == null){
            retainCondition.setContext(() -> "target");
        }
        return this;
    }

    public boolean checkRemove() {
        if (retainCondition!=null){
            return !retainCondition.check(ref);
        }
        return false;
    }

    public void apply() {
        effect.apply(ref);
    }
}
