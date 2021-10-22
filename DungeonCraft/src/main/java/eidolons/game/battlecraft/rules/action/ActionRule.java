package eidolons.game.battlecraft.rules.action;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.unit.Unit;
import main.entity.obj.ActiveObj;

public interface ActionRule {

    void actionComplete(ActiveObj activeObj);

    default boolean unitBecomesActive(Unit unit){
        return true;
    }

    default boolean isAppliedOnExploreAction(DC_ActiveObj action) {
        return false;
    }
}
