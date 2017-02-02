package main.libgdx.texture;

import com.badlogic.gdx.graphics.Texture;
import main.system.auxiliary.StringMaster;

import java.util.HashMap;
import java.util.Map;

public class TextureCache {
    private Map<String, Texture> cache;
    private String imagePath;


    public TextureCache(String imagePath) {
        this.imagePath = imagePath;
        this.cache = new HashMap<>();
    }

    public final Texture getOrCreate(String path) {
        return this.get(path, true);
    }

    public final Texture get(String path) {
        return this.get(path, false);
    }

    public final Texture create(String path) {
        return new Texture( StringMaster.addMissingPathSegments(path, this.imagePath));
    }
        public final Texture get(String path, boolean save) {
            Texture t = create(path);
//        String p =
//                StringMaster.addMissingPathSegments(path, this.imagePath);
        if (!this.cache.containsKey(path)) {

            if (!save) {
                return t;
            }

            this.cache.put(path, t);
        }

        return this.cache.get(path);
    }
}

