package eidolons.ability.effects.oneshot.attack.force;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.rules.combat.misc.KnockdownRule;
import eidolons.ability.effects.DC_Effect;
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
