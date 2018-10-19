package eidolons.game.battlecraft.ai.advanced.behavior;

import eidolons.game.battlecraft.ai.UnitAI.AI_BEHAVIOR_MODE;
import eidolons.game.battlecraft.ai.explore.Constraint;

import java.util.List;

public abstract class AI_BehaviorMaster {

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
