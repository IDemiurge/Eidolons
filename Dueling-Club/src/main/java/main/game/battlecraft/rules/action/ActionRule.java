package main.game.battlecraft.rules.action;

import main.entity.active.DC_ActiveObj;
import main.entity.obj.ActiveObj;
import main.entity.obj.unit.Unit;

public interface ActionRule {

    public void actionComplete(ActiveObj activeObj);

    public boolean unitBecomesActive(Unit unit);

    default boolean isAppliedOnExploreAction(DC_ActiveObj action){
        return false;
    }
}
