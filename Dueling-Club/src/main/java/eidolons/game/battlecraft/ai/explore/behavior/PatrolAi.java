package eidolons.game.battlecraft.ai.explore.behavior;

import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.ai.UnitAI;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.game.bf.Coordinates;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by JustMe on 10/21/2018.
 */
public class PatrolAi extends CyclicBehavior {

    private Coordinates[] presetPoints;

    public PatrolAi(AiMaster master, UnitAI ai, Coordinates... points) {
        super(master, ai);
        this.presetPoints = points;
    }

    @Override
    protected DC_Obj createCycledArg(int i, DC_Obj[] cycledArgs) {
        if (presetPoints != null) {
            return master.getGame().getCellByCoordinate(presetPoints[i]);
        }
        Coordinates c = ai.getUnit().getCoordinates();
        //        distance = getDistanceForNearby(); max distance

        List<Coordinates> corners = CoordinatesMaster.getCornerCoordinates(block.getCoordinatesList());
        Coordinates corner = corners.get(i);
        //check not blocked

        if (master.getGame().getObjectsAt(corner).isEmpty())
            return master.getGame().getCellByCoordinate(corner);
        for (Coordinates coordinates : new HashSet<>(Arrays.asList(corner.getAdjacent()))) {
            if (master.getGame().getObjectsAt(coordinates).isEmpty())
                if (master.getGame().getVisionMaster().getSightMaster().getClearShotCondition().
                 check(coordinates, corner)) {
                    corner = coordinates;
                }

        }

        return master.getGame().getCellByCoordinate(corner);
    }

    @Override
    protected int getCycledStepsNumber() {
        return 4;
    }

    @Override
    protected boolean isFollowOrAvoid() {
        return true;
    }
}
