package eidolons.game.battlecraft.logic.battlefield.vision.mapper;

import main.content.enums.rules.VisionEnums.PLAYER_VISION;

/**
 * Created by JustMe on 3/30/2018.
 */
public class PlayerVisionMapper extends PlayerMapper<PLAYER_VISION> {

    @Override
    protected boolean isClearRequired() {
        return true;
    }

    @Override
    protected PLAYER_VISION getNullEquivalent() {
        return PLAYER_VISION.INVISIBLE;
    }

    @Override
    public String toString() {
        return "Player Map";
    }
}
