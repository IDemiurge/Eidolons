package main.game.battlecraft.logic.battlefield.vision;

import main.content.enums.rules.VisionEnums;
import main.content.enums.rules.VisionEnums.UNIT_TO_PLAYER_VISION;
import main.entity.obj.DC_Obj;

/**
 * Created by JustMe on 2/22/2017.
 */
public class DetectionMaster {
    private VisionMaster master;

    public DetectionMaster(VisionMaster visionManager) {
        master = visionManager;
    }

    public boolean checkKnown(DC_Obj obj) {
        return obj.getPlayerVisionStatus(true) == UNIT_TO_PLAYER_VISION.KNOWN || obj.getPlayerVisionStatus(true) == UNIT_TO_PLAYER_VISION.DETECTED;
    }

    public boolean checkKnownForPlayer(DC_Obj obj) {
        return obj.getPlayerVisionStatus(false) == UNIT_TO_PLAYER_VISION.KNOWN || obj.getPlayerVisionStatus(false) == UNIT_TO_PLAYER_VISION.DETECTED;
    }

    public boolean checkDetectedForPlayer(DC_Obj obj) {

        return obj.getPlayerVisionStatus(false) == VisionEnums.UNIT_TO_PLAYER_VISION.DETECTED;
    }

    public boolean checkDetectedEnemy(DC_Obj obj) {
        return checkDetected(obj, true);
    }

    public boolean checkDetected(DC_Obj obj) {
        return checkDetected(obj, false);
    }

    public boolean checkDetected(DC_Obj obj, boolean enemy) {
        if (obj == null) {
            return false;
        }
        if (!enemy || obj.getOwner().isMe()) {
            if (master.getActiveUnit() != null) {
                if (obj.getOwner() == master.getActiveUnit().getOwner()) {
                    return true;
                }
            }
        }
        return obj.getPlayerVisionStatus(!enemy) == VisionEnums.UNIT_TO_PLAYER_VISION.DETECTED;
    }

}
