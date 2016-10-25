package main.system.graphics;

import main.entity.obj.Obj;
import main.entity.obj.top.DC_ActiveObj;
import main.system.graphics.AnimationManager.ANIM_TYPE;

import java.awt.*;

public class MultiActionAnim extends MultiAnim {

    private DC_ActiveObj action;

    public MultiActionAnim(DC_ActiveObj action) {
        super(ANIM_TYPE.ACTION, action);
        this.action = action;
    }

    @Override
    protected Animation createAnimation(Object mainArg, Obj target) {
        ActionAnimation actionAnimation = new ActionAnimation(action);
        actionAnimation.setTarget(target);
        return actionAnimation;
    }

    @Override
    protected Image getThumbnailImage() {
        return animations.get(0).getThumbnailImage();
    }

}
