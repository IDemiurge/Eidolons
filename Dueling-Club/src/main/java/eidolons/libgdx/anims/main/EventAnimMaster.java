package eidolons.libgdx.anims.main;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.anims.Anim;
import eidolons.libgdx.anims.CompositeAnim;
import eidolons.libgdx.anims.construct.AnimConstructor.ANIM_PART;
import eidolons.libgdx.anims.std.EventAnimCreator;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import main.game.logic.event.Event;
import main.system.auxiliary.log.LogMaster;

/**
 * Created by JustMe on 11/15/2018.
 */
public class EventAnimMaster {
    AnimMaster master;

    public EventAnimMaster(AnimMaster master) {
        this.master = master;
    }

    protected void initEventAnimation(Event event) {
        if (event.getRef().isDebug()) {
            return;
        }
//        if (event.getType() == STANDARD_EVENT_TYPE.NEW_ROUND) {
//            if (showBuffAnimsOnNewRoundLength != null) {
//                continuousAnims.values().forEach(anim -> {
//                    if (anim.getBuff().isVisible()) {
//                        anim.reset();
//                        anim.start();
//                        anim.setDuration(showBuffAnimsOnNewRoundLength);
//                    }
//                });
//            }
//        }
        CompositeAnim parentAnim = null;
        if (FloatingTextMaster.isEventDisplayable(event)) {
            parentAnim = AnimMaster.getParentAnim(event.getRef());
            if (parentAnim != null) {
                parentAnim.addTextEvent(event);
            }
        }
        Anim anim = EventAnimCreator.getAnim(event);
        if (anim == null) {
            return;
        }
        parentAnim = getEventAttachAnim(event, anim);
        if (parentAnim != null) {

            LogMaster.log(LogMaster.ANIM_DEBUG, anim +
             " event anim created for: " + parentAnim);

            if (parentAnim.getMap().isEmpty())
                parentAnim.add(ANIM_PART.AFTEREFFECT, anim);
            else
                parentAnim.addEventAnim(anim, event); //TODO}
        }
        if (parentAnim.getMap().isEmpty()) {

        }
        if (parentAnim !=master.getDrawer().getLeadAnimation())
            if (!parentAnim.isFinished())
                if (!parentAnim.isRunning()) {// preCheck new TODO
                   master.add(parentAnim);
                }
        parentAnim.setRef(event.getRef());
    }

    private CompositeAnim getEventAttachAnim(Event event, Anim anim) {
        DC_ActiveObj active = (DC_ActiveObj) event.getRef().getActive();
        if (active != null) {
            //            if (event.getType() instanceof STANDARD_EVENT_TYPE) {
            ////                switch (((STANDARD_EVENT_TYPE) event.getType())) {
            //                if (event.getType() == DeathAnim.EVENT_TYPE) {
            //                    if (active.getRef().getTargetObj() != active.getOwnerObj())
            //                    if (active.getChecker().isPotentiallyHostile()) {
            return AnimMaster. getParentAnim(active.getRef());
            //                    }
            //                }
            //            }
            //
        }
        //        if (leadAnimation!=null )
        //            if (leadAnimation.getActive()!=active)
        //        return leadAnimation;

        return new CompositeAnim();
    }

}
