package main.ability.effects.oneshot.rpg;

import main.ability.effects.DC_Effect;
import main.system.math.Formula;

public class TossUnitEffect extends DC_Effect {

    private Boolean fallDown;

    public TossUnitEffect(String forceFormula, Boolean fallDown) {
        this.fallDown = fallDown;
        this.formula = new Formula(forceFormula);
    }

    @Override
    public boolean applyThis() {
        // distance? landing cell - check collisions

        return true;
    }

}
