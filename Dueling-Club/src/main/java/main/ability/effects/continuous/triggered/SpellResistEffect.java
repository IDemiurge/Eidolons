package main.ability.effects.continuous.triggered;

import main.elements.conditions.standard.ChanceCondition;
import main.system.math.Formula;

public class SpellResistEffect extends BlockEffect {

    public SpellResistEffect(Formula formula) {
        super(BLOCK_TYPES.HOSTILE_SPELLS);
        this.formula = formula;
    }

    @Override
    public boolean applyThis() {
        conditions.add(new ChanceCondition(formula));
        return super.applyThis();
    }
}
