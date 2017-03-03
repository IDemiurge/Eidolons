package main.game.ai.tools.prune;

import main.content.enums.entity.UnitEnums.FACING_SINGLE;
import main.game.ai.elements.actions.Action;
import main.game.ai.elements.generic.AiHandler;
import main.game.ai.tools.target.TargetingMaster;
import main.game.battlefield.Coordinates;
import main.game.battlefield.FacingMaster;
import main.system.SortMaster;
import main.system.math.PositionMaster;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by JustMe on 3/3/2017.
 */
public class PruneMaster  extends AiHandler {
    public PruneMaster(AiHandler master) {
        super(master);
    }
    public static List<Coordinates> pruneTargetCells(Action targetAction, List<Coordinates> list) {
        TreeMap<Integer, Coordinates> map = new TreeMap<>(SortMaster
                .getNaturalIntegerComparator(false));

        Coordinates coordinates = targetAction.getSource().getCoordinates();
        for (Coordinates c : list) {
            int distance = 10 * PositionMaster.getDistance(coordinates, c);
            if (!PositionMaster.inLine(c, coordinates)) {
                distance += 5;
            }
            if (PositionMaster.inLineDiagonally(c, coordinates)) {
                distance += 2;
            }
            FACING_SINGLE facing = FacingMaster.getSingleFacing(targetAction.getSource()
                    .getFacing(), c, coordinates);
            switch (facing) {
                case BEHIND:
                    distance += 12;
                    break;
                case IN_FRONT:
                    break;
                case TO_THE_SIDE:
                    distance += 6;
                    break;
            }
            map.put(distance, c);
        }
        // if (distance<minDistance)
        // minDistance=distance;
        // }

        for (int i = map.size() - TargetingMaster.pruneLimit; i > 0; i--) {
            map.remove(map.lastKey());
        }

        // int factor=defaultDistancePruneFactor;
        // while (factor>1)
        // for (Coordinates c :list)
        // {
        // int distance =
        // PositionMaster.getDistance(targetAction.getSource().getCoordinates(),
        // c);
        // if (distance>factor+minDistance)
        // continue;
        // prunedList.add(c);
        // }
        return new LinkedList<>(map.values());
    }
}
