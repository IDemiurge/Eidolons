package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import main.system.auxiliary.data.ListMaster;
import main.system.datatypes.WeightMap;

import java.util.List;

/**
 * Created by JustMe on 7/25/2018.
 */
public class RngContainerFiller extends RngFiller{
    public RngContainerFiller(LevelModel model, WeightMap<ROOM_CELL> fillerMap) {
        super(model, fillerMap);
    }

    @Override
    protected float getRequiredFillDefault() {
        return 0;
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
    public List<ROOM_TYPE> getMandatoryTypes() {
        return new ListMaster<ROOM_TYPE>().toList_(ROOM_TYPE.TREASURE_ROOM,
         ROOM_TYPE.SECRET_ROOM);
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
    public float getFillCoef(ROOM_TYPE type) {
        return 0;
    }

    @Override
    public float getMinMandatoryFill() {
        return 0;
    }

    @Override
    public float getMaxMandatoryFill() {
        return 0;
    }

    @Override
    public float getMinAdditionalFill() {
        return 0;
    }
}
