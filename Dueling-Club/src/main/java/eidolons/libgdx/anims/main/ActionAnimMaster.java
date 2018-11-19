package eidolons.libgdx.anims.main;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.anims.AnimContext;
import eidolons.libgdx.anims.CompositeAnim;
import eidolons.libgdx.anims.construct.AnimConstructor;
import main.system.auxiliary.log.LogMaster;

import static eidolons.libgdx.anims.main.AnimMaster.isAnimationOffFor;

/**
 * Created by JustMe on 11/15/2018.
 */
public class ActionAnimMaster {


    AnimMaster master;

    public ActionAnimMaster(AnimMaster master) {
        this.master = master;
    }

    protected void initActionAnimation(DC_ActiveObj activeObj, AnimContext context) {
        if (isAnimationOffFor(activeObj.getOwnerObj(), null)) {
            return;
        }
        boolean attachToNext = context.isAttachToNext();
        CompositeAnim animation = AnimConstructor.getOrCreate(activeObj);
        if (animation == null) {
            LogMaster.log(LogMaster.ANIM_DEBUG, "NULL ANIM FOR " + activeObj);
            return;
        }
        if (animation.isEventAnim())
            return;
        if (animation.isRunning())
            return;

        //        animation.reset();
        animation.setWaitingForNext(attachToNext);
        //        if (leadAnimation == null && !attachToNext) {
        //            leadAnimation = animation;
        //            leadAnimation.start(context);
        //        } else {
        animation.setRef(context);
        master.add(animation);
        if (master.getParallelDrawing()) {
            animation.start(context);
        }
        //            controller.store(animation);
        //        }
    }

}
