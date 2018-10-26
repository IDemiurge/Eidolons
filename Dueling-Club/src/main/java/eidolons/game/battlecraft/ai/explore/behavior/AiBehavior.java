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
    protected float timer;
    protected ActionSequence orders;
    protected float timeRequired;
    protected float speed;
    protected Action queuedAction;
    protected Map<XLine, List<Coordinates>> pathCache = new HashMap<>();


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
        return 1;
    }

    //    getConstraints() {
    //    }

    public void act(float delta) {
        timer += delta;
    }

    public boolean canAct() {
        if (timeRequired == 0) {
            return false;
        }
        if (!checkNextActionCanBeMade(queuedAction)) {
            return false;
        }
        return timeRequired <= timer;
    }

    public void queueNextAction() {
        if (orders == null) {
            return;
        }
        queuedAction = orders.peekNextAction();
        if (queuedAction == null) {
            orders = null;
            timeRequired = 0;
        } else
            timeRequired = getTimeRequired(queuedAction);
    }

    protected float getTimeRequired(Action action) {
        Double cost = action.getActive().getParamDouble(PARAMS.AP_COST);
        return (float) (10 * cost / getSpeed());
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
        target = updateTarget();
        if (!checkNeedsNewOrdersForTarget())
            return true;
        method = updateMethod();
        preferredPosition = updatePreferredPosition();
        boolean lost = isFailed();
        if (lost)
            if (checkCanTeleport()) {
                teleportToLeader();
                resetTimer();
            }
        if (status != BEHAVIOR_STATUS.RUNNING)
            log("running...");
        status = BEHAVIOR_STATUS.RUNNING;
        initOrders();
        return false; //don't act immediately, but on the next cycle *if* orders are OK by then still
    }

    protected boolean checkNeedsNewOrdersForTarget() {
        if (target == null) {
            return false;
        }
        return true;
    }

    protected void resetTimer() {
        timer = 0;
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

    protected abstract AI_BEHAVIOR_MODE getType();

    protected boolean isEnabled() {
        if (AiBehaviorManager.TESTED != null) {
            return getType() == AiBehaviorManager.TESTED;
        }
        return true;
    }

    protected boolean isLogged() {
        return true;
    }

    protected void initOrders() {
        orders = getOrders();
        if (orders == null) {
            log("null orders!");
            return; // can it be? 
        }
        log("new orders: " + orders);
        ai.setStandingOrders(orders);
    }

    public Action nextAction() {
        if (status == BEHAVIOR_STATUS.WAITING)
            return null;

        if (orders == null) {
            return null; // can it be? 
        }
        //TODO last action
        orders.popNextAction(); //ensure sync

        log("next action: " + queuedAction);
        return queuedAction;
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

    protected boolean checkOrdersValid(ActionSequence orders) {
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
        boolean atomic = false;
        if (isAtomicAllowed()) {
            if (cell.isAdjacent(ai.getUnit().getCoordinates()))
                atomic = true;
        }
        if (atomic) {
            Action action = master.getAtomicAi().getAtomicMove(cell, ai.getUnit());
            //                action = getMaster(ai).getAtomicAi().getAtomicActionApproach(ai);
            if (action != null)
                return new ActionSequence(GOAL_TYPE.WANDER, action);
        }

        XLine line = new XLine(getCoordinates(), cell);
        List<Coordinates> pathChain = pathCache.get(line);
        if (pathChain == null) {
            pathChain = master.getPathBuilderAtomic().getPathChain(
             getUnit(), getCoordinates(), cell, false, 15);
            pathCache.put(line, pathChain);
        }
        int n = Math.min(4, pathChain.size() / 2);

        Coordinates goal = pathChain.get(n);
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
        ai.getUnit().setCoordinates(preferredPosition);
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
        return timer > getTimeBeforeFail();
    }

    protected float getTimeBeforeFail() {
        return 50;
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
