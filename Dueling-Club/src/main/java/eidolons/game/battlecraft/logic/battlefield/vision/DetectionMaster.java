package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.entity.obj.DC_Obj;
import main.content.enums.rules.VisionEnums.PLAYER_VISION;

/**
 * Created by JustMe on 2/22/2017.
 */
public class DetectionMaster {
    private VisionMaster master;

    public DetectionMaster(VisionMaster visionManager) {
        master = visionManager;
    }

    public boolean checkKnown(DC_Obj obj) {
        return obj.getPlayerVisionStatus(true) == PLAYER_VISION.KNOWN || obj.getPlayerVisionStatus(true) == PLAYER_VISION.DETECTED;
    }

    public boolean checkKnownForPlayer(DC_Obj obj) {
        return obj.getPlayerVisionStatus(false) == PLAYER_VISION.KNOWN || obj.getPlayerVisionStatus(false) == PLAYER_VISION.DETECTED;
    }

    public boolean checkDetectedForPlayer(DC_Obj obj) {

        return obj.getPlayerVisionStatus(false) == PLAYER_VISION.DETECTED;
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
        return obj.getPlayerVisionStatus(!enemy) == PLAYER_VISION.DETECTED;
    }

}
