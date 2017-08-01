package main.game.battlecraft.ai.advanced.companion;

import main.content.CONTENT_CONSTS2.ORDER_TYPE;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.advanced.companion.MetaGoal.META_GOAL_TYPE;
import main.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.elements.generic.AiMaster;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 7/30/2017.
 */
public class MetaGoalMaster extends AiHandler {

    public MetaGoalMaster(AiMaster master) {
        super(master);
    }

    public List<MetaGoal> initMetaGoalsForUnit(UnitAI ai) {
        List<MetaGoal> goals = new LinkedList<>();
        if (ai.getCurrentOrder() != null) {
            goals.addAll(getGoalsFromOrder(ai.getCurrentOrder()));
        }
        if (ai.getCurrentOrder() != null) {
//            goals = getGoalsFromBehavior(ai.getCurrentBehavior());
        }
        goals.addAll(getGoalsFromCharacter(ai));

        return goals;
    }

    private List<MetaGoal> getGoalsFromCharacter(UnitAI ai) {
        List<MetaGoal> list = new LinkedList<>();
//
        switch (ai.getType()) {
            case TANK:
                list.addAll(createGoals(META_GOAL_TYPE.PROTECT, ai));
        }
        return list;
    }

    private List<MetaGoal> createGoals(META_GOAL_TYPE protect, UnitAI ai) {
        List<MetaGoal> goals = new LinkedList<>();
        switch (protect) {
            case PROTECT:
                getAnalyzer().getAllies(ai).forEach(ally -> goals.add(new MetaGoal(META_GOAL_TYPE.PROTECT, ally.getId())));
                break;
        }
        return goals;
    }

    private List<MetaGoal> getGoalsFromOrder(Order currentOrder) {
        List<MetaGoal> list = new LinkedList<>();
        META_GOAL_TYPE type = getGoalTypeFromOrderType(currentOrder.getType());

        list.add(new MetaGoal(type, currentOrder.getArg()));
        return list;
    }

    private META_GOAL_TYPE getGoalTypeFromOrderType(ORDER_TYPE type) {
        switch (type) {
            case SUPPORT:
            case HEAL:
                return META_GOAL_TYPE.AID;
            case PROTECT:
                return META_GOAL_TYPE.PROTECT;
            case PATROL:
            case PURSUIT:
            case WANDER:
            case MOVE:
//                return META_GOAL_TYPE.AVOID;
            case ATTACK:
            case KILL:
            case SPECIAL:
//                return META_GOAL_TYPE.AVENGE;
            case HOLD:
                break;
        }
        return null;
    }

    public Integer getPriorityMultiplier(ActionSequence sequence) {
        Integer mod = 0;
        for (MetaGoal metaGoal : sequence.getAi().getMetaGoals()) {
            if (metaGoal.getArg().equals(sequence.getTask().getArg())) {
                mod += getModForSameArgTask(sequence.getTask().getType(), metaGoal.getType());
            } else {
                Unit unit = sequence.getTask().getUnitArg();
                mod += getModForOtherArgTask(unit, metaGoal.getArg(), sequence, metaGoal.getType());

            }
        }

        return mod;
    }

    private Integer getModForOtherArgTask(Unit unit, Object metaGoalArg, ActionSequence sequence,
                                          META_GOAL_TYPE type) {
        Unit target = (Unit) unit.getGame().getObjectById((Integer) metaGoalArg);
        switch (type) {
            case PROTECT:
                if (sequence.getTask().getType() == GOAL_TYPE.ATTACK) {
                    //get threat
                    float threat = getThreatAnalyzer().getRelativeThreat(target.getAI(), unit);
                    int mod = getThreatProtectionMod(sequence.getAi(), target);
                    return (int) (threat * mod);
                }

        }


        return null;
    }

    private int getThreatProtectionMod(UnitAI ai, Unit target) {
        return 20 + target.calculatePower();
    }

    private Integer getModForSameArgTask(GOAL_TYPE type, META_GOAL_TYPE metaGoalType) {
        switch (metaGoalType) {
            case PROTECT:
                switch (type) {
                    case CUSTOM_SUPPORT:
                    case RESTORE:
                        return 35;
                }
                break;
            case AVENGE:
                break;
            case AVOID:
                break;
            case AID:
                switch (type) {
                    case CUSTOM_SUPPORT:
                    case RESTORE:
                        return 65;
                }
                break;
        }
        return null;
    }

}
