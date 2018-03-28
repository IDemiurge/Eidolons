package main.game.battlecraft.ai.tools.priority;

import main.content.PARAMS;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.elements.costs.Costs;
import main.game.battlecraft.ai.advanced.machine.AiConst;
import main.game.battlecraft.ai.elements.actions.ActionManager;
import main.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.elements.generic.AiMaster;

/**
 * Created by JustMe on 8/2/2017.
 */
public class PriorityModifier extends AiHandler {

    private static final String GOAL_PREFIX = "GOAL_";

    public PriorityModifier(AiMaster master) {
        super(master);
    }

    public int getBasePriority(ActionSequence sequence) {
        return 0;
    }

    public int getPriorityModifier(ActionSequence sequence) {
//getSituationAnalyzer().getCastingPriority()
        int mod = 100;
        int goalMod = getModifierForGoal(sequence.getTask().getType());
        mod += goalMod;
//        SITUATION situation = unit.getOwner().getAI().getSituation();
//        factor = ParamPriorityAnalyzer.getSituationFactor(goal, situation);
//        mod  += ParamPriorityAnalyzer.getAI_TypeFactor(goal, getUnit().getAI().getType());

        // if (behaviorMode != BEHAVIOR_MODE.PANIC)
        mod -= getCostPenalty(sequence);
        mod -= mod * (getSequenceLengthPenalty(sequence)) / 100;
        return mod;
    }

    private int getModifierForGoal(GOAL_TYPE type) {
        try {
            return getConstInt(getPriorityConstantMaster().getConst(GOAL_PREFIX + type.toString()));
        } catch (Exception e) {
//            main.system.ExceptionMaster.printStackTrace(e);
        }
        return 0;
    }

    public int getCostPenalty(ActionSequence as) {
        Costs cost = ActionManager.getTotalCost(as.getActions());
        int cost_penalty = 100 - getParamAnalyzer().getCostPriorityFactor(cost, getUnit());
        String string = "cost";
        try {
            if (as.getLastAction().getActive().isChanneling()) {
                cost_penalty += cost_penalty
                 * cost.getCost(PARAMS.C_N_OF_ACTIONS).getPayment().getAmountFormula()
                 .getInt(as.getLastAction().getRef()) / 5;
                string = "channeling cost";
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return (cost_penalty);

    }

    public int getSequenceLengthPenalty(ActionSequence as) {
        int length = as.getActions().size() - 1;
        int penalty = (int) Math.round(length * Math.sqrt(length) * getConstInt(AiConst.SEQUENCE_LENGTH_PENALTY_POW15))
         + length * getConstInt(AiConst.SEQUENCE_LENGTH_PENALTY);
        if (penalty > 95) {
            penalty = 95;
        }
        return (penalty);
    }

}
