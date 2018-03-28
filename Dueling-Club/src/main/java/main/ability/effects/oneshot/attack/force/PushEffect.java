package main.ability.effects.oneshot.attack.force;

import main.ability.effects.DC_Effect;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.rules.combat.misc.KnockdownRule;
import main.system.math.Formula;

/**
 * Created by JustMe on 3/24/2017.
 */
public class PushEffect extends DC_Effect {


    public PushEffect(String force) {
        this.formula = new Formula(force);
    }

    public PushEffect() {
    }

    @Override
    public boolean applyThis() {
        KnockdownRule.knockdown((Unit) ref.getTargetObj());
        return true;
    }
}
