package framework.field;

import elements.exec.targeting.area.CellSets;

import java.util.Set;

import static combat.sub.BattleManager.combat;
import static elements.content.enums.FieldConsts.*;

/**
 * Created by Alexander on 10/20/2023
 */
public class FieldAnalyzer {
    public static boolean isFirstRowFree(boolean ally) {
        return checkFree(CellSets.frontSet(ally));
    }

    private static boolean checkFree(Cell... cells) {
        return checkFree(CellSets.toSet(cells));
    }

    private static boolean checkFree(Set<Cell> set) {
        for (FieldPos pos : Transformer.toPos(set)) {
            if (combat().getField().getByPos(pos) != null)
                return false;
        }
        return true;
    }

    public static boolean isDuoClosestToFlankFree(boolean ally, Boolean top) {
        //TODO
        return checkFree(getClosestToFlank(ally, top));
    }

    public static Set<Cell> getClosestToFlank(boolean ally, Boolean top) {
        Cell c1 = FieldGeometry.get(CellType.Back, ally, top);
        Cell c2 = FieldGeometry.get(CellType.Front, ally, top);

        return Set.of(c1, c2);
    }

    public static Cell getFarthest(boolean front, Integer srcY, boolean ally) {
        return FieldGeometry.get(front ? CellType.Front : CellType.Back, ally, srcY < 1); //get top for src bottom and vice versa
    }

    public static boolean isAllButFarthestFree(boolean front, Integer y, boolean ally) {
        Cell cell1= mid(front, ally);
        Cell cell2 = getFarthest(front, 1-y, ally);
        return checkFree(cell1, cell2);
    }

    private static Cell mid(boolean front, boolean ally) {
        if (ally){
            return front ? Cell.Front_Player_2 :Cell.Back_Player_2;
        } else {
            return front ? Cell.Front_Enemy_2 :Cell.Back_Enemy_2;
        }
    }
}














