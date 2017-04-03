package main.ability.effects.container;

import main.ability.effects.Effect;
import main.ability.effects.EffectImpl;
import main.elements.conditions.Condition;

public class ConditionalEffect extends EffectImpl {
    Condition condition;
    Effect effect;

    public ConditionalEffect(Condition condition, Effect effect) {
        this.condition = condition;
        this.effect = effect;
    }

    @Override
    public boolean applyThis() {
        if (condition.preCheck(ref)) {
            return effect.apply(ref);
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return "If " + condition + ": " + effect.toString();
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }
}
