package main.libgdx.anims.weapons;

import main.entity.active.DC_ActiveObj;
import main.libgdx.anims.AnimMaster3d.WEAPON_ANIM_CASE;

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

    protected WEAPON_ANIM_CASE getCase() {
        if (getActive().isFailedLast())
            return WEAPON_ANIM_CASE.MISSILE_MISS;
        return WEAPON_ANIM_CASE.MISSILE ;
    }

    @Override
    public float getPixelsPerSecond() {
        return 400;
    }

    @Override
    protected void initSpeed() {
        super.initSpeed();
    }
}
