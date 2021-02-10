package libgdx.anims.anim3d;

import eidolons.entity.active.DC_ActiveObj;
import libgdx.assets.AssetEnums;
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
    public AssetEnums.PROJECTION getProjection(Ref ref, DC_ActiveObj active) {
        return AssetEnums.PROJECTION.HOR;
    }

    @Override
    protected AssetEnums.WEAPON_ANIM_CASE getCase() {
        return AssetEnums.WEAPON_ANIM_CASE.POTION;
    }
}
