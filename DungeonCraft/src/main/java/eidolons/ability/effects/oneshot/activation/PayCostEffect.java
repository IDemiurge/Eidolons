package eidolons.ability.effects.oneshot.activation;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.active.DC_UnitAction;
import eidolons.entity.unit.Unit;
import main.ability.effects.OneshotEffect;

public class PayCostEffect extends DC_Effect implements OneshotEffect {
    boolean force;
    private String actionName;

    public PayCostEffect(String actionName) {
        this.actionName = actionName;
    }

    @Override
    public boolean applyThis() {
        Unit hero = (Unit) ref.getTargetObj();

        DC_UnitAction action = hero.getAction(actionName);
        if (action == null) {
            return false;
        }
        if (!force) {
            // preCheck
        }

        action.payCosts();
        return true;
    }

}
