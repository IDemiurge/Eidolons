package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 7/30/2018.
 */
public class RngWallLightFiller extends RngFiller{
    public RngWallLightFiller(WeightMap<ROOM_CELL> weightMap) {
        super(weightMap);
    }

    @Override
    protected boolean isOverlaying() {
        return true;
    }

    @Override
    public float getFillCoef() {
        return 0.25f;
    }

    @Override
    public boolean isNoAdjacencyLimits() {
        return false;
    }

    @Override
    public boolean isNeverBlock() {
        return false;
    }

    @Override
    public boolean isCornersOnly() {
        return false;
    }

    @Override
    public boolean isFloorOrWallFiller() {
        return false;
    }
}
