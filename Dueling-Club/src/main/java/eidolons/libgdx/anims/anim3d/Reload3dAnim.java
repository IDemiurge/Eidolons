package eidolons.libgdx.anims.anim3d;

import eidolons.entity.active.DC_ActiveObj;
import eidolons.libgdx.assets.AssetEnums;
import eidolons.libgdx.assets.AssetEnums.PROJECTION;
import eidolons.libgdx.assets.AssetEnums.WEAPON_ANIM_CASE;

/**
 * Created by JustMe on 9/20/2017.
 */
public class Reload3dAnim extends Weapon3dAnim {
    public Reload3dAnim(DC_ActiveObj active) {
        super(active);
    }

    @Override
    protected WEAPON_ANIM_CASE getCase() {
        return AssetEnums.WEAPON_ANIM_CASE.RELOAD;
    }

    @Override
    protected void resetSprites() {
        super.resetSprites();
    }

    @Override
    public PROJECTION getProjection() {
        return super.getProjection(null , getActive());
    }
}
