package eidolons.libgdx.bf.overlays;

import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;

/**
 * Created by JustMe on 5/18/2018.
 */
public class HpBarManager {
    public static boolean isHpBarVisible(BattleFieldObject obj) {
        if (!obj.isFull((PARAMS.TOUGHNESS)))
            return true;
        return !obj.isFull((PARAMS.ENDURANCE));
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
