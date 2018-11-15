package eidolons.libgdx.anims.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import main.system.images.ImageManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 3/6/2017.
 */
public class SpriteAnimationFactory {
    final static float defaultFrameDuration = 0.025f;
    static Map<String, SpriteAnimation> cache = new HashMap<>();

    public static SpriteAnimation
    getSpriteAnimation(String texturePath) {
        if (!ImageManager.isImage(texturePath)) {
            main.system.auxiliary.log.LogMaster.log(1, "*********NO SPRITE FOUND " + texturePath);
        }
        return new SpriteAnimation(texturePath);
    }

    public static SpriteAnimation getSpriteAnimation(String path,
                                                     String name,
                                                     float frameDuration,
                                                     int loops) {
        TextureAtlas atlas = new TextureAtlas(path);
        return getSpriteAnimation(atlas.findRegions(name), frameDuration, loops);
    }

    public static SpriteAnimation getSpriteAnimation(Array<AtlasRegion> regions,
                                                     float frameDuration, int loops) {
        SpriteAnimation s = new SpriteAnimation(
         frameDuration, false, regions);
        s.setLoops(loops);
        return s;
    }

    public static SpriteAnimation getSpriteAnimation(String path
     , boolean singleSprite) {
        SpriteAnimation anim = cache.get(path.toLowerCase());
        if (anim != null)
            return anim;
//        if (Showcase.isRunning())
        try {
            anim = new SpriteAnimation(defaultFrameDuration, false, 1, path, null, singleSprite);
        } catch (Exception e) {
            main.system.auxiliary.log.LogMaster.log(1,
             "*********NO SPRITE FOUND getSpriteAnimation " + path);
            return getSpriteAnimation(ImageManager.getEmptyItemIconPath(false), false);
        }
        cache.put(path.toLowerCase(), anim);
        return anim;
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
