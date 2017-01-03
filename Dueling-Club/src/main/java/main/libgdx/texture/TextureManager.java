package main.libgdx.texture;

import com.badlogic.gdx.graphics.Texture;
import main.data.filesys.PathFinder;
import main.system.images.ImageManager;

/**
 * Created by JustMe on 12/30/2016.
 */
public class TextureManager {
    static TextureCache cache;

    public static Texture get(String p) {
        if (ImageManager.getPATH() != null)
            if (!ImageManager.isImage(p)) {
                p =  (ImageManager.getAltEmptyListIcon());
            }
       return  getCache().getOrCreate(p);
    }

    public static TextureCache getCache() {
        if (cache==null )
            cache = new TextureCache(PathFinder.getImagePath());
        return cache;
    }
}
