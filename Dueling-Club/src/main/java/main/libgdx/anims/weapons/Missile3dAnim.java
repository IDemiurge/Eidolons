package main.libgdx.anims.weapons;

import main.entity.active.DC_ActiveObj;
import main.libgdx.anims.AnimMaster3d;
import main.libgdx.anims.sprite.SpriteAnimation;

/**
 * Created by JustMe on 9/9/2017.
 */
public class Missile3dAnim extends Weapon3dAnim {
    public Missile3dAnim(DC_ActiveObj active) {
        super(active);
    }

    @Override
    public void initPosition() {
        super.initPosition();
        //apply offsets?
    }

    @Override
    protected SpriteAnimation get3dSprite() {
        return  AnimMaster3d.getSpriteForAction(getDuration(), getActive(),
         ref.getTargetObj(),true);
    }

    @Override
    public int getPixelsPerSecond() {
        return 400;
    }

    @Override
    protected void initSpeed() {
        super.initSpeed();
    }
}
