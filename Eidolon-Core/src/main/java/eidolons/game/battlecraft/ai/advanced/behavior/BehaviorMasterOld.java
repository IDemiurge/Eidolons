package eidolons.game.battlecraft.ai.advanced.behavior;

import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.UnitAI.AI_BEHAVIOR_MODE;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.explore.behavior.AiBehavior;
import eidolons.game.battlecraft.ai.explore.behavior.WanderAiMaster;

import java.util.HashMap;
import java.util.Map;

public class BehaviorMasterOld extends AiHandler {
    /*
     * 	No priority-ordering for Behaviors I suppose...
	 * Though I may want to pick optimal path for Stalking, e.g., based on some special factors like
	 * proximity, path's illumination, ...
	 * and for Search also, there will definitely be options to choose from, but for now, I can 
	 * make it like a SM-Wandering more or less... 
	 */

    Map<AI_BEHAVIOR_MODE, AiBehavior> behaviorMap = new HashMap<>();

    public BehaviorMasterOld(AiMaster master) {
        super(master);
    }

    public AiBehavior getBehavior(UnitAI ai) {
        AI_BEHAVIOR_MODE type = AI_BEHAVIOR_MODE.WANDER;

        if (getAnalyzer().getClosestEnemyDistance(ai.getUnit()) > 5) {
            type = AI_BEHAVIOR_MODE.GUARD;
        }
        return getBehavior(ai, type);

    }

    private AiBehavior getBehavior(UnitAI ai, AI_BEHAVIOR_MODE type) {
        AiBehavior behavior = behaviorMap.get(type);
        if (behavior != null) {
            return behavior;
        }
        switch (type) {

            case WANDER:
                behavior = new WanderAiMaster(getMaster(), ai);
                break;
            case AMBUSH:
            case GUARD:
            case PATROL:
            case STALK:
            case AGGRO:
                break;
        }
        behaviorMap.put(type, behavior);
        // for (AI_BEHAVIOR_MODE b : ai.getBehaviors()) {
        // // priority? situation? preCheck each...
        // }
        return behavior; // preCheck unit is viable?
    }


    public enum TACTIC {
        DELAY, STALK, AMBUSH, ENGAGE
    }
}
