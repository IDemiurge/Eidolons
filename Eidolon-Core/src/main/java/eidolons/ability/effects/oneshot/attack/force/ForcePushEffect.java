package eidolons.ability.effects.oneshot.attack.force;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.combat.misc.KnockdownRule;
import main.system.math.Formula;

/**
 * Created by JustMe on 3/24/2017.
 */
public class ForcePushEffect extends DC_Effect {


    public ForcePushEffect(String force) {
        this.formula = new Formula(force);
    }

    public ForcePushEffect() {
    }

    @Override
    public boolean applyThis() {
        KnockdownRule.knockdown((Unit) ref.getTargetObj());
        return true;
    }
}
