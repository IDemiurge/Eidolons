package main.libgdx.texture;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;
import main.system.graphics.GreyscaleUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TextureCache {
    private static TextureCache textureCache;
    private static Lock creationLock = new ReentrantLock();
    private Map<String, Texture> cache;
    private Map<Texture, Texture> greyscaleCache;
    private String imagePath;

    private TextureCache() {
        this.imagePath = PathFinder.getImagePath();
        this.cache = new HashMap<>();
        this.greyscaleCache = new HashMap<>();
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
        return new TextureRegion(getOrCreate(path));
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


        path = StringMaster.removePreviousPathSegments(path, imagePath);
        if (!this.cache.containsKey(path)) {
            Path p = null;
            try {
                p = Paths.get(imagePath, path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Texture t = null;
            try {
                t = new Texture(p.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
//                return _getOrCreate(ImageManager.getEmptyItemIconPath(false));
            }


            this.cache.put(path, t);
        }

        return this.cache.get(path);
    }
}

