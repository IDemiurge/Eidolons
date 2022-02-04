package libgdx.anims.anim3d;

import eidolons.content.consts.VisualEnums;
import eidolons.entity.feat.active.ActiveObj;

/**
 * Created by JustMe on 9/20/2017.
 */
public class Reload3dAnim extends Weapon3dAnim {
    public Reload3dAnim(ActiveObj active) {
        super(active);
    }

    @Override
    protected VisualEnums.WEAPON_ANIM_CASE getCase() {
        return VisualEnums.WEAPON_ANIM_CASE.RELOAD;
    }

    @Override
    protected void resetSprites() {
        super.resetSprites();
    }

    @Override
    public VisualEnums.PROJECTION getProjection() {
        return super.getProjection(null , getActive());
    }
}
