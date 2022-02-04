package libgdx.anims.anim3d;

import eidolons.content.consts.VisualEnums;
import eidolons.entity.feat.active.ActiveObj;
import main.entity.Ref;
import main.game.bf.Coordinates;

/**
 * Created by JustMe on 9/9/2017.
 */
public class Ranged3dAnim extends Weapon3dAnim {
    public Ranged3dAnim(ActiveObj active) {
        super(active);
    }

    @Override
    public Coordinates getDestinationCoordinates() {
        return getOriginCoordinates();
    }

    @Override
    public void start(Ref ref) {
        //missile to target, weapon on source
        //it's like there will be "precast" animation really
        super.start(ref);
    }

    @Override
    protected VisualEnums.WEAPON_ANIM_CASE getCase() {
        return VisualEnums.WEAPON_ANIM_CASE.NORMAL;
    }
}
