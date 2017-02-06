package main.libgdx.anims.std;

import main.game.event.Event;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.libgdx.anims.Anim;
import main.libgdx.anims.AnimationConstructor.ANIM_PART;

/**
 * Created by JustMe on 2/3/2017.
 */
public class EventAnimCreator {
    public static Anim getAnim(Event e){


if (e.getType() instanceof STANDARD_EVENT_TYPE){
    switch (((STANDARD_EVENT_TYPE) e.getType())){
        case UNIT_HAS_BEEN_KILLED:
            return new DeathAnim(e);
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
        switch (((STANDARD_EVENT_TYPE) event.getType())){
            case UNIT_HAS_BEEN_KILLED:
                return ANIM_PART.AFTEREFFECT;
        }
        return ANIM_PART.IMPACT;
    }
}
