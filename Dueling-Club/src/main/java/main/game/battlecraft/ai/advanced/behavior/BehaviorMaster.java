package main.game.battlecraft.ai.advanced.behavior;

import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.entity.Ref;
import main.entity.active.DC_ActionManager.STD_ACTIONS;
import main.entity.active.DC_ActiveObj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.GroupAI;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.UnitAI.AI_BEHAVIOR_MODE;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.battlecraft.ai.elements.generic.AiHandler;
import main.game.battlecraft.ai.elements.generic.AiMaster;
import main.game.battlecraft.ai.tools.path.ActionPath;
import main.game.bf.Coordinates;
import main.game.module.dungeoncrawl.ai.AiBehavior;
import main.game.module.dungeoncrawl.ai.GuardAi;
import main.game.module.dungeoncrawl.ai.PatrolMaster;
import main.game.module.dungeoncrawl.ai.WanderAi;
import main.system.auxiliary.data.ListMaster;

import java.util.HashMap;
import java.util.Map;

public class BehaviorMaster extends AiHandler {
    /*
     * 	No priority-ordering for Behaviors I suppose...
	 * Though I may want to pick optimal path for Stalking, e.g., based on some special factors like
	 * proximity, path's illumination, ...
	 * and for Search also, there will definitely be options to choose from, but for now, I can 
	 * make it like a SM-Wandering more or less... 
	 */

    Map<AI_BEHAVIOR_MODE, AiBehavior> behaviorMap = new HashMap<>();
    private boolean recursion;

    public BehaviorMaster(AiMaster master) {
        super(master);
    }

    public static AI_BEHAVIOR_MODE initGroupPref(GroupAI groupAI) {
        if (groupAI.getCreepGroup() != null) {

        }
        // else groupAI.getLeader();

        switch (groupAI.getEngagementLevel()) {
            case AGGRO:
                break;
            case ALARMED:
                break;
            case SUSPECTING:
                break;
            case UNSUSPECTING:
                break;

        }
        return AI_BEHAVIOR_MODE.WANDER;
    }

    private static GOAL_TYPE getGoalFromBehavior(AI_BEHAVIOR_MODE b) {
        switch (b) {
            case AGGRO:
                return (AiEnums.GOAL_TYPE.AGGRO);
            case AMBUSH:
                return (AiEnums.GOAL_TYPE.AMBUSH);
            case GUARD:
                return (AiEnums.GOAL_TYPE.STAND_GUARD);
            case PATROL:
                return (AiEnums.GOAL_TYPE.PATROL);
            case STALK:
                return (AiEnums.GOAL_TYPE.STALK);
            case WANDER:
                return (AiEnums.GOAL_TYPE.WANDER);
        }
        return AiEnums.GOAL_TYPE.MOVE;
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
                behavior = new WanderAi();
                break;
            case AMBUSH:
                break;
            case AGGRO:
                break;
            case STALK:
                break;
            case PATROL:
                break;
            case GUARD:
                behavior = new GuardAi();
                break;
        }
        behaviorMap.put(type, behavior);
        // for (AI_BEHAVIOR_MODE b : ai.getBehaviors()) {
        // // priority? situation? preCheck each...
        // }
        return behavior; // preCheck unit is viable?
    }


    private Action getAction(GOAL_TYPE type, UnitAI ai) {

        String action = null;
        Integer target = null;

        // doesn't the group have standing orders as a whole?..
        Unit unit = ai.getUnit();
        Ref ref = new Ref(unit);
        GroupAI group = ai.getGroup();
        // checkBehaviorChange(group); where does that happen?
        switch (type) {
            case AMBUSH:
                break;
            case STALK:
                break;
            case STAND_GUARD:
            case PATROL:
                PatrolMaster.getPatrolAction(ai);
            case SEARCH: // having already turned on the Mode
            case WANDER:
                if (ai.isLeader()) {
                    Boolean change = WanderAi.checkWanderDirectionChange(group, type);
                    if (change == null) {
                        action = getIdleAction(ai, type);
                        change = true;
                    }
                    // TODO IDEA: change only on LEADER's TURN, and for others,
                    // either proceed or WAIT; >> Updating blocked() status? or
                    // maybe go meet leader if blocked... or something like it
                    if (change) {
                        group.getWanderStepCoordinateStack().push(
                         group.getLeader().getCoordinates());
                        WanderAi.changeGroupMoveDirection(group, type);
                    }
                }
                boolean wait = false;
                // ActionSequenceConstructor.getSequence(targetAction, task)
                Coordinates targetCoordinates = WanderAi.getCoordinates(type, ai);
                if (targetCoordinates == null) {
                    wait = true;
                    // if (!recursion)
                    // return null;
                    // recursion = true;
                    // return getAction(type, ai);
                } else {
                    action = STD_ACTIONS.Move.name();
                    // if (!unit.getAction(action).canBeActivated()) {
                    // }
                    ActionPath path =
                     getPathBuilder().init(
                      new ListMaster<DC_ActiveObj>()
                       .getList(unit.getAction(action)),
                      new Action(unit.getAction(action),
                       new Ref(unit))
                     ).getPathByPriority(
                      new ListMaster<Coordinates>()
                       .getList(targetCoordinates));
                    if (path == null) {

                        ai.setPathBlocked(true); // TODO preCheck if path
                        // appropriate
                    } else {
                        ai.setPathBlocked(false);
                        return path.getActions().get(0);
                    }
                }
                // null if waiting for catchers up or so... turning and resting
                // etc

                if (wait) {
                    action = getIdleAction(ai, type);
                } else {
                    // if (change) {
                    // targetCoordinates = WanderMaster.getCoordinates(type,
                    // ai);
                    // }
                    // return path.getActions().getOrCreate(0);
                }

                // List<Action> s =
                // ActionSequenceConstructor.getTurnSequence(FACING_SINGLE.IN_FRONT,
                // unit, targetCoordinates);
                // if (!s.isEmpty())
                // return s.getOrCreate(0); // cache for speed-up immediate follow up
                // target =
                // ai.getUnit().getGame().getCellByCoordinate(targetCoordinates).getId();

                // TODO if the unit is 'ahead' of his buddies, he should just
                // wait
                // so there is a 'target cell'? Or is it measured by distance?
                // All going in same
                // direction until a unit gets too far ahead/behind (?) then
                // just wait till catch up (limit) and set new direction
                // limit by max distance from *origin*...

                // break;

        }

        DC_ActiveObj active = unit.getAction(action);

        ref.setTarget(target);
        recursion = false;
        return new Action(active, ref);

    }

    private String getIdleAction(UnitAI ai, GOAL_TYPE type) {
        // TODO turn randomly?
        return null;
    }

    public enum LOGIC {
        AVOID_TRAPS,
    }

    public enum TACTIC {
        DELAY, STALK, AMBUSH, ENGAGE
    }
}
