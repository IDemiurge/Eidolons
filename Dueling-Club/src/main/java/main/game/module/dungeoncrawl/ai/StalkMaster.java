package main.game.module.dungeoncrawl.ai;

import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.advanced.behavior.AI_BehaviorMaster;

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
