package main.game.module.dungeoncrawl.ai;

import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.entity.obj.DC_Cell;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.GroupAI;
import main.game.battlecraft.ai.UnitAI;
import main.game.battlecraft.ai.elements.actions.Action;
import main.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import main.game.battlecraft.ai.elements.task.Task;
import main.game.battlecraft.ai.tools.Analyzer;
import main.game.battlecraft.ai.tools.path.ActionPath;
import main.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.game.battlecraft.logic.dungeon.location.LocationBuilder.BLOCK_TYPE;
import main.game.battlecraft.logic.dungeon.universal.Dungeon;
import main.game.battlecraft.logic.dungeon.universal.Positioner;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.bf.DirectionMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class WanderAi extends AiBehavior {

    public static List<? extends DC_Obj> getWanderCells(UnitAI ai) {
        DIRECTION d = ai.getGroup().getWanderDirection();
        // permittedCells = ai.getGroup().getWanderBlocks();
        List<DC_Obj> list = new ArrayList<>();
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
            List<UnitAI> forwards = new ArrayList<>();
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
            case STAND_GUARD:
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

        if (type == AiEnums.GOAL_TYPE.PATROL) {
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

        } else if (type == AiEnums.GOAL_TYPE.WANDER) {
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
        Coordinates targetCoordinates = WanderAi.getWanderTargetCoordinatesCell(ai, type);
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
                    WanderAi.changeGroupMoveDirection(group, type);
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

    @Override
    public ActionSequence getOrders(UnitAI ai) {
        Coordinates c1 = null;
        try {
            WanderAi.checkWanderDirectionChange(ai.getGroup(), GOAL_TYPE.WANDER);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        try {
            c1 = (getWanderTargetCoordinatesCell(ai, GOAL_TYPE.WANDER));
        } catch (Exception e) {
            c1 = (CoordinatesMaster.getRandomAdjacentCoordinate(ai.getUnit().getCoordinates()));
            main.system.ExceptionMaster.printStackTrace(e);
        }
        c1 = Positioner.adjustCoordinate(ai.getUnit(), c1, ai.getUnit().getFacing()
         , getWanderPredicate(ai.getUnit(), ai.getUnit().getFacing(), c1));

        Task task = new Task(ai, GOAL_TYPE.WANDER, null);

        List<Action> turnSequence = getMaster(ai).
         getTurnSequenceConstructor().getTurnSequence(ai.getUnit(), c1);
        if (ListMaster.isNotEmpty(turnSequence)) {
            return new ActionSequence(turnSequence, task, ai);
        }

        List<Coordinates> c = new ArrayList<>();
        c.add(c1);
        getMaster(ai).setUnit(ai.getUnit());
//        getMaster(ai).getPathBuilder().init(null, null);
//        TimeLimitMaster.markTimeForAI(ai);
        List<ActionPath> paths = new ArrayList<>();
//         getMaster(ai).getPathBuilder().build(c);

        Action action = null;
        if (paths.isEmpty()) {
            if (c.get(0) != null)
                action = getMaster(ai).getAtomicAi().getAtomicMove(c.get(0), ai.getUnit());
            else
                action = getMaster(ai).getAtomicAi().getAtomicActionApproach(ai);
            if (action != null)
                return new ActionSequence(GOAL_TYPE.WANDER, action);
        } else
            action = paths.get(0).getActions().get(0);
        if (action == null)
            return null;
        List<ActionSequence> sequences = getMaster(ai).getActionSequenceConstructor().
         getSequencesFromPaths(paths, task, action);
        if (sequences.isEmpty())
            return null;
        return sequences.get(0);
    }

    private Predicate<Coordinates> getWanderPredicate(Unit unit,
                                                      FACING_DIRECTION facing,
                                                      Coordinates c1) {
        return new Predicate<Coordinates>() {
            @Override
            public boolean test(Coordinates coordinates) {
                int wallCount = 0;
                for (Coordinates sub : c1.getAdjacentCoordinates()) {
                    if (unit.getGame().getBattleFieldManager().getWallMap().get(sub) != null) {
                        wallCount++;
                    }
                }
                if (unit.getAI().getType().isRanged()) {
                    return wallCount >= 4;
                }
                if (unit.getAI().getType().isCaster()) {
                    return wallCount >= 4;
                }

                return wallCount >= 3;
            }
        };
    }
}










