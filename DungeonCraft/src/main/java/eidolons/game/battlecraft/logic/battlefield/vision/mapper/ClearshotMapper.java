package eidolons.game.battlecraft.logic.battlefield.vision.mapper;

import eidolons.game.battlecraft.logic.battlefield.DC_MovementManager;

/**
 * Created by JustMe on 4/1/2018.
 */
public class ClearshotMapper extends ObjVisionMapper<Boolean> {

    private final boolean light;

    public ClearshotMapper(boolean light) {
        this.light = light;
    }

    @Override
    protected boolean isClearRequired() {
        if (light) {
            // DC_StateManager.fullResetRequired;
            return false;
        }
        return DC_MovementManager.anObjectMoved;
    }
}
