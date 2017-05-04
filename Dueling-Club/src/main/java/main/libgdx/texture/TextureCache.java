package main.libgdx.texture;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;
import main.system.graphics.GreyscaleUtils;
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
    private static TextureCache textureCache;
    private static Lock creationLock = new ReentrantLock();
    private static AtomicInteger counter = new AtomicInteger(0);
    private Map<String, Texture> cache;
    private Map<Texture, Texture> greyscaleCache;
    private String imagePath;
    private TextureAtlas textureAtlas;
    private Pattern pattern;

    private TextureCache() {
        this.imagePath = PathFinder.getImagePath();
        this.cache = new HashMap<>();
        this.greyscaleCache = new HashMap<>();
        textureAtlas = new TextureAtlas(imagePath + "/testAtlas.txt");
        pattern = Pattern.compile("^.*[/\\\\]([a-z _\\-0-9]*)\\..*$");
    }

    private static void init() {
        try {
            creationLock.lock();
            if (textureCache == null) {
                textureCache = new TextureCache();
            }
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

    public static TextureRegion getOrCreateR(String path) {
        if (textureCache == null) {
            init();
        }


        path = path.toLowerCase();
        final Matcher matcher = textureCache.pattern.matcher(path);


        if (matcher.matches()) {
            final String name = matcher.group(1);
            final TextureAtlas.AtlasRegion region = textureCache.textureAtlas.findRegion(name);
            if (region != null) {
                counter.incrementAndGet();
                return region;
            }
        }

        return new TextureRegion(textureCache._getOrCreate(path));
    }

    public static TextureRegion getOrCreateGrayscaleR(String path) {
        return new TextureRegion(getOrCreateGrayscale(path));
    }

    private Texture _getOrCreateGrayScale(String path) {
        Texture normal = _getOrCreate(path);
        if (!greyscaleCache.containsKey(normal)) {
            if (!normal.getTextureData().isPrepared()) {
                normal.getTextureData().prepare();
            }

            Pixmap pixmap2 = new Pixmap(normal.getWidth(), normal.getHeight(), normal.getTextureData().getFormat());
            Pixmap pixmap = normal.getTextureData().consumePixmap();

            for (int i = 0; i < normal.getWidth(); i++) {
                for (int j = 0; j < normal.getHeight(); j++) {
                    int c = pixmap.getPixel(i, j);
                    pixmap2.drawPixel(i, j, GreyscaleUtils.luminosity(c));
                }
            }

            greyscaleCache.put(normal, new Texture(pixmap2));
        }

        return greyscaleCache.get(normal);
    }

    private Texture _getOrCreate(String path) {

//        if (path.contains(imagePath)) {
//            throw new RuntimeException("fix this path: '" + path + "'");
//        }
        path = StringMaster.removePreviousPathSegments(path, imagePath);
        path = path.replace("\\", "/").toLowerCase();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        if (!this.cache.containsKey(path)) {
            try {
                Path p = Paths.get(imagePath, path);
                Texture t = new Texture(
                        new FileHandle(p.toString()), Pixmap.Format.RGBA8888, false
                );
                cache.put(path, t);
            } catch (Exception e) {
                e.printStackTrace();
                 if (cache.get(getEmptyPath()) == null){
                    Texture emptyTexture = new Texture(getEmptyPath());
                    cache.put(getEmptyPath(), emptyTexture );
                     return emptyTexture;
                }
                return cache.get(getEmptyPath());

            }
        }

        return this.cache.get(path);
    }

    private String getEmptyPath() {
        return
                ImageManager.getImageFolderPath() +
                        ImageManager.getDefaultEmptyListIcon();
    }
}

