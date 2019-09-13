package eidolons.game.battlecraft.logic.battlefield.vision.mapper;

import eidolons.entity.obj.DC_Obj;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;

/**
 * Created by JustMe on 3/30/2018.
 */
public class PlayerVisionMapper extends PlayerMapper<PLAYER_VISION> {
    @Override
    public void set(DC_Player source, DC_Obj object, PLAYER_VISION player_vision) {
        if (object.isVisibilityFrozen()) {
            return;
        }
        super.set(source, object, player_vision);
    }

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
