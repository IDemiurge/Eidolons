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

    public static Texture getOrCreate(String path) {
        if (textureCache == null) {
            try {
                creationLock.lock();
                if (textureCache == null) {
                    textureCache = new TextureCache();
                }
            } finally {
                creationLock.unlock();
            }
        }

        return textureCache._getOrCreate(path);
    }

    public static TextureRegion getOrCreateR(String path) {
        return new TextureRegion(getOrCreate(path));
    }

    private Texture _getOrCreate(String path) {
        Path p = Paths.get(imagePath, path);
        if (!this.cache.containsKey(path)) {
            Texture t = new Texture(p.toString());
            this.cache.put(path, t);
        }

        return this.cache.get(path);
    }
}

