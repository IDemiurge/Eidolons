package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 7/25/2018.
 */
public class RngDecorFiller extends RngFiller {

    public RngDecorFiller(WeightMap<ROOM_CELL> fillerMap) {
        super( fillerMap);
    }

    @Override
    protected ROOM_CELL getFillCellType() {
        return ROOM_CELL.DESTRUCTIBLE;
    }
    @Override
    public float getFillCoef() {
        return 0.6f;
    }

    @Override
    public int getMaxAdjacency(ROOM_CELL filler) {
        return 2;
    }

    @Override
    public float getMaxDistanceFromEdge() {
        return 2.5f;
    }

    @Override
    public boolean isAlternativeCenterDistance() {
        return true;
    }

    @Override
    public float getMaxDistanceFromCenter() {
        return 1.5f;
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
        return false;
    }

    @Override
    public boolean isFloorOrWallFiller() {
        return true;
    }

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
