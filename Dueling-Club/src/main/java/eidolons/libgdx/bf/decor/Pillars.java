package eidolons.libgdx.bf.decor;

import eidolons.game.module.generator.model.AbstractCoordinates;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.gui.generic.GroupX;
import main.data.XLinkedMap;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;

import java.util.Map;

/**
 * z-order
 * <p>
 * blending with cells, objects and other overlays
 */
public class Pillars extends GroupX {

    DIRECTION horDirection = DIRECTION.RIGHT;
    DIRECTION vertDirection = DIRECTION.DOWN;
    boolean vertPreferred = true;
    boolean allowShardsOnTop = true;
    GridPanel grid;

    private Map<Coordinates, Pillar> map = new XLinkedMap<>();
    private Map<Coordinates, Pillar> lightMap = new XLinkedMap<>();

    public Pillars(GridPanel gridPanel) {
        this.grid = gridPanel;
        init();
    }

    private void init() {
        for (int x = -1; x - 1 < grid.getCols(); x++) {
            for (int y = -1; y - 1 < grid.getRows(); y++) {

                if (x >= 0 && y >= 0)
                    if (x < grid.getCells().length &&
                            y < grid.getCells()[0].length)
                        if (grid.getCells()[x][y] != null)
                            continue;
                Object direction = null;
                PILLAR_TYPE type = selectType(x, y);
//                new Pillar(type, version);
//
//                Integer degrees = getDirectionForShards(x, y);
                AbstractCoordinates c = new AbstractCoordinates(x, y);
            }
        }
    }

    private PILLAR_TYPE selectType(int x, int y) {



        return null;
    }


    public enum PILLAR_TYPE {
        HOR_SMALL,
        HOR_SINGLE,
        HOR_LINE,

        VERT_LONG,
        VERT_SHORT,
    }

    public enum PILLAR_VERSION {
        DARK,
        ROUGH,
        DESTROYED,
        DEFAULT,
    }

    public static class PillarSide extends GroupX {
        SpriteX lighting;
        SpriteX surface;

    }

    public class Pillar extends GroupX {
        Map<DIRECTION, PillarSide> sides;

    }


}
