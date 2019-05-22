package eidolons.libgdx.anims.std.sprite;

import eidolons.libgdx.anims.AnimData;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.anims.std.ActionAnim;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.system.data.DataUnitFactory;

import java.util.ArrayList;

public class CustomSpriteAnim extends ActionAnim {

protected     SpriteAnimation sprite;

    public CustomSpriteAnim(Entity active, String sprites) {
        super(active,
                new AnimData(
                        DataUnitFactory.getKeyValueString(true, AnimData.ANIM_VALUES.SPRITES, sprites)
                ));
    }
    @Override
    protected void resetSprites() {
        sprites = new ArrayList<>();
        sprite = getOrCreateSprite();
        sprite.setFps(getDefaultFps());
        duration = sprite.getFrameNumber() * sprite.getFrameDuration();
        sprites.add(sprite);
    }

    protected int getDefaultFps() {
        return 20;
    }


    private SpriteAnimation getOrCreateSprite() {
        return   SpriteAnimationFactory.getSpriteAnimation(
                PathFinder.getSpritesPathNew()+ data.getValue(AnimData.ANIM_VALUES.SPRITES)
                        +".txt");
    }
}
