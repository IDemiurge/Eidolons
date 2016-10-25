package main.elements.conditions;

import main.ability.effects.Effect;

public class EffectCondition extends ConditionImpl {

    private Effect e;

    public EffectCondition(Effect e) {
        this.e = e;
    }

    @Override
    public boolean check() {
        return ref.getEffect().getClass() == e.getClass();
    }

}
