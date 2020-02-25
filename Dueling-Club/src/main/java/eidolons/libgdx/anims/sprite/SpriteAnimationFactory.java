package eidolons.libgdx.anims.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.anims.Assets;
import eidolons.libgdx.anims.anim3d.AnimMaster3d;
import eidolons.libgdx.bf.boss.anim.BossAnimator;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.Sprites;
import eidolons.libgdx.texture.TextureCache;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 3/6/2017.
 */
public class SpriteAnimationFactory {
    public final static float defaultFrameDuration = 0.025f;
    public final static float fps30 = 0.033f;
    static Map<String, SpriteAnimation> cache = new HashMap<>();
    public static Array<AtlasRegion> dummySpriteRegions;
    private static boolean singleFrameSpriteMode = false;
    private static boolean testMode = false;

    public static void init() {
        dummySpriteRegions = new Array();
        if (CoreEngine.isIDE() && testMode)
            dummySpriteRegions.add(new AtlasRegion(TextureCache.getOrCreate(Images.EMPTY_SKULL), 0, 0, 128, 128));
        else {
            dummySpriteRegions.add(new AtlasRegion(TextureCache.getOrCreate(Images.REALLY_EMPTY_32), 0, 0, 128, 128));
        }
    }

    //    public static SpriteAnimation getSpriteFromAtlas(String key, String atlasPath) {
//        //TODO
//        return getSpriteAnimation(key, true);
//    }

    private static String getSingleFrameSpritePath(String key) {
        return SingleSpriteGenerator.getPath(key);
    }


    public static SpriteAnimation getSpriteAnimation(String key) {
        return getSpriteAnimation(key, true, true);
    }

    public static SpriteAnimation getSpriteAnimation(String key, boolean useDefault) {
        return getSpriteAnimation(key, useDefault, true);
    }

    public static SpriteAnimation getSpriteAnimation(String key, boolean useDefault, Boolean useCache_orNullIfOnlyCached) {
        return getSpriteAnimation(key, useDefault, useCache_orNullIfOnlyCached, false);
    }
//    private static SpriteAnimation getSpriteAnimation(String key, boolean useDefault, Boolean useCache_orNullIfOnlyCached) {
//        return getSpriteAnimation(key, useDefault, useCache_orNullIfOnlyCached, false);
//    }

    public static SpriteAnimation getSpriteAnimation(String key, boolean useDefault, Boolean useCache_orNullIfOnlyCached
            , boolean ktx) {
        key = FileManager.formatPath(key, true, true);

        if (!key.contains(".")) {
            key = Sprites.substituteKey(key);
        }
        if (!Assets.checkSprite(key)) {
            return getDefaultSprite(key);
        }
        if (singleFrameSpriteMode) {
            key = getSingleFrameSpritePath(key);
        }

        if (!key.contains(".")) {
            if (!useDefault) {
                return null;
            }
        } else {
            if (useCache_orNullIfOnlyCached == null) {
                if (cache.get(key.toLowerCase()) == null) {
                    return null;
                }
                useCache_orNullIfOnlyCached = true;
            }
            SpriteAnimation sprite = useCache_orNullIfOnlyCached ? cache.get(key.toLowerCase()) : null;
            if (sprite != null) {
                sprite.reset();
                return sprite;
            }
        }
        String texturePath = key;
        if (texturePath.toLowerCase().endsWith(".atlas")
                || texturePath.toLowerCase().endsWith(".txt")) {
            texturePath = GdxImageMaster.appendImagePath(texturePath);

//            boolean ktx = Assets.isKtx(texturePath);
            if (ktx) {
                Assets.loadedKtxAtlas(texturePath);
                texturePath = Assets.getKtxAtlasPath(texturePath);
            }
            TextureAtlas atlas = null;
            try {
                atlas = AnimMaster3d.getOrCreateAtlas(texturePath);

            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                main.system.auxiliary.log.LogMaster.important("CRITICAL: No atlas for path - " + key);
                main.system.auxiliary.log.LogMaster.important("Setting Lite Mode... ");
                CoreEngine.setLiteLaunch(true);
//          TODO really?
//           OptionsMaster.getGraphicsOptions().setValue(GraphicsOptions.GRAPHIC_OPTION.LITE_MODE, true);
//                OptionsMaster.saveOptions();
                return getDefaultSprite(key);
            }
            if (atlas == null) {
                main.system.auxiliary.log.LogMaster.important("CRITICAL: No atlas for path - " + key);
                return getDefaultSprite(key);
            }
            SpriteAnimation a = new SpriteAnimation(fps30, false, atlas);
            cache.put(
                    key.toLowerCase(), a);
            return a;
        } else if (!ImageManager.isImage(texturePath)) {
            if (useDefault) {
                return getDefaultSprite(key);
            } else {
                main.system.auxiliary.log.LogMaster.log(1, "****NO SPRITE FOUND "
                        + texturePath);
                return null;
            }
        }
        SpriteAnimation a = new SpriteAnimation(texturePath, true);
        cache.put(key.toLowerCase(), a);
        return a;
    }

    public static SpriteAnimation getDefaultSprite(String key) {
        main.system.auxiliary.log.LogMaster.important("Sprite replaced by default: " + key);
        SpriteAnimation spriteAnimation = new SpriteAnimation(fps30, false, dummySpriteRegions);
        spriteAnimation.setDefault(true);
        return spriteAnimation;
    }


    public static SpriteAnimation createSpriteVariant(String name, BossAnimator.BossSpriteVariant variant) {
        String path = StringMaster.getAppendedFile(name, " " + variant.toString());
        return getSpriteAnimation(path);

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

    public static SpriteAnimation getSpriteAnimation(boolean singleSprite, String path
    ) {
        return getSpriteAnimation(true, singleSprite, path);
    }

    public static SpriteAnimation getSpriteAnimation(boolean useCache, boolean singleSprite, String path
    ) {
        SpriteAnimation anim = useCache ? cache.get(path.toLowerCase()) : null;
        if (anim != null)
            return anim;
//        if (Showcase.isRunning())
        try {
            anim = new SpriteAnimation(defaultFrameDuration, false, 1, path, null, singleSprite);
        } catch (Exception e) {
            //TODO don't try, check!
            main.system.auxiliary.log.LogMaster.log(1,
                    "*********NO SPRITES FOUND getSpriteAnimation " + path);
            return null;
//                    TODO what should be default?
//                     getSpriteAnimation(ImageManager.getEmptyItemIconPath(false), false);
        }
        cache.put(path.toLowerCase(), anim);
        return anim;
    }

    public static SpriteAnimation getSpriteAnimation(float frameDuration, boolean looping, int loops,
                                                     String path,
                                                     Texture texture
            , boolean singleSprite) {
//        if (!ImageManager.isImage(path)) {
//            path = ImageManager.getEmptyItemIconPath(false);
//        }
        return new SpriteAnimation(defaultFrameDuration, looping, loops, path, null, singleSprite);

    }

    public static Array<AtlasRegion> getSpriteRegions(boolean backAndForth, TextureAtlas atlas) {
        Array<AtlasRegion> regions = atlas.getRegions();
        if (!backAndForth) {
            return regions;
        }
        Array<AtlasRegion> reversed = new Array<>(regions);
        reversed.reverse();
        regions.addAll(reversed, 1, regions.size - 2);
        return regions;
    }

    public static boolean isDefault(SpriteAnimation sprite) {
        return sprite.isDefault();
    }

    public static void disposed(String path) {
        cache.remove(path);
    }

}
