package eidolons.game.exploration.dungeons.generator.fill;

import eidolons.game.exploration.dungeons.generator.GeneratorEnums;
import eidolons.game.exploration.dungeons.generator.model.Room;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 8/11/2018.
 */
public class RngWallDecorFiller extends RngFiller {
    public RngWallDecorFiller(WeightMap<GeneratorEnums.ROOM_CELL> fillerMap) {
        super(fillerMap);
    }

    @Override
    public float getFillCoef() {
        return 0.3f;
    }

    @Override
    protected GeneratorEnums.ROOM_CELL getFillCellType() {
        return GeneratorEnums.ROOM_CELL.WALL_WITH_DECOR_OVERLAY;
    }
    @Override
    public int getMaxAdjacency(GeneratorEnums.ROOM_CELL filler) {
        return 1;
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

    @Override
    protected int getWrapByExitChance(Room room) {
        switch (room.getType()) {
            case THRONE_ROOM:
                return 80;
            case COMMON_ROOM:
                return 30;
            case ENTRANCE_ROOM:
            case EXIT_ROOM:
                return 40;
        }
        return 0;
    }

    @Override
    protected int getFillCornersChance(Room room) {
        switch (room.getType()) {
            case THRONE_ROOM:
                return 80;
            case COMMON_ROOM:
                return 30;
            case ENTRANCE_ROOM:
            case EXIT_ROOM:
                return 40;
        }
        return 0;
    }
}
