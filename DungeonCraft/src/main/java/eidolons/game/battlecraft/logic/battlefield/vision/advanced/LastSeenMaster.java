package eidolons.game.battlecraft.logic.battlefield.vision.advanced;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import libgdx.bf.grid.cell.UnitGridView;
import main.content.enums.rules.VisionEnums.UNIT_VISION;

/**
 * Created by JustMe on 4/1/2018.
 */
public class LastSeenMaster {
    public static boolean isUpdateRequired(BattleFieldObject object) {
        if (object.isDetectedByPlayer())
            return object.getUnitVisionStatus() == UNIT_VISION.IN_SIGHT
                    || object.getUnitVisionStatus() == UNIT_VISION.IN_PLAIN_SIGHT;
        return false;
    }

    public static void resetLastSeen(UnitGridView view,
                                     BattleFieldObject obj, boolean visible) {
        if (obj instanceof Unit){
            obj.isPlayerCharacter();
        }
        if (obj.isDead() || obj.isHidden())
            view.getLastSeenView().fadeOut();
        else
        if (visible  )
        {
            view.getLastSeenView().remove();
            view.getParent().addActor(view.getLastSeenView());
            view.getLastSeenView().fadeIn();
        }
        else
        {
            view.getLastSeenView().fadeOut();
        }

        Float time = obj.getGame().getLoop().getTime();
        obj.setLastSeenTime(time);
        obj.setLastSeenFacing(obj.getFacing());
        obj.setLastSeenOutline(obj.getOutlineTypeForPlayer());
//        main.system.auxiliary.log.LogMaster.log(1, obj.getOutlineTypeForPlayer()+ "LSV RESET FOR " + obj +
//         time);
    }
}
