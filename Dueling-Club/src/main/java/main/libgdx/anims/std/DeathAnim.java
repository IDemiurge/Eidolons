package main.libgdx.anims.std;

import main.entity.Entity;
import main.entity.obj.DC_HeroObj;
import main.libgdx.anims.Anim;
import main.libgdx.anims.AnimData;
import main.libgdx.anims.sprite.SpriteAnimation;

import java.util.List;

/**
 * Created by JustMe on 1/16/2017.
 */
public class DeathAnim extends Anim {
    DC_HeroObj unit;

    @Override
    public List<SpriteAnimation> getSprites() {
        return super.getSprites();
    }

    public DeathAnim(Entity active, AnimData params) {
        super(active, params);
    }
}
