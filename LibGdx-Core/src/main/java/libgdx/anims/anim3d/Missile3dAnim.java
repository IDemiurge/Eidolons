package libgdx.anims.anim3d;

import eidolons.content.consts.VisualEnums;
import eidolons.entity.active.DC_ActiveObj;

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
    protected float getSpriteScale() {
        return super.getSpriteScale();
    }

    protected VisualEnums.WEAPON_ANIM_CASE getCase() {
        if (getActive().isFailedLast())
            return VisualEnums.WEAPON_ANIM_CASE.MISSILE_MISS;
        return VisualEnums.WEAPON_ANIM_CASE.MISSILE;
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
