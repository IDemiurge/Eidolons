package eidolons.libgdx.anims.main;

import eidolons.ability.effects.oneshot.move.MoveEffect;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.game.core.ActionInput;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.master.EffectMaster;
import eidolons.libgdx.anims.AnimContext;
import eidolons.libgdx.anims.CompositeAnim;
import eidolons.libgdx.anims.construct.AnimConstructor;
import main.system.auxiliary.TimeMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.threading.WaitMaster;

import static eidolons.libgdx.anims.main.AnimMaster.isAnimationOffFor;

/**
 * Created by JustMe on 11/15/2018.
 */
public class ActionAnimMaster {

    private static final boolean LOGGED = false;
    private static ActionAnimMaster instance;
    AnimMaster master;
    private static CompositeAnim current;

    public ActionAnimMaster(AnimMaster master) {
        this.master = master;
        instance = this;
    }

    public static int getMaxAnimTime(DC_ActiveObj action) {
        if (action.isAttackOfOpportunityMode() || action.isInstantMode()) {
            return (int) (2000* AnimMaster.speedMod());
        }
        return (int) (3000* AnimMaster.speedMod());
    }

    public static boolean isWait(DC_ActiveObj action) {
        if (action.getOwnerUnit().isAiControlled()) {
            return false;
        }
        if (action.isMove()) {
            return false;
        }
        if (action.isSpell()) {
            if (!EffectMaster.getEffectsOfClass(action, MoveEffect.class).isEmpty()) {
            return false;
            }
        }
        if (action.isMode()) {
            return false;
        }
        return !action.isTurn();
    }

    public static void animate(ActionInput actionInput) {
        log("Animating: ", actionInput.getAction());
        Eidolons.onGdxThread(()->
        instance.initActionAnimation(actionInput.getAction(), (AnimContext) actionInput.getContext()));
        if (!isWait(actionInput.getAction())) {
            return;
        }
        for (int i = 0; i < 10; i++) {
            WaitMaster.WAIT(100);
            if (isAnimatingAction()) {
                // await();
                log("Waiting: ", actionInput.getAction());
                WaitMaster.waitForCondition(p -> !isAnimatingAction(),
                        getMaxAnimTime(actionInput.getAction()));
                log("Done waiting: ", actionInput.getAction());
                break;
            }
        }
    }

    protected static void log(String s, DC_ActiveObj action) {
        if (LOGGED)
            DC_Game.game.getLogManager().log(TimeMaster.getFormattedTime(true, true) + s + action.getName());
    }

    protected void initActionAnimation(DC_ActiveObj activeObj, AnimContext context) {
        CompositeAnim result = checkActionAnimates(activeObj, context);
        if (result != null) {
            current = result;
            DC_Game.game.getLogManager().log( TimeMaster.getFormattedTime(true, true) + "Current anim time: " + current.getTime());
        } else {
            log("Null anim: ", activeObj);
        }
    }

    public static boolean isAnimatingAction() {
        if (current == null) {
            return false;
        }
        if (current.isFinished()) {
            current=null;
            return false;
        }
        return true;
    }

    protected CompositeAnim checkActionAnimates(DC_ActiveObj activeObj, AnimContext context) {
        if (isAnimationOffFor(activeObj.getOwnerObj(), null)) {
            return null;
        }
        boolean attachToNext = context.isAttachToNext();
        CompositeAnim animation = AnimConstructor.getOrCreate(activeObj);
        if (animation == null) {
            LogMaster.log(LogMaster.ANIM_DEBUG, "NULL ANIM FOR " + activeObj);
            return null;
        }
        if (animation.isEventAnim())
            return null;
        if (animation.isRunning())
            return null;

        //        animation.reset();
        animation.setWaitingForNext(attachToNext);
        //        if (leadAnimation == null && !attachToNext) {
        //            leadAnimation = animation;
        //            leadAnimation.start(context);
        //        } else {
        animation.setRef(context);
        master.add(animation);
        if (master.isParallelDrawing()) {
            animation.start(context);
        }
        return animation;
    }

}
