package libgdx.anims.main;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.active.DC_QuickItemAction;
import libgdx.anims.Anim;
import libgdx.anims.AnimEnums;
import libgdx.anims.AnimEnums.ANIM_PART;
import libgdx.anims.anim3d.Potion3dAnim;
import libgdx.anims.std.DeathAnim;
import libgdx.anims.std.DoorAnimation;
import libgdx.anims.std.QuickItemAnim;
import libgdx.anims.std.sprite.LockKeyAnimation;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;

/**
 * Created by JustMe on 2/3/2017.
 */
public class EventAnimCreator {

    public static boolean isEventAnimated(Event event) {
        if (event.getType() instanceof STANDARD_EVENT_TYPE) {
            switch (((STANDARD_EVENT_TYPE) event.getType())) {
                case UNIT_HAS_BEEN_KILLED:
                case UNIT_HAS_USED_QUICK_ITEM:
                case DOOR_CLOSES:
                case DOOR_OPENS:
                case DOOR_IS_UNLOCKED:
                    return true;
            }
        }
        return false;
    }

    public static Anim getAnim(Event e) {


        if (e.getType() instanceof STANDARD_EVENT_TYPE) {
            switch (((STANDARD_EVENT_TYPE) e.getType())) {
                case UNIT_HAS_BEEN_KILLED:
                    return new DeathAnim(e);
                case UNIT_HAS_USED_QUICK_ITEM:
                    try {
                        if (((DC_QuickItemAction) e.getRef().getActive()).getItem().isPotion())
                            return new Potion3dAnim((DC_ActiveObj) e.getRef().getActive());
                    } catch (Exception ex) {
                        main.system.ExceptionMaster.printStackTrace(ex);
                    }
                    return new QuickItemAnim(e);
                case DOOR_CLOSES:
                case DOOR_OPENS:
                    return new DoorAnimation(e);

                case DOOR_IS_UNLOCKED:
                    return new LockKeyAnimation(e);
            }

        }
//            if (part == ANIM_PART.AFTEREFFECT)
//                if (lethal)
//
        return null;
    }

    public static float getEventAnimDelay(Event event, Anim anim, ANIM_PART partToAddAt) {
        return 0;
    }

    public static ANIM_PART getPartToAttachTo(Event event) {
        switch (((STANDARD_EVENT_TYPE) event.getType())) {
            case UNIT_HAS_BEEN_KILLED:
                return AnimEnums.ANIM_PART.AFTEREFFECT;
        }
        return AnimEnums.ANIM_PART.IMPACT;
    }

}
