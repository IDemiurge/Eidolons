package eidolons.game.exploration.dungeon.generator.fill;

import eidolons.game.exploration.dungeon.generator.GeneratorEnums;
import eidolons.game.exploration.dungeon.generator.LevelData;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import main.system.auxiliary.data.ListMaster;

import java.util.List;

/**
 * Created by JustMe on 7/26/2018.
 */
public interface RngFillerInterface {

    default int getMaxAdjacency(GeneratorEnums.ROOM_CELL filler) {
        return 1;
    }

    boolean isNoAdjacencyLimits();

    default List<ROOM_TYPE> getMandatoryTypes() {
        return new ListMaster<ROOM_TYPE>().toList_(ROOM_TYPE.values());
    }

    boolean isNeverBlock();

    boolean isCornersOnly();

    boolean isFloorOrWallFiller();

    default float getFillCoef(ROOM_TYPE type) {
        return 1;
    }

    default float getFillCoef() {
        return 1;
    }

      float getFillCoef_();

    GeneratorEnums.LEVEL_VALUES getFillCoefConst();

    LevelData getData();

    default float getRequiredFillDefault() {
        return 0.5f * getFillCoef_();
    }

    default float getMinMandatoryFill() {
        return 0.25f * getFillCoef_();
    }

    default float getMaxMandatoryFill() {
        return 0.5f * getFillCoef_();
    }

    default float getMinAdditionalFill() {
        return 0.2f * getFillCoef_();
    }

    default float getMaxDistanceFromEdge() {
        return -1;
    }

    default boolean isAlternativeCenterDistance() {
        return false;
    }

    default float getMaxDistanceFromCenter() {
        return -1;
    }
}
