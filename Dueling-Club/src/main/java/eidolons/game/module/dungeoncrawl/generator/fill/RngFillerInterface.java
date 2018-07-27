package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;

import java.util.List;

/**
 * Created by JustMe on 7/26/2018.
 */
public interface RngFillerInterface {

    int getMaxAdjacency(ROOM_CELL filler);

    boolean isNoAdjacencyLimits();

    List<ROOM_TYPE> getMandatoryTypes();

    boolean isNeverBlock();

    boolean isCornersOnly();

    boolean isFloorOrWallFiller();

    float getFillCoef(ROOM_TYPE type);

    float getMinMandatoryFill();

    float getMaxMandatoryFill();

      float getMinAdditionalFill();
}
