package eidolons.ability.effects.oneshot.rule;

import eidolons.ability.effects.DC_Effect;
import main.ability.effects.OneshotEffect;
import main.system.math.Formula;

public class TossUnitEffect extends DC_Effect implements OneshotEffect {

    private Boolean fallDown;

    public TossUnitEffect(String forceFormula, Boolean fallDown) {
        this.fallDown = fallDown;
        this.formula = new Formula(forceFormula);
    }

    @Override
    public boolean applyThis() {
        // distance? landing cell - preCheck collisions

        return true;
    }

}
