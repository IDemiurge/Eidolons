package main.ability.effects.container;

import main.ability.effects.Effect;
import main.elements.conditions.Condition;

public class IfElseEffect extends ConditionalEffect {

    Effect ELSEeffect;

    public IfElseEffect(Effect IFeffect, Condition condition, Effect ELSEeffect) {
        super(condition, IFeffect);
        this.ELSEeffect = ELSEeffect;
    }

    @Override
    public String getTooltip() {
        if (condition.preCheck(ref)) {
            return effect.getTooltip();
        } else {
            return ELSEeffect.getTooltip();
        }
    }

    @Override
    public boolean applyThis() {
        if (condition.preCheck(ref)) {
            return effect.apply(ref);
        } else {
            return ELSEeffect.apply(ref);
        }
    }
}
