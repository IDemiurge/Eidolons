package main.elements.conditions;

import main.ability.effects.Effect;
import main.entity.Ref;

public class EffectCondition extends ConditionImpl {

    private Effect e;

    public EffectCondition(Effect e) {
        this.e = e;
    }

    @Override
    public boolean check(Ref ref) {
        return ref.getEffect().getClass() == e.getClass();
    }

}
