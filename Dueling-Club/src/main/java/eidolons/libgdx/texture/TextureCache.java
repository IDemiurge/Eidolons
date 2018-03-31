package eidolons.libgdx.texture;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.libgdx.GdxImageTransformer;
import eidolons.system.graphics.GreyscaleUtils;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextureCache {
    private static final boolean uiAtlasOn = false;
    private static TextureCache textureCache;
    private static Lock creationLock = new ReentrantLock();
    private static AtomicInteger counter = new AtomicInteger(0);
    private static boolean altTexturesOn = true;
    private static Texture emptyTexture;
    private static Map<String, TextureRegion> regionCache = new HashMap<>(300);
    private static Map<TextureRegion, Drawable> drawableMap = new HashMap<>(300);
    private Map<String, Texture> cache;
    private Map<Texture, Texture> greyscaleCache;
    private String imagePath;
    private TextureAtlas textureAtlas;
    private Pattern pattern;


    private TextureCache() {
        this.imagePath = PathFinder.getImagePath();
        this.cache = new HashMap<>();
        this.greyscaleCache = new HashMap<>();
        if (uiAtlasOn)
            textureAtlas = new SmartTextureAtlas(imagePath + "/ui//ui.txt");

        pattern = Pattern.compile("^.*[/\\\\]([a-z _\\-0-9]*)\\..*$");
    }

    private static void init() {
        try {
            creationLock.lock();
            if (textureCache == null) {
                textureCache = new TextureCache();
            }
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        } finally {
            creationLock.unlock();
        }
    }

    public static Texture getOrCreateGrayscale(String path) {
        if (textureCache == null) {
            init();
        }

        return textureCache._getOrCreateGrayScale(path);
    }

    public static Texture getOrCreate(String path) {
        if (textureCache == null) {
            init();
        }

        return textureCache._getOrCreate(path);
    }

    public static TextureRegion getOrCreateRoundedRegion(String path) {
        TextureRegion region = getOrCreateR(getRoundedPath(path));
        if (!region.getTexture().equals(emptyTexture)) {
            return region;
        }
        return GdxImageTransformer.round(path, true);
    }

    public static String getRoundedPath(String path) {
        return StringMaster.cropFormat(path) + " rounded.png";
    }

    public static TextureRegion getOrCreateR(String path) {
        if (textureCache == null) {
            init();
        }
        TextureRegion region = regionCache.get(path);
        if (region != null)
            return region;

        final Matcher matcher = textureCache.pattern.matcher(path);

        if (matcher.matches()) {
            String name = path.substring(
//          1+path.indexOf(StringMaster.getPathSeparator())
             3
             , path.lastIndexOf("."));// matcher.group(1);
            name = StringMaster.constructStringContainer
             (StringMaster.getPathSegments(name), "/");
            name = name.substring(0, name.length() - 1);

            if (uiAtlasOn)
                region = textureCache.textureAtlas.findRegion(name);
            if (region != null) {
                regionCache.put(path, region);
                counter.incrementAndGet();
                //cache!
                return region;
            }
        }

        region = new TextureRegion(textureCache._getOrCreate(path));
        if (region.getTexture() != emptyTexture)
            regionCache.put(path, region);
        return region;
    }

    private static boolean checkAltTexture(String path) {
        if (altTexturesOn) {
            return path.contains(StrPathBuilder.build("gen"));
        }
        return false;
    }

    public static TextureRegion getOrCreateGrayscaleR(String path) {
        return new TextureRegion(getOrCreateGrayscale(path));
    }

    public static void setAltTexturesOn(boolean altTexturesOn) {
        TextureCache.altTexturesOn = altTexturesOn;
    }

    public static Texture getEmptyTexture() {
        if (emptyTexture == null)
            emptyTexture = new Texture(getEmptyPath());

        return emptyTexture;
    }

    private static String getEmptyPath() {
        return
         ImageManager.getImageFolderPath() +
          ImageManager.getDefaultEmptyListIcon();
    }

    public static Texture createTexture(String path) {
        return createTexture(path, true);
    }

    public static Texture createTexture(String path, boolean putIntoCache) {
        if (textureCache == null) {
            init();
        }
        return textureCache._createTexture(path, putIntoCache);
    }

    public static Drawable getOrCreateTextureRegionDrawable(TextureRegion originalTexture) {
        Drawable drawable = drawableMap.get(originalTexture);
        if (drawable == null) {
            drawable = new TextureRegionDrawable(originalTexture);
            drawableMap.put(originalTexture, drawable);
        }

        return drawable;
    }

    public static Drawable getOrCreateTextureRegionDrawable(String imagePath) {
        return getOrCreateTextureRegionDrawable(getOrCreateR(imagePath));
    }

    public static String formatTexturePath(String path) {
        path = path.toLowerCase().replace("\\\\", "\\")
         .replace("\\", "/");
        if (path.endsWith("/"))
            return path.substring(0, path.length() - 1);
        return path;
    }

    public static TextureRegion getOrCreateSizedRegion(int iconSize, String path) {
        return new TextureRegion(GdxImageTransformer.size(path, iconSize, true));
    }

    private String getAltTexturePath(String filePath) {
        return filePath.replace(StrPathBuilder.build("gen", "entity"), StrPathBuilder.build("main"));
    }

    private Texture _getOrCreateGrayScale(String path) {
        Texture normal = _getOrCreate(path);
        if (!greyscaleCache.containsKey(normal)) {
            if (!normal.getTextureData().isPrepared()) {
                normal.getTextureData().prepare();
            }
//            Gdx2DPixmap. GDX2D_FORMAT_RGBA8888
            Pixmap pixmap2 = new Pixmap(normal.getWidth(), normal.getHeight(), Format.RGBA8888);
            Pixmap pixmap = normal.getTextureData().consumePixmap();

            for (int i = 0; i < normal.getWidth(); i++) {
                for (int j = 0; j < normal.getHeight(); j++) {
                    int rgba = pixmap.getPixel(i, j);

                    if (rgba == 0)
                        continue;
                    pixmap2.drawPixel(i, j, GreyscaleUtils.luminosity(rgba));
                }
            }

            greyscaleCache.put(normal, new Texture(pixmap2));
        }

        return greyscaleCache.get(normal);
    }

    private Texture _getOrCreate(String path) {

        path = path
         .toLowerCase();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        if (!this.cache.containsKey(path)) {
            Texture x = _createTexture(path);
            if (x != null)
                return x;
        }

        return this.cache.get(path);
    }

    public Texture _createTexture(String path) {
        return _createTexture(path, true);
    }

    public Texture _createTexture(String path, boolean putIntoCache) {
        Path p = Paths.get(imagePath, path);
        String filePath = p.toString();
        Texture t = null;
        if (checkAltTexture(filePath))
            try {
                t = new Texture(new FileHandle(getAltTexturePath(filePath)),
                 Pixmap.Format.RGBA8888, false);
                if (putIntoCache)
                    cache.put(path, t);
            } catch (Exception e) {

            }
        if (t == null)
            try {
                t = new Texture(new FileHandle(filePath), Pixmap.Format.RGBA8888, false);
                if (putIntoCache) {
                    cache.put(path, t);
                }
            } catch (Exception e) {
//                main.system.ExceptionMaster.printStackTrace(e);
                if (!cache.containsKey(getEmptyPath())) {
                    if (putIntoCache)
                        cache.put(getEmptyPath(), getEmptyTexture());
                    return getEmptyTexture();
                }
                return cache.get(getEmptyPath());

            }
        return t;
    }

    public static TextureRegion getRegion(String path, Texture texture) {
        TextureRegion region = regionCache.get(path);
        if (region==null ){
            region = new TextureRegion(texture);
            regionCache.put(path, region);
        }
        return region;
    }
}

