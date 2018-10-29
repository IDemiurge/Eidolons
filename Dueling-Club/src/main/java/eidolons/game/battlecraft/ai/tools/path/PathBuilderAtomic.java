package eidolons.game.battlecraft.ai.tools.path;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.elements.generic.AiHandler;
import eidolons.game.battlecraft.ai.elements.generic.AiMaster;
import main.game.bf.Coordinates;
import main.system.SortMaster;
import main.system.auxiliary.Loop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 10/27/2018.
 */
public class PathBuilderAtomic extends AiHandler {
    public PathBuilderAtomic(AiMaster master) {
        super(master);
    }


    public List<Coordinates> getPathChain(Unit unit, Coordinates coordinates, Coordinates cell,
                                          Boolean diagonals, int tries) {
        List<List<Coordinates>> lists = new ArrayList<>();
        Loop loop = new Loop(tries);
        while (loop.continues()) {
            //        Coordinates last=unit.getCoordinates();
            List<Coordinates> list = new ArrayList<>();
            coordinates = unit.getCoordinates();
            steps:
            while (true) { //randomized 'cause it's a hashset
                for (Coordinates c1 :  (coordinates.getAdjacentCoordinates(diagonals))) {
                    if (check(coordinates, c1, cell, unit)) {
                        //                    last = coordinates;
                        coordinates = c1;
                        list.add(coordinates);
                        if (coordinates.equals(cell))
                            return list;
                        continue steps;
                    }
                }
                break;
            }
            lists.add(list);
        }
        return lists.stream().sorted(new SortMaster<List>()
         .getSorterByExpression_(l -> l.size())).findFirst().orElse(null);
    }

    private boolean check(Coordinates last, Coordinates c1, Coordinates cell, Unit unit) {
        if (last.dst_(cell) < c1.dst_(cell)) {
            return false;
        }
        if (!getGame().getMovementManager().canMove(unit, c1)) {
            return false;
        }
        if (c1.equals(cell))
            return true;
        //girth check?
        return true;
    }
}
