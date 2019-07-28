package eidolons.libgdx.anims.main;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.anims.Anim;
import eidolons.libgdx.anims.CompositeAnim;
import eidolons.libgdx.anims.construct.AnimConstructor.ANIM_PART;
import eidolons.libgdx.anims.std.DeathAnim;
import eidolons.libgdx.anims.std.EventAnimCreator;
import eidolons.libgdx.anims.std.sprite.LockKeyAnimation;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import main.data.XLinkedMap;
import main.game.logic.event.Event;
import main.system.auxiliary.log.LogMaster;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by JustMe on 11/15/2018.
 */
public class EventAnimMaster {
    AnimMaster master;
    private Map<Event, Anim> pendingEventAnims = new XLinkedMap<>();

    public EventAnimMaster(AnimMaster master) {
        this.master = master;
    }

    protected void checkPendingEventAnimations() {
        for (Event event : new ArrayList<>(pendingEventAnims.keySet())) {
            if (initEventAnimation(pendingEventAnims.get(event), event, true)) {
                pendingEventAnims.remove(event);
            }

        }
    }

    protected boolean initEventAnimation(
            Event event) {
        return initEventAnimation(null, event, false);
    }

    protected boolean initEventAnimation(
            Anim anim, Event event, boolean check) {
        if (event.getRef().isDebug()) {
            return false;
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
        if (anim == null)
            anim = EventAnimCreator.getAnim(event);
        if (anim == null) {
            return false;
        }

        if (isImmediateAnim(anim)){
            anim.startAsSingleAnim(event.getRef());
            return true;

        }

        parentAnim = getEventAttachAnim(event, anim);
        if (parentAnim != null) {
            LogMaster.log(LogMaster.ANIM_DEBUG, anim +
                    " event anim created for: " + parentAnim);
            parentAnim.setRef(event.getRef());
//            if (parentAnim != master.getDrawer().getLeadAnimation())
            if (!parentAnim.isFinished())
                if (!parentAnim.isRunning()) {// preCheck new TODO

                    if ((anim instanceof DeathAnim) || //
                            (master.getDrawer().getLeadAnimation() != null && parentAnim != master.getDrawer().getLeadAnimation())) {
                        parentAnim.addEventAnim(anim, event);
                        if (parentAnim != master.getDrawer().getLeadAnimation())
                             master.add(parentAnim);
                        return true;
                    } else {
                        LogMaster.log(1, anim +
                                " event anim will be already shown on: " + parentAnim);
                    }
                }

            if (parentAnim.getMap().isEmpty())
                parentAnim.add(ANIM_PART.AFTEREFFECT, anim);
            else
                parentAnim.addEventAnim(anim, event); //TODO}
            return true;
        }
        if (!check)
            pendingEventAnims.put(event.getClone(), anim);
        return false;
    }

    private boolean isImmediateAnim(Anim anim) {
        if (anim instanceof LockKeyAnimation) {
            return true;
        }
            return false;
    }

    private CompositeAnim getEventAttachAnim(Event event, Anim anim) {
        DC_ActiveObj active = (DC_ActiveObj) event.getRef().getActive();
        if (active != null) {
            //            if (event.getType() instanceof STANDARD_EVENT_TYPE) {
            ////                switch (((STANDARD_EVENT_TYPE) event.getType())) {
            //                if (event.getType() == DeathAnim.EVENT_TYPE) {
            //                    if (active.getRef().getTargetObj() != active.getOwnerObj())
            //                    if (active.getChecker().isPotentiallyHostile()) {
            return AnimMaster.getParentAnim(active.getRef());
            //                    }
            //                }
            //            }
            //
        }
        //        if (leadAnimation!=null )
        //            if (leadAnimation.getActive()!=active)
        //        return leadAnimation;
//main.system.auxiliary.log.LogMaster.log(1,">> New event attach anim for " + active);
        return new CompositeAnim();
    }

}
