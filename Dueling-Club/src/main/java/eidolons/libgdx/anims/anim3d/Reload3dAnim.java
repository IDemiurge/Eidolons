package eidolons.libgdx.anims.anim3d;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.anims.anim3d.AnimMaster3d.PROJECTION;
import eidolons.libgdx.anims.anim3d.AnimMaster3d.WEAPON_ANIM_CASE;

/**
 * Created by JustMe on 9/20/2017.
 */
public class Reload3dAnim extends Weapon3dAnim {
    public Reload3dAnim(DC_ActiveObj active) {
        super(active);
    }

    @Override
    protected WEAPON_ANIM_CASE getCase() {
        return WEAPON_ANIM_CASE.RELOAD;
    }

    @Override
    protected void resetSprites() {
        super.resetSprites();
    }

    @Override
    protected PROJECTION getProjection() {
        return super.getProjection();
//        return PROJECTION.HOR;
    }
}
