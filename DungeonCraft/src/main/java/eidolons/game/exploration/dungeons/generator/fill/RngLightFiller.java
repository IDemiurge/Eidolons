package eidolons.game.exploration.dungeons.generator.fill;

import eidolons.game.exploration.dungeons.generator.GeneratorEnums;
import eidolons.game.exploration.dungeons.generator.model.Room;
import eidolons.game.exploration.dungeons.struct.LevelBlock;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 7/30/2018.
 */
public class RngLightFiller extends RngFiller {
    public RngLightFiller(WeightMap<GeneratorEnums.ROOM_CELL> fillerMap) {
        super(fillerMap);
    }

    protected int getJustDontChance(LevelBlock block, GeneratorEnums.ROOM_CELL filler) {
        return 53;
    }
    protected int getWrapByExitChance(Room room) {
        return 0;
    }

    protected int getWrapPreExitChance(Room room) {
        return 65;
    }
    @Override
    public GeneratorEnums.LEVEL_VALUES getFillCoefConst() {
        return GeneratorEnums.LEVEL_VALUES.FILL_LIGHT_EMITTER_COEF;
    }

    @Override
    protected GeneratorEnums.ROOM_CELL getFillCellType() {
        return GeneratorEnums.ROOM_CELL.LIGHT_EMITTER;
    }
    @Override
    public float getFillCoef() {
        return 0.1f;
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
    public int getMaxAdjacency(GeneratorEnums.ROOM_CELL filler) {
        return 1;
    }

    @Override
    public boolean isCornersOnly() {
        return false;
    }

    @Override
    public boolean isFloorOrWallFiller() {
        return true;
    }

    @Override
    protected int getFillCornersChance(Room room) {
        switch (room.getType()) {
            case THRONE_ROOM:
            case COMMON_ROOM:
                return 40;
            case TREASURE_ROOM:
            case GUARD_ROOM:
                return 30;
            case ENTRANCE_ROOM:
            case EXIT_ROOM:
                return 60;
        }
        return 0;
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
