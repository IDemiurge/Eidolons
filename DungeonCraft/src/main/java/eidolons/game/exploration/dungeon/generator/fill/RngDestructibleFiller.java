package eidolons.game.exploration.dungeon.generator.fill;

import eidolons.game.exploration.dungeon.generator.GeneratorEnums;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 8/11/2018.
 */
public class RngDestructibleFiller extends RngFiller {
    public RngDestructibleFiller(WeightMap<GeneratorEnums.ROOM_CELL> fillerMap) {
        super(fillerMap);
    }

    @Override
    protected GeneratorEnums.ROOM_CELL getFillCellType() {
        return GeneratorEnums.ROOM_CELL.DESTRUCTIBLE;
    }
    @Override
    public int getMaxAdjacency(GeneratorEnums.ROOM_CELL filler) {
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
