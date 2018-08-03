package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import main.system.auxiliary.data.ListMaster;
import main.system.datatypes.WeightMap;

import java.util.List;

/**
 * Created by JustMe on 7/25/2018.
 */
public class RngContainerFiller extends RngFiller {

    public RngContainerFiller(WeightMap<ROOM_CELL> fillerMap) {
        super(fillerMap);
    }

    @Override
    public float getFillCoef() {
        return 0.3f;
    }

    public float getFillCoef(ROOM_TYPE type) {
        switch (type) {
            case TREASURE_ROOM:
                return 1.5f;
        }
        return 0.5f;
    }

    @Override
    public float getRequiredFillDefault() {
        return 0;
    }

    @Override
    public int getMaxAdjacency(ROOM_CELL filler) {
        return 2;
    }

    @Override
    public boolean isNoAdjacencyLimits() {
        return false;
    }

    @Override
    public List<ROOM_TYPE> getMandatoryTypes() {
        return new ListMaster<ROOM_TYPE>().toList_(
         ROOM_TYPE.COMMON_ROOM,
         ROOM_TYPE.TREASURE_ROOM,
         ROOM_TYPE.SECRET_ROOM);
    }

    @Override
    public boolean isNeverBlock() {
        return true;
    }

    @Override
    public boolean isCornersOnly() {
        return true;
    }

    @Override
    public boolean isFloorOrWallFiller() {
        return true;
    }


}
