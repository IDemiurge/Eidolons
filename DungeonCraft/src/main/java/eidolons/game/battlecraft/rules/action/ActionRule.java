package eidolons.game.battlecraft.rules.action;

import eidolons.entity.active.ActiveObj;
import eidolons.entity.unit.Unit;
import main.entity.obj.IActiveObj;

public interface ActionRule {

    void actionComplete(IActiveObj activeObj);

    default boolean unitBecomesActive(Unit unit){
        return true;
    }

    default boolean isAppliedOnExploreAction(ActiveObj action) {
        return false;
    }
}
