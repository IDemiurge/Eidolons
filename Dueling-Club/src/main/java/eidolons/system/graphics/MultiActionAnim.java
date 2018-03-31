package eidolons.system.graphics;

import eidolons.entity.active.DC_ActiveObj;
import main.entity.obj.Obj;
import eidolons.system.graphics.AnimationManager.ANIM_TYPE;

import java.awt.*;

public class MultiActionAnim extends MultiAnim {

    private DC_ActiveObj action;

    public MultiActionAnim(DC_ActiveObj action) {
        super(ANIM_TYPE.ACTION, action);
        this.action = action;
    }

    @Override
    protected PhaseAnimation createAnimation(Object mainArg, Obj target) {
        ActionAnimation actionAnimation = new ActionAnimation(action);
        actionAnimation.setTarget(target);
        return actionAnimation;
    }

    @Override
    protected Image getThumbnailImage() {
        return animations.get(0).getThumbnailImage();
    }

}
