package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 8/11/2018.
 */
public class RngSpecialContainerFiller extends RngFiller {
    public RngSpecialContainerFiller(WeightMap<ROOM_CELL> fillerMap) {
        super(fillerMap);
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
    protected ROOM_CELL getFillCellType() {
        return ROOM_CELL.SPECIAL_CONTAINER;
    }
    @Override
    public boolean isCornersOnly() {
        return false;
    }
    @Override
    protected int getMinFilledCells(ROOM_TYPE roomType) {
        switch (roomType) {
            case COMMON_ROOM:
            case SECRET_ROOM:
            case TREASURE_ROOM:
                return 1;
            case THRONE_ROOM:
                return 5;
            case DEATH_ROOM:
            case EXIT_ROOM:
                return 2;
            case GUARD_ROOM:
            case ENTRANCE_ROOM:
                break;
        }
        return 0 ;
    }
    @Override
    public int getMaxAdjacency(ROOM_CELL filler) {
        return 2;
    }

    @Override
    public float getFillCoef(ROOM_TYPE type) {
        switch (type) {
            case CORRIDOR:
                return 0.5f;
        }
        return 1;
    }

    @Override
    public boolean isAlternativeCenterDistance() {
        return true;
    }

    @Override
    protected int getFillCornersChance(Room room) {
        switch (room.getType()) {
            case TREASURE_ROOM:
                return 50;
            case CORRIDOR:
                return 0;
        }
        return 25;
    }

    @Override
    protected int getWrapPreExitChance(Room room) {
        switch (room.getType()) {
            case TREASURE_ROOM:
            case THRONE_ROOM:
                return 50;
            case CORRIDOR:
                return 15;
        }
        return 25;
    }


    @Override
    public boolean isFloorOrWallFiller() {
        return true;
    }
}
