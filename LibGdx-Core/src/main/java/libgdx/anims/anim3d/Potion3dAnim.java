package libgdx.anims.anim3d;

import eidolons.content.consts.VisualEnums;
import eidolons.entity.feat.active.ActiveObj;
import main.entity.Ref;

/**
 * Created by JustMe on 12/3/2017.
 */
public class Potion3dAnim extends Weapon3dAnim {
    public Potion3dAnim(ActiveObj active) {
        super(active);
    }

    protected float getSpriteScale() {
        return 0.66f;
    }

    @Override
    public VisualEnums.PROJECTION getProjection(Ref ref, ActiveObj active) {
        return VisualEnums.PROJECTION.HOR;
    }

    @Override
    protected VisualEnums.WEAPON_ANIM_CASE getCase() {
        return VisualEnums.WEAPON_ANIM_CASE.POTION;
    }
}
