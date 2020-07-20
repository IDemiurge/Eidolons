package eidolons.libgdx.texture;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.assets.AssetEnums;
import eidolons.libgdx.assets.Assets;
import eidolons.libgdx.assets.Atlases;
import eidolons.libgdx.assets.utils.AtlasGen;
import eidolons.libgdx.gui.generic.btn.FlipDrawable;
import eidolons.libgdx.screens.AtlasGenSpriteBatch;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.MapMaster;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import static main.system.auxiliary.log.LogMaster.important;
import static main.system.auxiliary.log.LogMaster.log;

public class TextureCache {
    public static final boolean atlasesOn = !CoreEngine.TEST_LAUNCH || Flags.isJar();
    public static final boolean fullLog = Flags.isJarlike() || Flags.isJar();
    private static final Lock creationLock = new ReentrantLock();
    private static final ObjectMap<String, TextureRegion> regionCache = new ObjectMap<>(1300);
    private static final ObjectMap<TextureRegion, TextureRegionDrawable> drawableMap = new ObjectMap<>(400);
    private static final ObjectMap<TextureRegion, FlipDrawable> flipDrawableMap = new ObjectMap<>(400);
    public static final Set<String> missingTextures = new LinkedHashSet<>();
    public static final Set<String> atlasMissingTextures = new LinkedHashSet<>();

    private static Texture missingTexture;
    private static boolean returnEmptyOnFail = true;

    private final ObjectMap<String, Texture> cache;
    private final ObjectMap<Texture, Texture> greyscaleCache;
    private final String imagePath;
    private static final boolean stats = Flags.isIDE();
    private static final Map<String, Integer> statMap = new LinkedHashMap<>();
    private boolean silent;
    private static TextureCache instance;

    @Deprecated
    public static TextureRegion fromAtlas(String atlasPath, String light) {
        if (Assets.get().getManager().isLoaded(atlasPath)) {
            SmartTextureAtlas atlas = Assets.get().getManager().get(atlasPath);
            return atlas.findRegion(light);
        }
        return new TextureRegion(getMissingTexture());
    }

    private TextureCache() {
        this.imagePath = PathFinder.getImagePath();
        this.cache = new ObjectMap<>(1300);
        this.greyscaleCache = new ObjectMap<>(100);
    }


    @Deprecated
    public void loadAtlases(boolean menu) {
        if (atlasesOn)
            if (menu) {
                Assets.preloadMenu();
            } else {
                Assets.preloadDC();
            }
    }

    public static String getTexturePath(String s) {
        return GdxImageMaster.appendImagePath(s);
    }

    private static void init() {
        try {
            creationLock.lock();
            if (instance == null) {
                instance = new TextureCache();
                SpriteAnimationFactory.init();
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            creationLock.unlock();
        }
    }

    ///////////// MAIN METHOD //////////////
    public static TextureRegion getOrCreateR(String path, AssetEnums.ATLAS atlas) {
        return getOrCreateR(path, false, atlas);
    }

    public static TextureRegion getOrCreateR(String path, boolean overrideNoAtlas,
                                             AssetEnums.ATLAS atlas) {
        if (stats) {
            MapMaster.addToIntegerMap(statMap, path, 1);
        }
        if (path == null) {
            log(1, "EMPTY TEXTURE REGION REQUEST!");
            return new TextureRegion(missingTexture);
        }
        TextureRegion region = regionCache.get(path);


        if (region != null) {
            if (atlasesOn) if (!missingTextures.contains(path))
                if (!(region instanceof TextureAtlas.AtlasRegion)) {
                    missingTextures.add(path);
                    log(1, missingTextures.size() + " - Atlas missing texture: " + path);
                }
            return region;
        }

        if (fullLog) log(1, atlas + " - TextureRegion request: " + path);
        if (atlasesOn && !overrideNoAtlas) {
            String name = GdxImageMaster.cropImagePath(StringMaster.cropFormat(path));
            // if (!atlasMissingTextures.contains(path))
            {
                if (atlas == null) {
                    atlas = AtlasGen.getAtlasForPath(name);
                }
                if (atlas != null) {
                    if (atlas.file != null) {
                        region = atlas.file.findRegion(name);
                    }
                }
                if (region == null) {
                    important(name + " - no img in atlas: " + atlas);
                    for (AssetEnums.ATLAS a : Atlases.all) {
                        if (a == atlas) continue;
                        region = a.file.findRegion(name);
                        if (region != null) {
                            important(name + " - FOUND in atlas: " + a);
                            break;
                        }
                    }
                }

                if (atlasesOn && region == null) {
                    important("[!!!] No img in atlases: " + name);
                    atlasMissingTextures.add(path);
                    if (Flags.isJarlike() && Flags.isIDE())
                        return new TextureRegion(getMissingTexture());
                }
            }
        }
        if (region != null) {
            if (atlasesOn) {
                atlasMissingTextures.remove(path);
                if (!SmartTextureAtlas.isCached()) {
                    regionCache.put(path, region);
                }
            } else
                regionCache.put(path, region);

            if (!overrideNoAtlas)
                return region;
        }

        region = new TextureRegion(getInstance()._getOrCreate(path));
        if (region.getTexture() != missingTexture)
            regionCache.put(path, region);
        return region;
    }

    public static Texture getOrCreate(String path, boolean silent) {
        getInstance().setSilent(silent);
        Texture texture = null;
        try {
            texture = getInstance()._getOrCreate(path);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            texture = null;
        }
        getInstance().setSilent(false);
        return texture;
    }

    public static Texture getOrCreate(String path) {
        return getInstance()._getOrCreate(path);
    }

    public static TextureRegion getOrCreateRoundedRegion(String path) {
        return getOrCreateRoundedRegion(path, true);
    }

    public static TextureRegion getOrCreateRoundedRegion(String path, boolean write) {
        TextureRegion region = getOrCreateR(GdxImageMaster.getRoundedPathRadial(path));
        if (!region.getTexture().equals(missingTexture)) {
            return region;
        }
        region = getOrCreateR(GdxImageMaster.getRoundedPath(path));
        if (!region.getTexture().equals(missingTexture)) {
            return region;
        }
        return GdxImageMaster.round(path, write, "");
    }

    public static TextureRegion getOrCreateR(String path) {
        return getOrCreateR(path, false);
    }

    public static TextureRegion getOrCreateR(String path, boolean overrideNoAtlas) {
        if (overrideNoAtlas) {
            return getOrCreateR(path, true, null);
        }
        return getOrCreateR(path, false, AtlasGen.getAtlasForPath(path));
    }

    public static Texture getMissingTexture() {
        if (missingTexture == null)
            missingTexture = new Texture(getMissingPath());

        return missingTexture;
    }

    public static String getMissingPath() {
        return ImageManager.getImageFolderPath() +
                Images.MISSING_TEXTURE;
    }

    public static String getEmptyPath() {
        return ImageManager.getImageFolderPath() +
                Images.REALLY_EMPTY_32;
    }

    public static TextureRegionDrawable getOrCreateTextureRegionDrawable(TextureRegion originalTexture) {
        TextureRegionDrawable drawable = drawableMap.get(originalTexture);
        if (drawable == null) {
            drawable = new TextureRegionDrawable(originalTexture);
            drawableMap.put(originalTexture, drawable);
        }
        return drawable;
    }

    public static Drawable getOrCreateTextureRegionDrawable(TextureRegion textureRegion,
                                                            Supplier<Boolean> o, Supplier<Boolean> o1) {
        FlipDrawable flipDrawable = flipDrawableMap.get(textureRegion);
        if (flipDrawable == null) {
            flipDrawable = new FlipDrawable(new TextureRegionDrawable(textureRegion),
                    o, o1);
            flipDrawableMap.put(textureRegion, flipDrawable);
        }

        return flipDrawable;
    }

    public static TextureRegionDrawable getOrCreateTextureRegionDrawable(String imagePath) {
        if (imagePath == null)
            return null;
        return getOrCreateTextureRegionDrawable(getOrCreateR(imagePath));
    }

    public static String formatTexturePath(String path) {
        return FileManager.formatPath(path, true, true);
    }

    public static TextureRegion getOrCreateSizedRegion(int iconSize, String path) {
        Texture sized = GdxImageMaster.size(path, iconSize, true);
        if (sized == null)
            return new TextureRegion(getMissingTexture());
        return new TextureRegion(sized);
    }

    public static TextureRegion getRegion(String path, Texture texture) {
        TextureRegion region = regionCache.get(path);
        if (region == null) {
            region = new TextureRegion(texture);
            regionCache.put(path, region);
        }
        return region;
    }

    public static TextureRegion getSizedRegion(int imageSize, String path) {
        if (path == null)
            return null;
        if (atlasesOn) {
            TextureRegion orig = getOrCreateR(path);
            if (orig.getRegionHeight() == imageSize &&
                    orig.getRegionWidth() == imageSize)
                return orig;
            path = GdxImageMaster.getSizedImagePath(path, imageSize);
            return getOrCreateR(path, false, null);
        }
        String sized = StringMaster.getAppendedImageFile(path, " " + imageSize);
        return getRegion(sized, GdxImageMaster.size(sized, imageSize, true));
    }

    public static boolean isReturnEmptyOnFail() {
        return returnEmptyOnFail;
    }

    public static void setReturnEmptyOnFail(boolean returnEmptyOnFail) {
        TextureCache.returnEmptyOnFail = returnEmptyOnFail;
    }

    public static TextureCache getInstance() {
        if (instance == null) init();
        return instance;
    }

    //TODO gdx refinement
    public static boolean isImage(String property) {
        if (atlasesOn){
            if (findTexture(property) != null)
                return true;
        }
        if (!GdxMaster.isLwjglThread())
            return ImageManager.isImage(property);
        if (!isReturnEmptyOnFail())
            return getOrCreate(property) != null;
        Texture t = null;
        try {
            t = getOrCreate(property);
        } catch (Exception e) {
        }
        if (t == null) {
            return false;
        }
        return t != missingTexture;
    }

    private static TextureRegion findTexture(String property) {
        AssetEnums.ATLAS atlas = AtlasGen.getAtlasForPath(property);
        TextureAtlas.AtlasRegion region = atlas.findRegion(property);
        if (region == null)
        for (AssetEnums.ATLAS a : Atlases.all) {
            region = a.findRegion(property);
            if (region != null) {
                break;
            }
        }
        return region;
    }

    public static boolean isEmptyTexture(Texture texture) {
        if (texture == null) {
            return true;
        }
        return missingTexture == texture;
    }

    public static boolean isEmptyTexture(TextureRegion region) {
        return isEmptyTexture(region.getTexture());
    }


    private Texture _getOrCreate(String path) {
        if (stats) {
            MapMaster.addToIntegerMap(statMap, path, 1);
        }
        if (path == null) {
            log(1, "EMPTY TEXTURE REQUEST!");
            return missingTexture;
        }
        path = getPathForCache(path);

        if (!this.cache.containsKey(path)) {
            Texture x = _createTexture(path, true, false);
            if (x != null)
                return x;
        }

        return this.cache.get(path);
    }

    private String getPathForCache(String path) {
        path = path
                .toLowerCase();
        if (path.startsWith("img")) {
            path = path.replaceFirst("img", "");
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }

    public Texture _createTexture(String path, boolean putIntoCache, boolean recursion) {
        String filePath = null;
        FileHandle fullPath = null;
        if (fullLog) log(1, "CreateTexture request: " + path);
        Path p = null;
        try {
            p = Paths.get(imagePath, path);
            filePath = p.toString();
        } catch (Exception e) {
            filePath = path;
        }

        Texture t = null;
        fullPath = GDX.file(filePath);
        if (fullPath.exists())
            try {
                t = new Texture(fullPath, Pixmap.Format.RGBA8888, false);
                if (fullLog) log(1, "Texture Created: " + filePath);
                if (putIntoCache) {
                    cache.put(path, t);
                }
            } catch (Exception e) {
            }
        if (t == null) {
            if (!recursion) {
                if (path.contains(".png"))
                    return _createTexture(path.replace(".png", ".jpg"), putIntoCache, true);
                if (path.contains(".jpg"))
                    return _createTexture(path.replace(".jpg", ".png"), putIntoCache, true);
            }
            if (!silent || fullLog)
                main.system.auxiliary.log.LogMaster.verbose("No texture for " + fullPath);
            if (!isReturnEmptyOnFail())
                return null;
            if (!cache.containsKey(getMissingPath())) {
                if (putIntoCache)
                    cache.put(getMissingPath(), getMissingTexture());
                missingTextures.add(path);
                return getMissingTexture();
            }
            return cache.get(getMissingPath());

        }
        return t;
    }

    public Texture createAndCacheTexture(String path, Pixmap pixmap) {
        Texture texture = new Texture(pixmap);
        cache.put(path, texture);
        return texture;
    }

    public TextureRegion createAndCacheRegion(String path, Pixmap pixmap) {
        path = getPathForCache(path);
        Texture texture = new Texture(pixmap);
        cache.put(path, texture);
        TextureRegion r = new TextureRegion(texture);
        regionCache.put(path, r);
        return r;
    }

    public static boolean isCached(String s) {
        if (missingTextures.contains(s)) {
            return false;
        }
        Texture texture = getInstance().cache.get(s);
        if (texture != null) {
            return true;
        }
        TextureRegion textureRegion = getOrCreateR(s);
        if (isEmptyTexture(textureRegion)) {
            missingTextures.add(s);
            return false;
        }
        return true;
    }

    public static TextureRegion getFlippedRegion(boolean x, boolean y, String path) {
        if (atlasesOn) {
            return getOrCreateR(path, false, null);
        }
        Texture texture = GdxImageMaster.flip(path, x, y, Flags.isIDE());
        return getRegion(GdxImageMaster.getFlippedPath(path, x, y), texture);
    }


    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public void clearCache() {
        cache.clear();
    }

    public void logDiagnostics() {
        if (stats) {
            MapMaster.sort(statMap);
            important(statMap.toString());
        }
        important(
                "atlasMissingTextures.size " + atlasMissingTextures.size() +
                        "\nmissingTextures.size " + missingTextures.size() +
                        "\ncache.size " + cache.size +
                        "\nregionCache.size " + regionCache.size +
                        "\ndrawableMap.size " + drawableMap.size +
                        "\ngreyscaleCache.size " + greyscaleCache.size
        );
        important("atlasMissingTextures:" + atlasMissingTextures);

        if (GdxMaster.getMainBatch() instanceof AtlasGenSpriteBatch) {
            // ((AtlasGenSpriteBatch) GdxMaster.getMainBatch()).writeAtlases();
        }
    }

    public static TextureRegion getRegionUV(String s) {
        return getOrCreateR(s, false, AssetEnums.ATLAS.UNIT_VIEW);
    }

    public static TextureRegion getRegionUI(String s) {
        return getOrCreateR(s, false, AssetEnums.ATLAS.UI_BASE);
    }

    public static TextureRegion getRegionUI_DC(String s) {
        return getOrCreateR(s, false, AssetEnums.ATLAS.UI_DC);
    }

    public static TextureRegion getRegionTEX(String s) {
        return getOrCreateR(s, false, AssetEnums.ATLAS.TEXTURES);
    }
}

