package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_VALUES;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 7/30/2018.
 */
public class RngLightFiller extends RngFiller {
    public RngLightFiller(WeightMap<ROOM_CELL> fillerMap) {
        super(fillerMap);
    }

    @Override
    public LEVEL_VALUES getFillCoefConst() {
        return LEVEL_VALUES.FILL_LIGHT_COEF;
    }

    @Override
    public float getFillCoef() {
        return 0.5f;
    }

    @Override
    public boolean isNoAdjacencyLimits() {
        return false;
    }

    @Override
    public boolean isNeverBlock() {
        return true;
    }

    @Override
    public boolean isCornersOnly() {
        return true;
    }

    @Override
    public boolean isFloorOrWallFiller() {
        return true;
    }



//    @Override
//    public boolean isAlternativeCenterDistance() {
//        return true;
//    }
//
//    @Override
//    public float getMaxDistanceFromCenter() {
//        return 2;
//    }
//
//    @Override
//    public float getMaxDistanceFromEdge() {
//        return 3;
//    }
}
