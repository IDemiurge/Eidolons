package main.ability.effects.common;

import main.ability.effects.DC_Effect;
import main.entity.active.DC_UnitAction;
import main.entity.obj.unit.Unit;

public class PayCostEffect extends DC_Effect {
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
            // check
        }

        action.payCosts();
        return true;
    }

}
