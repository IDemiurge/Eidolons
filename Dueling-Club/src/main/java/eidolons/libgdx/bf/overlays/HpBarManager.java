package eidolons.libgdx.bf.overlays;

import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.game.battlecraft.logic.battlefield.vision.VisionManager;

/**
 * Created by JustMe on 5/18/2018.
 */
public class HpBarManager {
    public static boolean isHpBarVisible(BattleFieldObject obj) {
        if (VisionManager.isCinematicVision()){
            return false;
        }
        if (obj instanceof Structure)
        {
            if (!obj.isFull((PARAMS.TOUGHNESS)))
            return true;
        return !obj.isFull((PARAMS.ENDURANCE));
        }
        return true;
    }

    public static boolean canHpBarBeVisible(BattleFieldObject obj) {
        if (obj.isDead())
            return false;
        if (!obj.isMine())
            if (!obj.isDetectedByPlayer())
                return false;
        return true;
    }

}
