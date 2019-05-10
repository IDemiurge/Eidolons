package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 8/11/2018.
 */
public class RngDestructibleFiller extends RngFiller {
    public RngDestructibleFiller(WeightMap<ROOM_CELL> fillerMap) {
        super(fillerMap);
    }

    @Override
    protected ROOM_CELL getFillCellType() {
        return ROOM_CELL.DESTRUCTIBLE;
    }
    @Override
    public int getMaxAdjacency(ROOM_CELL filler) {
        return 2;
    }

    @Override
    public float getFillCoef() {
        return 0.7f;
    }

    @Override
    public boolean isNoAdjacencyLimits() {
        return true;
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
        return true;
    }

}