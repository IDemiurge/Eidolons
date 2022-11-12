package eidolons.ability.effects.oneshot.attack.force;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.rules.combat.misc.KnockdownRule;
import main.system.math.Formula;

/**
 * Created by JustMe on 3/24/2017.
 */
public class KnockdownEffect extends DC_Effect {


    public KnockdownEffect(String force) {
        this.formula = new Formula(force);
    }

    public KnockdownEffect() {
    }

    @Override
    public boolean applyThis() {
        KnockdownRule.knockdown((Unit) ref.getTargetObj());
        return true;
    }
}
