package libgdx.anims.anim3d;

import eidolons.entity.active.ActiveObj;
import eidolons.entity.item.WeaponItem;
import libgdx.anims.sprite.SpriteAnimation;

/**
 * Created by JustMe on 5/30/2018.
 */
public class Parry3dAnim extends Weapon3dAnim{

    public Parry3dAnim(WeaponItem parryWeapon, ActiveObj attack) {
        super(null );
    }

    @Override
    protected SpriteAnimation get3dSprite() {
        return super.get3dSprite();
    }
}
