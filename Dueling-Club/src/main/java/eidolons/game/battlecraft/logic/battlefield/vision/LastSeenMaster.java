package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.entity.obj.BattleFieldObject;
import main.content.enums.rules.VisionEnums.UNIT_TO_UNIT_VISION;

/**
 * Created by JustMe on 4/1/2018.
 */
public class LastSeenMaster {
    public static boolean isUpdateRequired(BattleFieldObject object) {
        if (object.isDetectedByPlayer())
           if (object.getUnitVisionStatus()== UNIT_TO_UNIT_VISION.IN_SIGHT
            ||object.getUnitVisionStatus()== UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT)
            return true;
        return false;
    }
}
