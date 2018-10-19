package eidolons.game.battlecraft.ai.explore;

import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.advanced.behavior.AI_BehaviorMaster;

public class StalkMaster extends AI_BehaviorMaster {


    @Override
    public void initConstraints() {

    }

    @Override
    public void interrupt() {

    }

    @Override
    public void resume() {

    }

    @Override
    public UnitAI.AI_BEHAVIOR_MODE getBehavior() {
        return null;
    }

    @Override
    public boolean checkBehaviorPossible() {
        return false;
    }

    @Override
    public boolean checkIdle() {
        return false;
    }
}
