package eidolons.game.battlecraft.logic.battlefield.vision.mapper;

import main.content.enums.rules.VisionEnums.UNIT_VISION;

/**
 * Created by JustMe on 3/30/2018.
 */
public class UnitVisionMapper extends ObjVisionMapper<UNIT_VISION> {

    @Override
    protected UNIT_VISION getNullEquivalent() {
        return UNIT_VISION.BEYOND_SIGHT;
    }

    @Override
    protected boolean isClearRequired() {
        return false;
    }

    @Override
    public String toString() {
        return "Unit Vision Map";
    }
}
