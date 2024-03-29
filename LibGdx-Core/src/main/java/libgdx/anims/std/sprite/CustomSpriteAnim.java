package libgdx.anims.std.sprite;

import com.badlogic.gdx.graphics.g2d.Animation;
import libgdx.anims.AnimData;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.anims.sprite.SpriteAnimationFactory;
import libgdx.anims.std.ActionAnim;
import main.entity.Entity;
import main.system.data.DataUnitFactory;

import java.util.ArrayList;

public class CustomSpriteAnim extends ActionAnim {

    protected SpriteAnimation sprite;

    public CustomSpriteAnim(String path) {
        this(null , path);
    }
    public CustomSpriteAnim(Entity active, SpriteAnimation sprite) {
        this(active, "");
        this.sprite = sprite;
    }

    public CustomSpriteAnim(Entity active, String sprites) {
        super(active,
                new AnimData(
                        DataUnitFactory.getKeyValueString(true, AnimData.ANIM_VALUES.SPRITES, sprites)
                ));
    }

    @Override
    public void initPosition() {
        if (origin!=null) {
            return;
        }
        super.initPosition();
    }

    @Override
    protected void resetSprites() {
        sprites = new ArrayList<>();
        if (sprite == null) {
            sprite = getOrCreateSprite();
            sprite.setFps(getDefaultFps());
        } else sprite.reset();
        duration = sprite.getFrameNumber() * sprite.getFrameDuration();
        sprite.setPlayMode(getPlayMode());
        sprite.setLooping(false);
        sprites.add(sprite);
    }

    protected Animation.PlayMode getPlayMode() {
        return Animation.PlayMode.NORMAL;
    }

    protected int getDefaultFps() {
        return 20;
    }


    private SpriteAnimation getOrCreateSprite() {
        String path =  data.getValue(AnimData.ANIM_VALUES.SPRITES);
//        path = GdxImageMaster.cropImagePath(path);
        if (!path.contains("sprites")) {
            path  = "sprites/" + path;
        }
//        path = PathFinder.getImagePath() + path;
        if (!path.contains(".")) {
            path += ".txt";
        }
        return SpriteAnimationFactory.getSpriteAnimation(path);
    }
}
