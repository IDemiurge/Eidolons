package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 8/11/2018.
 */
public class RngWallDecorFiller extends RngFiller {
    public RngWallDecorFiller(WeightMap<ROOM_CELL> fillerMap) {
        super(fillerMap);
    }

    @Override
    public float getFillCoef() {
        return 0.3f;
    }

    @Override
    protected ROOM_CELL getFillCellType() {
        return ROOM_CELL.WALL_WITH_DECOR_OVERLAY;
    }
    @Override
    public int getMaxAdjacency(ROOM_CELL filler) {
        return 0;
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
    public float getMaxDistanceFromEdge() {
        return 0;
    }

    @Override
    public boolean isAlternativeCenterDistance() {
        return false;
    }

    @Override
    public float getMaxDistanceFromCenter() {
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
