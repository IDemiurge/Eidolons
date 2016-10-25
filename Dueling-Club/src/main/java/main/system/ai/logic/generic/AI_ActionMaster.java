package main.system.ai.logic.generic;

import main.system.ai.UnitAI.AI_BEHAVIOR_MODE;

import java.util.List;

public abstract class AI_ActionMaster {

    List<Constraint> constraints;

    /*
     * isPrioritizing()
     *
     *
     */
    public abstract void initConstraints();

    public abstract void interrupt();

    public abstract void resume();

    public abstract AI_BEHAVIOR_MODE getBehavior();

    public abstract boolean checkBehaviorPossible();

    public abstract boolean checkIdle();
}
