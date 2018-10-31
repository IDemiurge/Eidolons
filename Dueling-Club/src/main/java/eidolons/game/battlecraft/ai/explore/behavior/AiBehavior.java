package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.UnitAI.AI_BEHAVIOR_MODE;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.tools.path.ActionPath;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;
import main.swing.XLine;
import main.system.auxiliary.ContainerUtils;
import main.system.launch.CoreEngine;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 9/23/2017.
 */
public abstract class AiBehavior {

    protected final GroupAI group;
    protected final UnitAI ai;
    protected final AiMaster master;
    protected final Coordinates origin;

    protected BEHAVIOR_STATUS status;
    protected BEHAVIOR_METHOD method;
    protected LevelBlock block;
    protected Coordinates preferredPosition;
    protected DC_Obj target;
    protected float sinceLastAction;
    protected float globalTimer;
    protected Orders orders;
    protected float timeRequired;
    protected float speed;
    protected Action queuedAction;
    protected Map<XLine, List<Coordinates>> pathCache = new HashMap<>();
    private Action lastAction;
    private List<Action> actionLog=    new ArrayList<>() ;
    private List<Orders> ordersLog=    new ArrayList<>() ;

public void log(){
     log( "Orders: \n" + ContainerUtils.toStringContainer(ordersLog, "\n"));
     log( "Actions: \n" + ContainerUtils.toStringContainer(actionLog, "\n"));

}
    public AiBehavior(AiMaster master, UnitAI ai) {
        this.master = master;
        this.ai = ai;
        this.group = ai.getGroupAI();
        origin = getCoordinates();
        speed = getDefaultSpeed();
        block = master.getGame().getDungeonMaster().getDungeonLevel().getBlockForCoordinate(
         ai.getUnit().getCoordinates());
        if (block == null) {
            for (Coordinates c : ai.getUnit().getCoordinates().getAdjacent()) {
                block = master.getGame().getDungeonMaster().getDungeonLevel().getBlockForCoordinate(
                 c);
                if (block != null)
                    return;
            }
        }
    }

    protected float getDefaultSpeed() {
        return isTestMode()? 5f : 1;
    }

    //    getConstraints() {
    //    }

    public void act(float delta) {
        sinceLastAction += delta;
        globalTimer += delta;
    }

    public boolean canAct() {
        if (queuedAction == null) {
            return false;
        }
        if (timeRequired == 0) {
            return false;
        }
        if (!checkNextActionCanBeMade(queuedAction)) {
            return false;
        }
        return timeRequired <= sinceLastAction;
    }

    public void queueNextAction() {
        if (orders == null) {
            return;
        }
        queuedAction = orders.peekNextAction();
        if (queuedAction == null) {
            orders = null; //done
            timeRequired = 0;
        } else
            timeRequired = getTimeRequired(queuedAction);
    }

    protected float getTimeRequired(Action action) {
        Double cost = action.getActive().getParamDouble(PARAMS.AP_COST);
        return (float) ( cost / getSpeed());
    }

    public Coordinates getCoordinates() {
        return getUnit().getCoordinates();
    }

    public Unit getUnit() {
        return ai.getUnit();
    }

    //returns true if unit is ready to act
    public boolean update() {
        if (!isEnabled())
            return false;
        if (!isUnitActive()){
            resetSinceLastAction();
            return false;
        }
        master.setUnit(ai.getUnit());
        if (checkOrdersValid(orders))
            return true;
        if (!checkNeedsToUpdate()) {
            //means we are 'waiting'... thou we could just stop += delta
            if (status != BEHAVIOR_STATUS.WAITING)
                log("waiting...");
            status = BEHAVIOR_STATUS.WAITING;
            return false;
        }
        if (target != (target = updateTarget())){
            log("target: " + target);
        }
        boolean failed = isFailed();
        if (failed){
            if (failed())
                return false;
        }
        if (!checkNeedsNewOrdersForTarget())
            return true;
        method = updateMethod();
        preferredPosition = updatePreferredPosition();
        if (status != BEHAVIOR_STATUS.RUNNING)
            log("running...");
        status = BEHAVIOR_STATUS.RUNNING;
        initOrders();
        return false; //don't act immediately, but on the next cycle *if* orders are OK by then still
    }

    private boolean isUnitActive() {
        return getUnit().canAct();
    }

    protected boolean failed() {
        log("failed, applying a fix...");
        if (checkCanTeleport()) {
            teleportToLeader();
        } else {

        }
        resetSinceLastAction();
        return true;
    }

    protected boolean isTestMode() {
        return AiBehaviorManager.TEST_MODE;
    }

    protected boolean checkNeedsNewOrdersForTarget() {
        if (target == null) {
            return false;
        }
        return true;
    }

    protected void resetSinceLastAction() {
        sinceLastAction = 0;
    }

    protected void resetTimer() {
        globalTimer = 0;
    }
    protected boolean checkNeedsToUpdate() {
        //        if (target == null) why?
        //            return true;
        if (orders == null) {
            return true;
        }
        if (ai.isLeader())
            if (isFollowOrAvoid() != isNearby()) {
                return true;
            }
        if (!ai.isLeader())
            if (isFollowOrAvoid() == isNearby()) {
                return false;
            }
        return isCustomActionRequired();
    }

    protected boolean isCustomActionRequired() {
        if (isFacingChangeRequired())
            return true;
        return false;
    }

    protected boolean isFacingChangeRequired() {
        if (getRequiredFacing() == null) {
            return false;
        }
        return getRequiredFacing() != ai.getUnit().getFacing();
    }

    protected FACING_DIRECTION getRequiredFacing() {
        return null;
    }

    protected abstract boolean isFollowOrAvoid();

    protected BEHAVIOR_METHOD updateMethod() {
        return method;
    }

    protected DC_Obj updateTarget() {
        if (target == null) {
            if (ai.getGroupAI() == null) {
                return getUnit();
            }
            target = ai.getGroupAI().getLeader();
        }
        return target;
    }

    protected void log(String msg) {
        if (isLogged()) {
            main.system.auxiliary.log.LogMaster.log(1, this + ": " + msg);
        }
    }

    @Override
    public String toString() {
        return getUnit().getNameAndCoordinate() + " " + getType() + " ai";
    }

    public abstract AI_BEHAVIOR_MODE getType();

    protected boolean isEnabled() {
        if (AiBehaviorManager.TESTED != null) {
            return getType() == AiBehaviorManager.TESTED;
        }
        return true;
    }

    protected boolean isLogged() {
        return CoreEngine.isIDE();
    }

    protected void initOrders() {
        ActionSequence actions = getOrders();
        if (actions == null) {
//            log("null orders!");
            return; // can it be? 
        }
        orders =new Orders(actions);
        log("new orders: " + orders);

        ordersLog.add(orders);

//        ai.setStandingOrders(orders);
    }

    public Action nextAction() {
        if (status == BEHAVIOR_STATUS.WAITING)
            return null;

        if (orders == null) {
            return null; // can it be? 
        }
        //TODO last action
        orders.popNextAction(); //ensure sync

        log("Action to execute: " + queuedAction);
        lastAction = queuedAction;
        actionLog.add(lastAction);
        queuedAction=null;
        resetSinceLastAction();
        return lastAction;
    }

    protected boolean checkNextActionCanBeMade(Action action) {
        if (action == null) {
            return false;
        }
        return true;
    }

    public ActionSequence getOrders() {
        //check facing change required
        ActionSequence orders = getCustomOrders();

        if (orders != null) {
            return orders;
        }

        List<Coordinates> targetCells = new ArrayList<>();
        if (isTargetCoordinateValid())
            targetCells.add(target.getCoordinates());

        if (isAdjacentToTargetValid()) {
            targetCells = new ArrayList<>(
             target.getCoordinates().getAdjacentCoordinates());
        }
        orders = getMoveOrders(targetCells);
        return orders;
    }

    protected boolean checkOrdersValid(Orders orders) {
        if (orders == null) {
            return false;
        }
        //if something changes... or

        return true;
    }

    protected boolean isTargetCoordinateValid() {
        return !(target instanceof BattleFieldObject);
    }

    protected boolean isAdjacentToTargetValid() {
        return target instanceof BattleFieldObject;
    }

    protected ActionSequence getCustomOrders() {
        ActionSequence orders = getFacingChangeOrders();

        if (orders != null) {
            return orders;
        }
        orders = getMethodOrders();
        return orders;
    }

    protected ActionSequence getFacingChangeOrders() {
        FACING_DIRECTION required = getRequiredFacing();
        if (required == null) {
            return null;
        }
        if (required == getUnit().getFacing())
            return null;

        Coordinates c = ai.getUnit().getCoordinates().getAdjacentCoordinate(getRequiredFacing().getDirection());
        if (c == null) {
            return null;
        }
        return new ActionSequence(master.getTurnSequenceConstructor().getTurnSequence(ai.getUnit(), c),
         ai);
    }

    protected DC_Cell getCell(Coordinates coordinates) {
        return ai.getUnit().getGame().getCellByCoordinate(coordinates);
    }

    protected ActionSequence getMethodOrders() {
        return null;
    }

    public ActionSequence getMoveOrders(List<Coordinates> validCells) {

        Coordinates cell = chooseMoveTarget(validCells);

        XLine line = new XLine(getCoordinates(), cell);
        List<Coordinates> pathChain = pathCache.get(line);
        if (pathChain == null) {
            pathChain = master.getPathBuilderAtomic().getPathChain(
             getUnit(), getCoordinates(), cell, false, 15);
            pathCache.put(line, pathChain);
        }
        boolean atomic = false;
        if (isAtomicAllowed()) {
//            if (cell.isAdjacent(ai.getUnit().getCoordinates()))
                atomic = true;
        }

        int n =atomic? 0:  Math.min(4, pathChain.size() / 2);
        if (pathChain.size()<=n) {
            if (pathChain.size()<=1) {
                return null;
            }
            n=0;
        }
        Coordinates goal = pathChain.get(n);

        if (atomic) {
            Action action = master.getAtomicAi().getAtomicMove(goal, ai.getUnit());
            //                action = getMaster(ai).getAtomicAi().getAtomicActionApproach(ai);
            if (action != null)
                return new ActionSequence(GOAL_TYPE.WANDER, action);
        }

        List<Coordinates> preferred = validCells.stream().filter(c -> c == goal).collect(Collectors.toList());

        master.getPathBuilder().setUnit(ai.getUnit());
        ActionPath path = master.getPathBuilder().getPathByPriority(preferred);
        if (path == null)
            return null;
        ActionSequence sequence = master.getActionSequenceConstructor().getSequenceFromPath(path, ai);

        return sequence;
    }

    protected boolean isAtomicAllowed() {
        return true;
    }

    protected Coordinates chooseMoveTarget(List<Coordinates> validCells) {
        return CoordinatesMaster.getClosestTo(getCoordinates(), validCells);
    }

    public void teleportToLeader() {
        //yeah...
        if (target == null) {
            return;
        }
        ai.getUnit().setCoordinates(target.getCoordinates());
    }

    protected boolean isNearby() {
        double dst = PositionMaster.getExactDistance(ai.getUnit(), target);
        return dst <= getDistanceForNearby();

    }

    protected double getDistanceForNearby() {
        return 1;
    }

    protected boolean checkCanTeleport() {
        double dst = PositionMaster.getExactDistance(ai.getUnit(), Eidolons.getMainHero());
        if (dst < Eidolons.getMainHero().
         getMaxVisionDistanceTowards(ai.getUnit().getCoordinates())) {
            return false;
        }
        return true;
    }

    public boolean isPositionValid(Coordinates c) {
        if (!block.getCoordinatesList().contains(c)) {
            return false;
        }
        return c.dst_(preferredPosition) <= getDistanceForNearby();
    }

    public Coordinates updatePreferredPosition() {
        //follow from behind...
        if (target instanceof Unit) {
            DIRECTION d = ((Unit) target).getFacing().flip().getDirection();
            Coordinates c = target.getCoordinates().getAdjacentCoordinate(d);
            if (c != null) {
                return c;
            }

        }
        return target.getCoordinates();
    }

    protected boolean isFailed() {
        return sinceLastAction > getTimeBeforeFail();
    }

    protected float getTimeBeforeFail() {
        return 50/speed;
    }

    protected AiMaster getMaster(UnitAI ai) {
        return ai.getUnit().getGame().getAiManager();
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getDebugInfo() {
        return sinceLastAction + " "
         + queuedAction + " " + group.getLeader().getName();
    }


    public enum BEHAVIOR_METHOD {
        WANDER,
        MARCH,
        SPRINT,
        SNEAK,
        SEARCH,
        REST,
    }

    public enum BEHAVIOR_STATUS {
        WAITING,
        RUNNING,
        BLOCKED,

    }
}
