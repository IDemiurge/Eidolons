package eidolons.ability.effects.oneshot.attack.force;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.mechanics.InterruptRule;
import main.system.math.Formula;

/**
 * Created by JustMe on 3/24/2017.
 */
public class InterruptionEffect extends DC_Effect {


    public InterruptionEffect(String force) {
        this.formula = new Formula(force);
    }

    public InterruptionEffect() {
    }

    @Override
    public boolean applyThis() {
        InterruptRule.interrupt((Unit) ref.getTargetObj());
        return true;
    }
}
