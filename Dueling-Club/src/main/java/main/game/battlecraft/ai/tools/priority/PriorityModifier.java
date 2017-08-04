package main.game.battlecraft.ai.tools.priority;

import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.elements.generic.AiMaster;

/**
 * Created by JustMe on 8/2/2017.
 */
public class PriorityModifier extends AiHandler{

    private static final String GOAL_PREFIX = "GOAL_";

    public PriorityModifier(AiMaster master) {
        super(master);
    }

    public int getBasePriority(ActionSequence sequence) {
        return 0;
    }

    public int getPriorityModifier(ActionSequence sequence) {
//getSituationAnalyzer().getCastingPriority()
       int goalMod=getModifierForGoal(sequence.getTask().getType());
        return 0;
    }

    private int getModifierForGoal(GOAL_TYPE type) {
        return getConstInt(getPriorityConstantMaster().getConst(
         GOAL_PREFIX+type.toString()));
    }
}
