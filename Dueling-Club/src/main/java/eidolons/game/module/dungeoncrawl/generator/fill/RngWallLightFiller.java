package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 7/30/2018.
 */
public class RngWallLightFiller extends RngFiller{
    public RngWallLightFiller(WeightMap<ROOM_CELL> weightMap) {
        super(weightMap);
    }

    @Override
    protected ROOM_CELL getFillCellType() {
        return ROOM_CELL.WALL_WITH_LIGHT_OVERLAY;
    }

    protected int getWrapByExitChance(Room room) {
        return 50;
    }

    protected int getWrapPreExitChance(Room room) {
        return 80;
    }
    @Override
    protected boolean isOverlaying() {
        return true;
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
    protected int getFillCornersChance(Room room) {
        switch (room.getType()) {
            case THRONE_ROOM:
            case COMMON_ROOM:
                return 50;
            case TREASURE_ROOM:
            case GUARD_ROOM:
                return 50;
            case ENTRANCE_ROOM:
            case EXIT_ROOM:
                return 80;
        }
        return 0;
    }
}
