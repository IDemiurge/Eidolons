package main.game.ai.advanced.behavior;

import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.ai.GroupAI;
import main.game.ai.UnitAI;
import main.game.ai.elements.goal.Goal.GOAL_TYPE;
import main.game.ai.tools.Analyzer;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.DirectionMaster;
import main.game.logic.dungeon.Dungeon;
import main.game.logic.dungeon.building.DungeonBuilder.BLOCK_TYPE;
import main.game.logic.generic.Positioner;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.math.PositionMaster;

import java.util.LinkedList;
import java.util.List;

public class WanderMaster {

    public static List<? extends DC_Obj> getWanderCells(UnitAI ai) {
        DIRECTION d = ai.getGroup().getWanderDirection();
        // permittedCells = ai.getGroup().getWanderBlocks();
        List<DC_Obj> list = new LinkedList<>();
        for (DC_Cell cell : Analyzer.getCells(ai, false, false, true)) {
            if (d != null) {
                if (DirectionMaster.getRelativeDirection(cell, ai.getUnit()) != d) {
                    continue;
                }
            }
            if (PositionMaster.getDistance(cell, ai.getUnit()) <= ai.getMaxWanderDistance()) {
                list.add(cell);
            }
        }
        if (list.isEmpty()) {
            // change direction?
        }
        return list;
    }

    public static Coordinates getWanderTargetCoordinatesCell(UnitAI ai, GOAL_TYPE type) {
        // List<? extends DC_Obj> cells = getWanderCells(ai);
        // ai.getStandingOrders(); TODO set target???
        // // from original coordinate? Or 'last'?
        // ai.getGroup().getWanderDistance();
        boolean follow = ai.getGroup().isFollowLeader() || isFollowLeader(type);
        if (follow) {
            if (isAheadOfLeader(ai)) {
                return null;
            }
        }
        DIRECTION direction = ai.getGroup().getWanderDirection();
        // DirectionMaster.getRelativeDirection(source, target);
        if (direction == null) {
            return null;
        }
        Coordinates c = ai.getUnit().getCoordinates().getAdjacentCoordinate(direction);
        return c;
        // auto-turn if facing ain't right? take relative *position*

    }

    private static boolean isAheadOfLeader(UnitAI ai) {
        Coordinates c = ai.getGroup().getOriginCoordinates();
        return PositionMaster.getDistance(c, ai.getUnit().getCoordinates()) > PositionMaster
                .getDistance(c, ai.getGroup().getLeader().getCoordinates());
        // ai.getGroup().getTargetCoordinate();

    }

    private static boolean checkUnitArrived(UnitAI ai, GOAL_TYPE type) {

        GroupAI group = ai.getGroup();
        Coordinates c = group.getOriginCoordinates();
        int maxTotal = getMaxWanderTotalDistance(group, type);
        Coordinates c2 = group.getWanderStepCoordinateStack().peek();
        int maxStep = getMaxWanderStepDistance(group, type);
        if (PositionMaster.getDistance(c, ai.getUnit().getCoordinates()) > maxTotal) {
            return true;
        }
        if (c2 != null) {
            if (PositionMaster.getDistance(c2, ai.getUnit().getCoordinates()) > maxStep) {
                return true;
            }
        }
        return false;
    }

    private static int getMaxWanderStepDistance(GroupAI group, GOAL_TYPE type) {
        // TODO Auto-generated method stub
        return getMaxWanderTotalDistance(group, type) / 2;
    }

    public static int getMaxWanderTotalDistance(GroupAI group, GOAL_TYPE type) {
        Boolean x_y_diag = !group.getWanderDirection().isVertical();
        if (group.getWanderDirection().isDiagonal()) {
            x_y_diag = null;
        }
        Dungeon dungeon = group.getLeader().getGame().getDungeon();
        switch (type) {
            case SEARCH:
            case STALK: // keep the distance from the *target*, not origin...
        } // group.getLeader().getGame().getDungeon().getCellsX()
        if (x_y_diag == null) {
            return (int) Math
                    .round(Math.sqrt(dungeon.getSquare()) * getDistanceFactor(type, group));
        }
        if (x_y_diag) {
            return Math.round(dungeon.getCellsX() * getDistanceFactor(type, group));
        }
        return Math.round(dungeon.getCellsY() * getDistanceFactor(type, group));
        // TODO maybe the block, the zone? Whole dungeon could be huge... or
        // small...
    }

    private static float getDistanceFactor(GOAL_TYPE type, GroupAI group) {
        return 0.3f;
    }

    private static boolean checkProgressObstructed(DIRECTION direction, UnitAI ai, GOAL_TYPE type) {
        return ai.isPathBlocked();
        // Coordinates c =
        // ai.getUnit().getCoordinates().getAdjacentCoordinate(direction);
        // if (c == null)
        // return true;
        // if (c.isInvalid())
        // return true;
        // // what's the point of such a preCheck? blocked() should be passed
        // return
        // ai.getUnit().getGame().getBattleFieldManager().canMoveOnto(ai.getUnit(),
        // c);

    }

    public static Boolean checkWanderDirectionChange(GroupAI group, GOAL_TYPE type) {

        boolean change = false;

        if (checkUnitArrived(group.getLeader().getUnitAI(), type)) {
            change = true;
        } else {
            List<UnitAI> forwards = new LinkedList<>();
            for (Unit unit : group.getMembers()) {
                UnitAI ai = unit.getUnitAI();
                boolean done = checkUnitArrived(ai, type);
                if (!done) {
                    done = checkProgressObstructed(group.getWanderDirection(), ai, type);
                }
                if (done)
                // if (PositionMaster.getDistance(c,
                // m.getUnit().getCoordinates()) > max)
                {
                    forwards.add(ai);
                    if (forwards.size() > Math.round(group.getMembers().size()
                            * getCatchUpFactor(group, type))) {

                        // if (group.isCatchUp())
                        // change = null;
                        // if units are too far
                        // laggers.add(ai);
                        // cannot be a lagger if obstructed?
                        change = true;
                        break;
                    }
                }
            }
            // need to have most units far enough?
            // distance from each other? don't forget the leader... he is
            // special
        }
        return change;
    }

    private static float getCatchUpFactor(GroupAI group, GOAL_TYPE type) {
        return 0.5f;
    }

    private static boolean isFollowLeader(GOAL_TYPE type) {
        switch (type) {
            case SEARCH:
            case AGGRO:
            case WANDER:
                return false;
            case STALK:
            case GUARD:
            case PATROL:
                return true;
        }
        return false;
    }

    public static void initPatrol(GroupAI group, boolean guard) {
        if (!guard) {
            try {
                guard = group.getCreepGroup().getBlock().getType() == BLOCK_TYPE.CORRIDOR;
            } catch (Exception e) {
            }
        }
        if (guard) {
            group.setBackAndForth(true); // corridor?
        } else {
            group.setClockwisePatrol(RandomWizard.random());
        }
    }

    public static void changeGroupMoveDirection(GroupAI group, GOAL_TYPE type) {
        DIRECTION wanderDirection = group.getWanderDirection();

        if (type == GOAL_TYPE.PATROL) {
            // back-forth or in circle (square); alternate or always
            // one-way-turn
            if (group.isBackAndForth()) // GUARD?
            {
                wanderDirection = DirectionMaster.rotate90(DirectionMaster.rotate90(
                        wanderDirection, true), true);
            } else {
                wanderDirection = DirectionMaster.rotate90(wanderDirection, group
                        .isClockwisePatrol());
            }

        } else if (type == GOAL_TYPE.WANDER) {
            if (PositionMaster.getDistance(group.getOriginCoordinates(), group.getLeader()
                    .getCoordinates()) >= getMaxWanderTotalDistance(group, type)) {
                wanderDirection = DirectionMaster.getRelativeDirection(group.getLeader()
                        .getCoordinates(), group.getOriginCoordinates());
            }

        }
        if (wanderDirection == group.getWanderDirection()) {
            if (RandomWizard.random() || RandomWizard.random()) {
                wanderDirection = DirectionMaster.rotate45(wanderDirection, RandomWizard.random());
            } else {
                wanderDirection = DirectionMaster.rotate90(wanderDirection, RandomWizard.random());
            }
        }
        // if () //TODO change of opposite!
        // DirectionMaster.getDirectionByDegree(degrees);
        // getOrCreate direction by Leader's facing at the time, and make him
        // turn and turn while waiting!
        group.setWanderDirection(wanderDirection);
    }

    public static Coordinates getCoordinates(GOAL_TYPE type, UnitAI ai) {
        Coordinates targetCoordinates = WanderMaster.getWanderTargetCoordinatesCell(ai, type);
        Unit unit = ai.getUnit();
        GroupAI group = ai.getGroup();
        boolean adjust = targetCoordinates == null;
        if (!adjust) {
            adjust = (!unit.getGame().getRules().getStackingRule().canBeMovedOnto(unit,
                    targetCoordinates));
        }
        if (adjust) {
            Coordinates c = null;
            Loop loop = new Loop(50);
            while (loop.continues()) {
                c = Positioner.adjustCoordinate(targetCoordinates, null);
                if (c == null) {
                    group.getWanderStepCoordinateStack().push(group.getLeader().getCoordinates());
                    WanderMaster.changeGroupMoveDirection(group, type);
                    return null;
                }
                if (DirectionMaster.getRelativeDirection(unit.getCoordinates(), c) != group
                        .getWanderDirection()) {

                    continue;
                }
                break;
            }
            targetCoordinates = c;
        }
        return targetCoordinates;
    }

}
