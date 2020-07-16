package eidolons.libgdx.anims.anim3d;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.assets.AnimMaster3d.PROJECTION;
import eidolons.libgdx.assets.AnimMaster3d.WEAPON_ANIM_CASE;
import main.entity.Ref;

/**
 * Created by JustMe on 12/3/2017.
 */
public class Potion3dAnim extends Weapon3dAnim {
    public Potion3dAnim(DC_ActiveObj active) {
        super(active);
    }

    protected float getSpriteScale() {
        return 0.66f;
    }

    @Override
    public PROJECTION getProjection(Ref ref, DC_ActiveObj active) {
        return PROJECTION.HOR;
    }

    @Override
    protected WEAPON_ANIM_CASE getCase() {
        return WEAPON_ANIM_CASE.POTION;
    }
}
