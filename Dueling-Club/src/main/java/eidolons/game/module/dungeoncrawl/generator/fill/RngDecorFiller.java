package eidolons.game.module.dungeoncrawl.generator.fill;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import main.system.datatypes.WeightMap;

/**
 * Created by JustMe on 7/25/2018.
 */
public class RngDecorFiller extends RngFiller {

    public RngDecorFiller(WeightMap<ROOM_CELL> fillerMap) {
        super( fillerMap);
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



}
