package eidolons.ability.conditions.req;

import eidolons.entity.active.ActiveObj;
import eidolons.entity.unit.Unit;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;

public class CostCondition extends MicroCondition {
    boolean spell;
    private String actionName;

    public CostCondition(String actionName, Boolean spell) {
        this.actionName = actionName;
        this.spell = spell;

    }

    public CostCondition(String actionName) {
        this(actionName, false);
    }

    @Override
    public boolean check(Ref ref) {
        Unit hero = (Unit) ref.getTargetObj();

        ActiveObj action;

        if (spell) {
            action = hero.getSpell(actionName);
        } else {
            action = hero.getAction(actionName);
        }
        if (action == null) {
            return false;
        }

        boolean canBeActivated = action.canBeActivated(ref, true);
        if (!canBeActivated) {
            return false;
        }
        return canBeActivated;
    }

}
