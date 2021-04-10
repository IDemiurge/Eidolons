package libgdx.anims.sprite;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.content.consts.libgdx.GdxStringUtils;
import eidolons.game.core.Eidolons;
import libgdx.assets.Assets;
import libgdx.assets.Atlases;
import eidolons.content.consts.Images;
import eidolons.content.consts.Sprites;
import libgdx.texture.TextureCache;
import main.system.auxiliary.data.FileManager;
import main.system.images.ImageManager;
import main.system.launch.Flags;

import static main.system.auxiliary.log.LogMaster.important;
import static main.system.auxiliary.log.LogMaster.log;

/**
 * Created by JustMe on 3/6/2017.
 */
public class SpriteAnimationFactory {
    public final static float defaultFrameDuration = 0.025f;
    public final static float fps30 = 0.033f;
    static ObjectMap<String, SpriteAnimation> cache = new ObjectMap<>(350);
    public static Array<AtlasRegion> dummySpriteRegions;
    private static final boolean testMode = false;

    public static void init() {
        dummySpriteRegions = new Array();
        //TODO into base-ui
        if (Flags.isIDE() && testMode)
            dummySpriteRegions.add(new AtlasRegion(TextureCache.getOrCreate(Images.EMPTY_SKULL), 0, 0, 128, 128));
        else {
            dummySpriteRegions.add(new AtlasRegion(TextureCache.getOrCreate(Images.REALLY_EMPTY_32), 0, 0, 128, 128));
        }
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

    public static SpriteAnimation getSpriteAnimation(String key, boolean useDefault, Boolean useCache_orNullIfOnlyCached
            , boolean ktx) {
        key = FileManager.formatPath(key, true, true);

        if (!key.contains(".")) {
            key = Sprites.substituteKey(key);
        }
        if (!Assets.checkSprite(key)) {
            return getDefaultSprite(key);
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
            texturePath = GdxStringUtils.appendImagePath(texturePath);

            if (ktx) {
                Assets.loadedKtxAtlas(texturePath);
                texturePath = Assets.getKtxAtlasPath(texturePath);
            }
            TextureAtlas atlas;
            SpriteAnimation animation = null;
            if (isAtlasesFromMain()) {
                Array<AtlasRegion> regions = Atlases.getAtlasRegions(texturePath);
                if (regions != null)
                    if (regions.size > 0) {
                        animation = new SpriteAnimation(fps30, true, regions);
                    }
            }
            if (animation == null) {
                atlas = Atlases.getOrCreateAtlas(texturePath);
                if (atlas == null) {
                    important("CRITICAL: No atlas for path - " + key);
                    return getDefaultSprite(key);
                }
                if (animation == null) {
                    animation = new SpriteAnimation(fps30, false, atlas);
                }
            }
            cache.put(                    key.toLowerCase(), animation);
            return animation;
        } else if (!ImageManager.isImage(texturePath)) {
            if (useDefault) {
                return getDefaultSprite(key);
            } else {
                log(1, "****NO SPRITE FOUND "
                        + texturePath);
                return null;
            }
        }
        SpriteAnimation a = new SpriteAnimation(texturePath, true);
        cache.put(key.toLowerCase(), a);
        return a;
    }

    private static boolean isAtlasesFromMain() {
        return TextureCache.atlasesOn;
    }

    public static SpriteAnimation getDefaultSprite(String key) {
        important("Sprite replaced by default: " + key);
        SpriteAnimation spriteAnimation = new SpriteAnimation(fps30, false, dummySpriteRegions);
        spriteAnimation.setDefault(true);
        return spriteAnimation;
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

    public static void preload(String s) {
        Eidolons.onGdxThread(() -> getSpriteAnimation(TextureCache.getTexturePath(s)));
    }
}
