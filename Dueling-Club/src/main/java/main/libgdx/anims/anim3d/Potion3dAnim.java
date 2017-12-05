package main.libgdx.anims.anim3d;

import main.entity.active.DC_ActiveObj;
import main.libgdx.anims.AnimMaster3d.PROJECTION;
import main.libgdx.anims.AnimMaster3d.WEAPON_ANIM_CASE;

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
    protected PROJECTION getProjection() {
        return PROJECTION.HOR ;
    }

    @Override
    protected WEAPON_ANIM_CASE getCase() {
        return WEAPON_ANIM_CASE.POTION;
    }
}
