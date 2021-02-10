package eidolons.game.battlecraft.logic.dungeon.location.struct;

import eidolons.libgdx.bf.decor.CellDecorLayer;
import main.game.bf.Coordinates;

import java.util.function.Function;

public class CellMaster {

    public static  Function<Coordinates, Integer> getFunc(int width, int height, CellDecorLayer.CELL_PATTERN cellPattern) {

        switch (cellPattern) {
            case CHESS:
                /*
                0 1 0
                1 0 1
                0 1 0
                 */
                return c -> {
                    if (c.x % 2 != c.y % 2)
                        return 1;
                    return 0;
                };
            case GRID:
                /*
                0 1 0
                1 1 1
                0 1 0
                 */
                break;
            case CROSS:
                /*
                0 0 1 0 0
                1 1 1 1 1
                0 0 1 0 0
                 */
                break;
            case CROSS_DIAG:
                if (width!=height)
                    return getFunc(width, height, CellDecorLayer.CELL_PATTERN.CROSS);
                /*
                1 0 0 0 1
                0 1 0 1 0
                0 0 1 0 0
                0 1 0 1 0
                1 0 0 0 1
                 */
                break;
            case CENTERPIECE:
                /*
                0 0 0 0 0
                0 1 1 1 0
                0 1 1 1 0
                0 1 1 1 0
                0 0 0 0 0
                 */
                break;
            case OUTER_BORDER:
                //inverse
                return c-> getFunc(width, height, CellDecorLayer.CELL_PATTERN.CENTERPIECE).apply(c) == 1
                        ? 0
                        : 1;
            case SPIRAL:
                /*
                1 1 1 1 1
                1 0 0 0 1
                1 0 1 1 1
                1 0 1 0 1
                1 0 1 1 1
                 */
                break;
            case CONCENTRIC:
                /*
                1 1 1 1 1
                1 0 0 0 1
                1 0 1 0 1
                1 0 0 0 1
                1 1 1 1 1
                 */
                break;
        }
        return null;
    }

}
