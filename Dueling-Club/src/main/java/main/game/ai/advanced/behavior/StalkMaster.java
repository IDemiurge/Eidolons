package main.game.ai.advanced.behavior;

import main.game.ai.UnitAI;

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
