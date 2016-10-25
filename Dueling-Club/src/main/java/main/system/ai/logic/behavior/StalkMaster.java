package main.system.ai.logic.behavior;

import main.system.ai.UnitAI;
import main.system.ai.logic.generic.AI_ActionMaster;

public class StalkMaster extends AI_ActionMaster {


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
