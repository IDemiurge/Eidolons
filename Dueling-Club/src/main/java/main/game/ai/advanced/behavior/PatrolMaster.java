package main.game.ai.advanced.behavior;

import main.content.enums.system.AiEnums;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.ai.GroupAI;
import main.game.ai.UnitAI;
import main.game.ai.elements.actions.Action;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.CoordinatesMaster;
import main.game.battlefield.DirectionMaster;
import main.game.logic.generic.DC_ActionManager.STD_ACTIONS;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PatrolMaster {
    // similar to wandering but all units must be to the target point
    public static Action getPatrolAction(UnitAI ai) {
        Patrol patrol = ai.getGroup().getPatrol();
        if (patrol == null) {
            initPatrol(ai.getGroup());
        }

        Action action = null;
        boolean leader = ai.getGroup().getLeader() == ai.getUnit();
        if (isArrived(patrol, ai)) {
            if (leader) {
                if (checkNewDestination(patrol)) {
                    changeDestination(patrol);
                } else {
                    action = getIdleAction(patrol, ai);
                }
            }
        } else {
            action = getWaitAction(patrol, ai);
        }
        if (action != null) {
            return action;
        }

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
        Unit unit = ai.getUnit();

        Unit blocker = getBlockingUnit(patrol, ai);
        if (blocker == null) {
            return null;
        }

        Ref ref = new Ref(unit);
        ref.setTarget(blocker.getId());
        new Action(ai.getUnit().getAction( "Wait"), ref);
        return null;
    }

    private static Unit getBlockingUnit(Patrol patrol, UnitAI ai) {
        Unit unit = ai.getUnit();
        DIRECTION direction = DirectionMaster.getRelativeDirection(unit.getCoordinates(), patrol
                .getDestination());
        // paths.getOrCreate(unit);
        Coordinates coordinates = unit.getCoordinates().getAdjacentCoordinate(direction);
        // TODO more than 1 coordinate?
        Unit unitByCoordinate = null;// unit.getGame().getUnitByCoordinate(coordinates);
        // preCheck leader?
        Collection<Unit> units = unit.getGame().getUnitsForCoordinates(coordinates);
        for (Obj u : units) {
            // sort?
            Unit blocker = (Unit) u;
            // if (u == leader)
            if (!blocker.isOwnedBy(ai.getUnit().getOwner())) {
                continue;
            }
            if (!blocker.canMove()) {
                continue;
            }
            unitByCoordinate = blocker;
        }
        return unitByCoordinate;

    }

    private static Action getIdleAction(Patrol patrol, UnitAI ai) {
        STD_ACTIONS.Turn_Anticlockwise.name();
        // preCheck Prepare actions - rest, ... if turn is near over? OR
        // getPrepareAction instead or return NULL!
        return null;
    }

    private static boolean checkWait(Patrol patrol, UnitAI ai) {
        if (isBlocked(ai)) {

        }

        Unit unit = getBlockingUnit(patrol, ai);

        return unit.isOwnedBy(ai.getUnit().getOwner());

    }

    public static boolean isArrived(Patrol patrol, UnitAI ai) {
        Unit unit = ai.getUnit();
//		boolean blocked = isBlocked(patrol, ai.getGroup().getLeader());

        return false;
    }

    public static boolean isArrived(Patrol patrol, GroupAI group) {
        Coordinates destination = patrol.getDestination();
        Unit obj = group.getLeader();
        isBlocked(destination, obj);
        return false;

    }

    private static boolean isBlocked(UnitAI ai) {
        // TODO Auto-generated method stub
        return false;
    }

    private static void isBlocked(Coordinates destination, Unit obj) {
//        boolean blocked = obj.getGame().getBattleField().canMoveOnto(obj, destination);
    }

    private static boolean checkCatchersUp(Patrol patrol, int i) {
        for (Unit m : patrol.getGroup().getMembers()) {

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
        Coordinates c;
        if (patrol.getBlock() != null) {
            c = CoordinatesMaster.getFarmostCoordinateInDirection(d, patrol.getBlock()
                    .getCoordinates(), prefLessMoreMiddle);
        } else {
            if (distance == null) {
                distance = WanderMaster.getMaxWanderTotalDistance(patrol.getGroup(),
                        AiEnums.GOAL_TYPE.PATROL);
                patrol.setDistance(distance);
            }
            if (distance == null) {
                c = CoordinatesMaster.getFarmostCoordinateInDirection(d, patrol.getBlock()
                        .getCoordinates(), prefLessMoreMiddle);
            }
            c = patrol.getLeadingUnit().getCoordinates();
            Integer offsetX = distance;
            Integer offsetY = distance;
            if (d.isDiagonal()) {
                offsetX = (int) Math.round(Math.sqrt(distance));
                offsetY = (int) Math.round(Math.sqrt(distance));
            }
            if (d.isGrowX() == null) {
                offsetX = 0;
            } else if (!d.isGrowX()) {
                offsetX = -offsetX;
            }

            if (d.isGrowY() == null) {
                offsetY = 0;
            } else if (!d.isGrowY()) {
                offsetY = -offsetY;
            }

            // preCheck valid coordinates? limit!

            c = new Coordinates(c.x + offsetX, c.y + offsetY);

        }
        return c;
    }

    public static boolean checkNewDestination(Patrol patrol) {
//		if (!isArrived(patrol.getGroup()))
//			return false;
        patrol.turnWaited();
        if (patrol.getTurnsWaited() < patrol.getMinWaitPeriod()) {
            return false;
        }
        if (patrol.getTurnsWaited() >= patrol.getMaxWaitPeriod()) {
            if (checkCatchersUp(patrol, patrol.getTurnsWaited() - patrol.getMaxWaitPeriod())) {
                return true;
            }
        }

        return false;
    }

}
