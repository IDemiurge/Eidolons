package eidolons.libgdx.bf.decor;

import eidolons.libgdx.gui.generic.GroupX;
import main.game.bf.directions.DIRECTION;

public class Pillars extends GroupX {

    DIRECTION horDirection = DIRECTION.RIGHT;
    DIRECTION vertDirection = DIRECTION.DOWN;
    boolean vertPreferred = true;
    boolean allowShardsOnTop = true;

    /**
     * z-order
     *
     * blending with cells, objects and other overlays
     *
     */

    public enum PILLAR_OVERLAY{

    }
    public enum PILLAR_TYPE{
        HOR_SMALL,
        HOR_SINGLE,
        HOR_LINE    ,


    }
        public enum PILLAR_VERSION{

    }
}
