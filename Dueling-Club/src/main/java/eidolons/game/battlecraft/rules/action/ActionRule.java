package eidolons.game.battlecraft.rules.action;

import eidolons.entity.active.DC_ActiveObj;
import main.entity.obj.ActiveObj;
import eidolons.entity.obj.unit.Unit;

public interface ActionRule {

    void actionComplete(ActiveObj activeObj);

    boolean unitBecomesActive(Unit unit);

    default boolean isAppliedOnExploreAction(DC_ActiveObj action) {
        return false;
    }
}
