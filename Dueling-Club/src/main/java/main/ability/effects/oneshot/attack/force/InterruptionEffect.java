package main.ability.effects.oneshot.attack.force;

import main.ability.effects.DC_Effect;
import main.entity.obj.unit.Unit;
import main.rules.mechanics.InterruptRule;
import main.system.math.Formula;

/**
 * Created by JustMe on 3/24/2017.
 */
public class InterruptionEffect extends DC_Effect {


    public InterruptionEffect(String force) {
        this.formula =new Formula(force);
    }

    public InterruptionEffect() {
    }

    @Override
    public boolean applyThis() {
        InterruptRule.interrupt((Unit) ref.getTargetObj());
        return true;
    }
}
