package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.GroupAI;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.UnitAI.AI_BEHAVIOR_MODE;
import eidolons.game.battlecraft.ai.elements.actions.Action;
import eidolons.game.battlecraft.ai.elements.actions.sequence.ActionSequence;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.ai.elements.task.Task;
import eidolons.game.battlecraft.ai.tools.Analyzer;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.Dungeon;
import eidolons.game.battlecraft.logic.dungeon.universal.Positioner;
import main.content.enums.system.AiEnums;
import main.content.enums.system.AiEnums.GOAL_TYPE;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class WanderAiMaster extends AiBehavior {

    public WanderAiMaster(AiMaster master, UnitAI ai) {
        super(master, ai);
    }

    @Override
    protected boolean isFollowOrAvoid() {
        return true;
    }

    @Override
    public AI_BEHAVIOR_MODE getType() {
        return AI_BEHAVIOR_MODE.WANDER;
    }

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
            if (PositionMaster.getDistance(cell, ai.getUnit()) <= getMaxWanderDistance()) {
                list.add(cell);
            }
        }
        if (list.isEmpty()) {
            // change direction?
        }
        return list;
    }


    protected static int getMaxWanderDistance() {
        return 5;
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

    protected static boolean isAheadOfLeader(UnitAI ai) {
        Coordinates c = ai.getGroup().getOriginCoordinates();
        return PositionMaster.getDistance(c, ai.getUnit().getCoordinates()) > PositionMaster
         .getDistance(c, ai.getGroup().getLeader().getCoordinates());
        // ai.getGroup().getTargetCoordinate();

    }

    protected static boolean checkUnitArrived(UnitAI ai, GOAL_TYPE type) {
        return ai.isPathBlocked();
        //        GroupAI group = ai.getGroup();
        //        Coordinates c = group.getOriginCoordinates();
        //        int maxTotal = getMaxWanderTotalDistance(group, type);
        //        Coordinates c2 = group.getWanderStepCoordinateStack().peek();
        //        int maxStep = getMaxWanderStepDistance(group, type);
        //        if (PositionMaster.getDistance(c, ai.getUnit().getCoordinates()) > maxTotal) {
        //            return true;
        //        }
        //        if (c2 != null) {
        //            if (PositionMaster.getDistance(c2, ai.getUnit().getCoordinates()) > maxStep) {
        //                return true;
        //            }
        //        }
        //        return false;
    }

    protected static int getMaxWanderStepDistance(GroupAI group, GOAL_TYPE type) {
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

    protected static float getDistanceFactor(GOAL_TYPE type, GroupAI group) {
        return 0.3f;
    }

    protected static boolean isProgressObstructed(DIRECTION direction,
                                                UnitAI ai, GOAL_TYPE type) {
        //      TODO cache needed? if (ai.isPathBlocked()!=null )
        //           return ai.isPathBlocked();
        boolean blocked = checkProgressObstructed(direction, ai );
        ai.setPathBlocked(blocked);
        return blocked;
    }

    protected static boolean checkProgressObstructed(DIRECTION direction, UnitAI ai ) {
        Coordinates c =
         ai.getUnit().getCoordinates().getAdjacentCoordinate(direction);
        if (c == null)
            return true;
        if (c.isInvalid())
            return true;
        // what's the point of such a preCheck? blocked() should be passed
        return
         !ai.getUnit().getGame().getBattleFieldManager().canMoveOnto(ai.getUnit(),
          c);

    }

    public static Boolean checkWanderDirectionChange(GroupAI group, GOAL_TYPE type) {

        boolean change = false;

        //        if (checkUnitArrived(group.getLeader().getUnitAi(), type)) {
        //            change = true;
        //        } else {
        List<UnitAI> forwards = new ArrayList<>();
        for (Unit unit : group.getMembers()) {
            UnitAI ai = unit.getUnitAI();
            boolean done = checkUnitArrived(ai, type);
            if (!done) {
                done = isProgressObstructed(group.getWanderDirection(), ai, type);
            }
            if (done)
            // if (PositionMaster.getDistance(c,
            // m.getUnit().getCoordinates()) > max)
            {
                forwards.add(ai);
                if (
                 group.getMembers().size() == 1 ||
                  forwards.size() > Math.round(group.getMembers().size()
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
        //        }
        return change;
    }

    protected static float getCatchUpFactor(GroupAI group, GOAL_TYPE type) {
        return 0.5f;
    }

    protected static boolean isFollowLeader(GOAL_TYPE type) {
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
            //            if (RandomWizard.random() || RandomWizard.random()) {
            //                wanderDirection = DirectionMaster.rotate45(wanderDirection, RandomWizard.random());
            //            } else {
            wanderDirection = DirectionMaster.rotate90(wanderDirection, RandomWizard.random());
            //            }
        }
        // if () //TODO change of opposite!
        // DirectionMaster.getDirectionByDegree(degrees);
        // getOrCreate direction by Leader's facing at the time, and make him
        // turn and turn while waiting!
        group.setWanderDirection(wanderDirection);
        group.getMembers().forEach(unit -> unit.getAI().setPathBlocked(false));
    }

    public static Coordinates getCoordinates(GOAL_TYPE type, UnitAI ai) {
        Coordinates targetCoordinates = WanderAiMaster.getWanderTargetCoordinatesCell(ai, type);
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
                    WanderAiMaster.changeGroupMoveDirection(group, type);
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
    public ActionSequence getOrders( ) {
        if (ai.getGroupAI() == null)
            return null;
        Coordinates c1 = null;
        Loop loop = new Loop(15);
        while (loop.continues()) {
            if (checkWanderDirectionChange(ai.getGroup(), GOAL_TYPE.WANDER)) {
                changeGroupMoveDirection(ai.getGroup(), GOAL_TYPE.WANDER);
            } else {
                c1 = (getWanderTargetCoordinatesCell(ai, GOAL_TYPE.WANDER));
                if (c1 == null)
                    changeGroupMoveDirection(ai.getGroup(), GOAL_TYPE.WANDER);
                else
                    break;
            }
        }
        if (c1 == null)
            c1 = (CoordinatesMaster.getRandomAdjacentCoordinate(ai.getUnit().getCoordinates()));

        Task task = new Task(ai, GOAL_TYPE.WANDER, null);

        if (c1 != null) {
            List<Action> turnSequence = getMaster(ai).
             getTurnSequenceConstructor().getTurnSequence(ai.getUnit(), c1);
            if (ListMaster.isNotEmpty(turnSequence)) {
                return new ActionSequence(turnSequence, task, ai);
            }
        }

        if (ai.isPathBlocked())
            return null;

        List<Coordinates> c = new ArrayList<>();
        c.add(c1);
        getMaster(ai).setUnit(ai.getUnit());

        Action action = null;
        if (c.get(0) != null)
            action = getMaster(ai).getAtomicAi().getAtomicMove(c.get(0), ai.getUnit());
        else
            action = getMaster(ai).getAtomicAi().getAtomicActionApproach(ai);
        if (action == null)
            return null;
        return new ActionSequence(GOAL_TYPE.WANDER, action);

        //        List<ActionSequence> sequences = getMaster(ai).getActionSequenceConstructor().
        //         getSequencesFromPaths(paths, task, action);
        //        if (sequences.isEmpty())
        //            return null;
        //        return sequences.get(0);
    }

    @Override
    protected Coordinates chooseMoveTarget(List<Coordinates> validCells) {
        return null;
    }

    //ensure that units don't end up in corners too often
    protected Predicate<Coordinates> getWanderPredicate(Unit unit,
                                                      FACING_DIRECTION facing,
                                                      Coordinates c1) {
        return new Predicate<Coordinates>() {
            @Override
            public boolean test(Coordinates coordinates) {
                int wallCount = 0;
                for (Coordinates sub : coordinates.getAdjacentCoordinates()) {
                    if (unit.getGame().getBattleFieldManager().getWallMap().get(sub) != null) {
                        wallCount++;
                    }
                }
                main.system.auxiliary.log.LogMaster.log(1,
                 coordinates + " has " + wallCount + " walls adjacent");

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










