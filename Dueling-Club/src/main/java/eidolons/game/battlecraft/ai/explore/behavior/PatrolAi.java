package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.UnitAI.AI_BEHAVIOR_MODE;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.system.math.DC_PositionMaster;
import main.entity.obj.MicroObj;
import main.game.bf.Coordinates;
import main.system.math.MathMaster;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 10/21/2018.
 */
public class PatrolAi extends CyclicGroupBehavior {

    public static final int MAX_RALLY_POINTS = 4;
    public static final int MIN_RALLY_POINTS = 2;
    int failedRallies = 0;
    private final Coordinates[] presetPoints;

    public PatrolAi(AiMaster master, UnitAI ai, Coordinates... points) {
        super(master, ai);
        this.presetPoints = points;
    }

    @Override
    protected boolean failed() {
        failedRallies++;
        return super.failed();
    }

    @Override

    protected DC_Obj createCycledArg(int i, DC_Obj[] cycledArgs) {
        if (presetPoints.length > i) {
            return master.getGame().getCellByCoordinate(presetPoints[i]);
        }
        if (i == getCycledStepsNumber() - 1) {
            //return back
            Coordinates c = origin;
            if (!isTargetValid(c, getCoordinates()))
                c = DC_PositionMaster.getRandomValidAdjacent(origin, ai.getUnit());
            return getCell(c);
        }
        //preferred length?!
        //does path-building even work? it must

        //min max dst from last cycled arg?!
        Coordinates last = (i == 0) ? origin :
         cycledArgs[i - 1].getCoordinates();

        List<Coordinates> valid = block.getCoordinatesSet().stream().filter(c ->
         last.dst_(c) < getMaxDistance() &&
          last.dst_(c) > getMinDistance() &&
          isTargetValid(c, last)
        ).collect(Collectors.toList());
        if (valid.isEmpty()) {
            valid = block.getCoordinatesSet().stream().filter(c ->
             isTargetValid(c, last)
            ).collect(Collectors.toList());
        }
        Collections.shuffle(valid);
        for (Coordinates coordinates : valid) {
            if (getUnit().getGame().getRules().getStackingRule().
             canBeMovedOnto(getUnit(), coordinates)) {
                return getCell(coordinates);
            }
        }
        return null;

    }

    @Override
    protected float getTimeBeforeFail() {
        if (isLeader()) {
            return 225;
        }
        return 85;
    }

    @Override
    protected float getDefaultSpeed() {
        return isTestMode() ? 12f : 2;
    }


    @Override
    protected boolean isAtomicAllowed(Coordinates cell) {
        if (isLeader())
            return false;
        if (!cell.isAdjacent(ai.getUnit().getCoordinates()))
            return false;
        return super.isAtomicAllowed(cell);
    }

    @Override
    protected double getDistanceForNearby() {
        if (isLeader())
            return 1;
        return 2;
    }

    private double getMinDistance() {
        return 3;
    }

    private double getMaxDistance() {
        return 5;
    }

    @Override
    protected int getCycledStepsNumber() {
        return MathMaster.getMinMax(MAX_RALLY_POINTS - failedRallies, MIN_RALLY_POINTS, MAX_RALLY_POINTS);
    }

    public String getDebugInfo() {
        return ((int) sinceLastAction) + " "
         + (ai.isLeader() ? cycledArgs == null ? "No cycled args" :
         Arrays.deepToString(Arrays.stream(cycledArgs).map(MicroObj::getCoordinates).toArray())
         : ((queuedAction == null) ? "null" : queuedAction.getActive().getName()))
         + "\n" + ((target == null) ? "target =null" : "target " + target.getCoordinates())
         + "\n" + ((orders == null) ? "orders =null" : orders.getActionSequence())
         //         + "\n" + "origin " + origin + ", in " + origin.dst(getCoordinates())
         + "\n" + (ai.isLeader() ? "LEADER, step " + step : ai.getGroupAI().getLeader().getName());
    }

    @Override
    protected boolean isFollowOrAvoid() {
        return true;
    }

    @Override
    public AI_BEHAVIOR_MODE getType() {
        return AI_BEHAVIOR_MODE.PATROL;
    }

    @Override
    protected Coordinates chooseMoveTarget(List<Coordinates> validCells) {
        return CoordinatesMaster.getClosestTo(preferredPosition, validCells);
    }
}
