package main.libgdx.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.data.filesys.PathFinder;

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
    private String imagePath;

    private TextureCache() {
        this.imagePath = PathFinder.getImagePath();
        this.cache = new HashMap<>();
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

    public static Texture getOrCreate(String path) {
        if (textureCache == null) {
            init();
        }

        return textureCache._getOrCreate(path);
    }

    public static void put(Texture texture, String path) {
        if (textureCache == null) {
            init();
        }

        textureCache.cache.put(path, texture);
    }

    public static TextureRegion getOrCreateR(String path) {
        return new TextureRegion(getOrCreate(path));
    }

    private Texture _getOrCreate(String path) {
        if (!this.cache.containsKey(path)) {
            Path p = Paths.get(imagePath, path);
            Texture t = new Texture(p.toString());
            this.cache.put(path, t);
        }

        return this.cache.get(path);
    }
}

