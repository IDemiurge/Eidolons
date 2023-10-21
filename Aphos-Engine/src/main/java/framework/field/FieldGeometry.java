package framework.field;

import elements.content.enums.FieldConsts.CellType;

import java.util.HashMap;
import java.util.Map;

import static elements.content.enums.FieldConsts.*;

/**
 * Created by Alexander on 10/21/2023
 */
public class FieldGeometry {
    public static final Map<Integer, Cell> cellMap = new HashMap<>();
    public static final Integer MIDDLE_Y = 1;

    public static Cell get(CellType type, boolean ally, Boolean top) {
        int x = getX(type);
        int y = getY(type, top);
        if (!ally)
            x = -x;

        return get(x, y).cell();
    }

    //duplicates logic in ENUM - could use enum.get + abs
    private static int getX(CellType type) {
        return switch (type) {
            case Rear -> 3;
            case Back -> 2;
            case Front -> 1;
            case Flank -> Cell.Bottom_Flank_Player.x;
            case Van -> Cell.Vanguard_Bot.x;
            default -> 0;
        };
    }

    private static int getY(CellType type, Boolean top) {
        if (type == CellType.Rear) {
            return Cell.Rear_Enemy.y;
        }
        if (type == CellType.Flank) {
            return top ? Cell.Top_Flank_Enemy.y : Cell.Bottom_Flank_Enemy.y;
        }
        if (type == CellType.Van) {
            return top ? Cell.Vanguard_Top.y : Cell.Vanguard_Bot.y;
        }
        return
                top == null ? 1 : top ? 2 : 0;

    }

    public static XYCell get(int x, int y) {
        return new XYCell(x, y);
    }

    static {
        for (Cell cell : Cell.values()) {
            cellMap.put(getKey(cell.x, cell.y), cell);
        }
    }

    private static Integer getKey(Integer x, Integer y) {
        int key = Math.abs(x) * 10 + y;
        if (x < 0) key = -key;
        return key;
    }

    public static class XYCell {
        int x, y;

        public XYCell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Cell cell() {
            return cellMap.get(getKey(x, y)); //resolve - hash map by key?
        }
    }
}
