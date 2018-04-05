package eidolons.game.battlecraft.rules.action;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import main.entity.obj.ActiveObj;

public interface ActionRule {

    void actionComplete(ActiveObj activeObj);

    boolean unitBecomesActive(Unit unit);

    default boolean isAppliedOnExploreAction(DC_ActiveObj action) {
        return false;
    }
}
