package eidolons.game.netherflame.main.death;

import eidolons.entity.obj.BattleFieldObject;
import main.content.enums.rules.VisionEnums;

public class ShadowVisionMaster {
    public static VisionEnums.VISIBILITY_LEVEL getVisibility(VisionEnums.UNIT_VISION sight, BattleFieldObject object) {
        double dst = ShadowMaster.getShadowUnit().getCoordinates().dst_(object.getCoordinates());

        //TODO what if we instead made ALL appear as OUTLINES?


        switch (sight) {
            case CONCEALED:
            case IN_PLAIN_SIGHT:
            case IN_SIGHT:
                return VisionEnums.VISIBILITY_LEVEL.CLEAR_SIGHT;
            case BEYOND_SIGHT:
                if ((dst<11)) {
                    return VisionEnums.VISIBILITY_LEVEL.CLEAR_SIGHT;
                }
            case BLOCKED:
                if ((dst<7)) {
                    return VisionEnums.VISIBILITY_LEVEL.CLEAR_SIGHT;
                }

        }
        return VisionEnums.VISIBILITY_LEVEL.UNSEEN;
    }
}
