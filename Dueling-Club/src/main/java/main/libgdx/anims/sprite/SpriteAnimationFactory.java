package main.libgdx.anims.sprite;

import com.badlogic.gdx.graphics.Texture;
import main.system.images.ImageManager;
import main.test.frontend.Showcase;

/**
 * Created by JustMe on 3/6/2017.
 */
public class SpriteAnimationFactory {
    final static float defaultFrameDuration = 0.025f;

    public static SpriteAnimation
    getSpriteAnimation(String texturePath) {
        if (!ImageManager.isImage(texturePath)) {
            main.system.auxiliary.log.LogMaster.log(1, "*********NO SPRITE FOUND " + texturePath);
            texturePath = ImageManager.getEmptyItemIconPath(false);
            if (Showcase.isRunning()) {
                main.system.auxiliary.log.LogMaster.log(1, "trying anyway ");
                try {
                    return new SpriteAnimation(texturePath);
                } catch (Exception e) {

                }

            }
        }
        return new SpriteAnimation(texturePath);
    }


    public static SpriteAnimation getSpriteAnimation(String path
     , boolean singleSprite) {
        if (Showcase.isRunning())
            try {
                return new SpriteAnimation(defaultFrameDuration, false, 1, path, null, singleSprite);
            } catch (Exception e) {
                main.system.auxiliary.log.LogMaster.log(1,
                 "*********NO SPRITE FOUND getSpriteAnimation " + path);
                path = ImageManager.getEmptyItemIconPath(false);
            }
        return new SpriteAnimation(defaultFrameDuration, false, 1, path, null, singleSprite);
    }

    public static SpriteAnimation getSpriteAnimation(float frameDuration, boolean looping, int loops,
                                                     String path,
                                                     Texture texture
     , boolean singleSprite) {
        if (!ImageManager.isImage(path)) {
            path = ImageManager.getEmptyItemIconPath(false);
        }
        return new SpriteAnimation(defaultFrameDuration, looping, loops, path, null, singleSprite);

    }
}
