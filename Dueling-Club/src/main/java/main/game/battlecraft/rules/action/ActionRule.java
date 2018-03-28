package main.game.battlecraft.rules.action;

import main.entity.active.DC_ActiveObj;
import main.entity.obj.ActiveObj;
import main.entity.obj.unit.Unit;

public interface ActionRule {

    void actionComplete(ActiveObj activeObj);

    boolean unitBecomesActive(Unit unit);

    default boolean isAppliedOnExploreAction(DC_ActiveObj action) {
        return false;
    }
}
