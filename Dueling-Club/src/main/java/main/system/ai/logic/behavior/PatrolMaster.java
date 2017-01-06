package main.system.ai.logic.behavior;

import main.entity.Ref;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.Obj;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.DirectionMaster;
import main.game.logic.macro.utils.CoordinatesMaster;
import main.rules.DC_ActionManager.STD_ACTIONS;
import main.system.ai.GroupAI;
import main.system.ai.UnitAI;
import main.system.ai.logic.actions.Action;
import main.system.ai.logic.goal.Goal.GOAL_TYPE;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PatrolMaster {
    // similar to wandering but all units must be to the target point
    public static Action getPatrolAction(UnitAI ai) {
        Patrol patrol = ai.getGroup().getPatrol();
        if (patrol == null)
            initPatrol(ai.getGroup());

        Action action = null;
        boolean leader = ai.getGroup().getLeader() == ai.getUnit();
        if (isArrived(patrol, ai)) {
            if (leader)
                if (checkNewDestination(patrol)) {
                    changeDestination(patrol);
                } else
                    action = getIdleAction(patrol, ai);
        } else {
            action = getWaitAction(patrol, ai);
        }
        if (action != null)
            return action;

        if (patrol.getReturnCoordinates() != null) {
            patrol.setDestination(patrol.getReturnCoordinates());
            patrol.setReturnCoordinates(null);
        }
        Coordinates c = patrol.getDestination();
        if (!leader) {
            Coordinates leaderCoordinates = ai.getGroup().getLeader().getCoordinates();
            DIRECTION direction = DirectionMaster.getRelativeDirection(patrol.getDestination(),
                    leaderCoordinates);

            List<Object> list = new LinkedList<>();

            list.add(leaderCoordinates.getAdjacentCoordinate(direction));
            list.add(leaderCoordinates.getAdjacentCoordinate(DirectionMaster.rotate45(direction,
                    true)));
            list.add(leaderCoordinates.getAdjacentCoordinate(DirectionMaster.rotate45(direction,
                    false)));

            c = leaderCoordinates.getAdjacentCoordinate(direction);
        }
        boolean catchingUp = false;
//		isSpecialActionsAllowed(ai, patrol);
//		new PathBuilder(moveActions, null).getPathByPriority(c);
//		paths.put(ai, path);
        // make sure paths stick together! getMaxDistanceForNodes(paths)
        return action;
    }

    private static Action getWaitAction(Patrol patrol, UnitAI ai) {
        DC_HeroObj unit = ai.getUnit();

        DC_HeroObj blocker = getBlockingUnit(patrol, ai);
        if (blocker == null)
            return null;

        Ref ref = new Ref(unit);
        ref.setTarget(blocker.getId());
        new Action(ai.getUnit().getAction(STD_ACTIONS.Wait.name()), ref);
        return null;
    }

    private static DC_HeroObj getBlockingUnit(Patrol patrol, UnitAI ai) {
        DC_HeroObj unit = ai.getUnit();
        DIRECTION direction = DirectionMaster.getRelativeDirection(unit.getCoordinates(), patrol
                .getDestination());
        // paths.getOrCreate(unit);
        Coordinates coordinates = unit.getCoordinates().getAdjacentCoordinate(direction);
        // TODO more than 1 coordinate?
        DC_HeroObj unitByCoordinate = null;// unit.getGame().getUnitByCoordinate(coordinates);
        // check leader?
        Collection<Obj> units = unit.getGame().getUnitsForCoordinates(coordinates);
        for (Obj u : units) {
            // sort?
            DC_HeroObj blocker = (DC_HeroObj) u;
            // if (u == leader)
            if (!blocker.isOwnedBy(ai.getUnit().getOwner()))
                continue;
            if (!blocker.canMove())
                continue;
            unitByCoordinate = blocker;
        }
        return unitByCoordinate;

    }

    private static Action getIdleAction(Patrol patrol, UnitAI ai) {
        STD_ACTIONS.Turn_Anticlockwise.name();
        // check Prepare actions - rest, ... if turn is near over? OR
        // getPrepareAction instead or return NULL!
        return null;
    }

    private static boolean checkWait(Patrol patrol, UnitAI ai) {
        if (isBlocked(ai)) {

        }

        DC_HeroObj unit = getBlockingUnit(patrol, ai);

        if (unit.isOwnedBy(ai.getUnit().getOwner()))
            return true;

        return false;
    }

    public static boolean isArrived(Patrol patrol, UnitAI ai) {
        DC_HeroObj unit = ai.getUnit();
//		boolean blocked = isBlocked(patrol, ai.getGroup().getLeader());

        return false;
    }

    public static boolean isArrived(Patrol patrol, GroupAI group) {
        Coordinates destination = patrol.getDestination();
        DC_HeroObj obj = group.getLeader();
        isBlocked(destination, obj);
        return false;

    }

    private static boolean isBlocked(UnitAI ai) {
        // TODO Auto-generated method stub
        return false;
    }

    private static void isBlocked(Coordinates destination, DC_HeroObj obj) {
        boolean blocked = obj.getGame().getBattleField().canMoveOnto(obj, destination);
    }

    private static boolean checkCatchersUp(Patrol patrol, int i) {
        for (DC_HeroObj m : patrol.getGroup().getMembers()) {

        }
        return false;
    }

    private static void initPatrol(GroupAI group) {
        Patrol patrol = new Patrol(group);
        group.setPatrol(patrol);

    }

    private static void changeDestination(Patrol patrol) {
        DIRECTION d = patrol.getDirection();
        if (d == null) {
            patrol.getLeadingUnit();
        }
        if (!patrol.isBackAndForth()) {
            d = DirectionMaster.rotate90(d, patrol.isClockwise());
        } else {
            d = DirectionMaster.flip(patrol.getDirection());
        }
        Coordinates newDestination = getNewDestination(patrol, d);
        patrol.setDestination(newDestination);

    }

    public static void interruptPatrol(Patrol patrol) {
        patrol.setReturnCoordinates(patrol.getLeadingUnit().getCoordinates());
    }

    public static void resumePatrol(Patrol patrol) {

        // could be really anywhere. probably should return...
    }

    public static Coordinates getNewDestination(Patrol patrol, DIRECTION d) {

        Boolean prefLessMoreMiddle = null;
        Integer distance = patrol.getDistance();
        Coordinates c = null;
        if (patrol.getBlock() != null)
            c = CoordinatesMaster.getFarmostCoordinateInDirection(d, patrol.getBlock()
                    .getCoordinates(), prefLessMoreMiddle);
        else {
            if (distance == null) {
                distance = WanderMaster.getMaxWanderTotalDistance(patrol.getGroup(),
                        GOAL_TYPE.PATROL);
                patrol.setDistance(distance);
            }
            if (distance == null)
                c = CoordinatesMaster.getFarmostCoordinateInDirection(d, patrol.getBlock()
                        .getCoordinates(), prefLessMoreMiddle);
            c = patrol.getLeadingUnit().getCoordinates();
            Integer offsetX = distance;
            Integer offsetY = distance;
            if (d.isDiagonal()) {
                offsetX = (int) Math.round(Math.sqrt(distance));
                offsetY = (int) Math.round(Math.sqrt(distance));
            }
            if (d.isGrowX() == null)
                offsetX = 0;
            else if (!d.isGrowX())
                offsetX = -offsetX;

            if (d.isGrowY() == null)
                offsetY = 0;
            else if (!d.isGrowY())
                offsetY = -offsetY;

            // check valid coordinates? limit!

            c = new Coordinates(c.x + offsetX, c.y + offsetY);

        }
        return c;
    }

    public static boolean checkNewDestination(Patrol patrol) {
//		if (!isArrived(patrol.getGroup()))
//			return false;
        patrol.turnWaited();
        if (patrol.getTurnsWaited() < patrol.getMinWaitPeriod())
            return false;
        if (patrol.getTurnsWaited() >= patrol.getMaxWaitPeriod()) {
            if (checkCatchersUp(patrol, patrol.getTurnsWaited() - patrol.getMaxWaitPeriod()))
                return true;
        }

        return false;
    }

}
