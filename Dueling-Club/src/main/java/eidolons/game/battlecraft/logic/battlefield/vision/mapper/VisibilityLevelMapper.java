package eidolons.game.battlecraft.logic.battlefield.vision.mapper;

import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;

/**
 * Created by JustMe on 3/30/2018.
 */
public class VisibilityLevelMapper extends ObjVisionMapper<VISIBILITY_LEVEL> {

    @Override
    protected VISIBILITY_LEVEL getNullEquivalent() {
        return VISIBILITY_LEVEL.UNSEEN;
    }
    @Override
    protected boolean isClearRequired() {
        return true;
    }
    @Override
    public String toString() {
        return super.toString();
    }
}
