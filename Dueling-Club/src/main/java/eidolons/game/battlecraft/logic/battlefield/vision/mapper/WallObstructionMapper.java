package eidolons.game.battlecraft.logic.battlefield.vision.mapper;

import eidolons.entity.obj.Structure;

/**
 * Created by JustMe on 4/3/2018.
 */
public class WallObstructionMapper extends CoordinateMapper<Boolean> {
    public void wallDestroyed(Structure structure) {
        map.clear(); //TODO can we do it better?
    }

    @Override
    protected boolean isClearRequired() {
        return super.isClearRequired();
    }
}
