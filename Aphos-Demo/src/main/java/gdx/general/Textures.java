package gdx.general;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.content.consts.libgdx.GdxStringUtils;
import libgdx.GdxImageMaster;
import libgdx.assets.texture.TextureCache;
import main.data.filesys.PathFinder;

import static main.system.auxiliary.log.LogMaster.log;

public class Textures {
    private static final ObjectMap<String, TextureRegion> regionCache = new ObjectMap<>(100);
    private final ObjectMap<String, Texture> cache = new ObjectMap<>(100);
    private static Textures instance;

    public static void init() {
        if (instance == null) {
            instance = new Textures();
        }
    }

    public static TextureRegion getOrCreateR(String path) {
        return instance.getOrCreateR_(path);
    }
    public static Texture getOrCreateTexture(String path) {
        return instance.getOrCreateTexture_(path);
    }
    public static final  String format(String path) {
        return path.toLowerCase();
    }

    ///////////// MAIN METHOD //////////////
    public TextureRegion getOrCreateR_(String path) {
        if (path == null) {
            log(1, "EMPTY TEXTURE REGION REQUEST!");
//            path=missingTexture;
        }
        TextureRegion region = regionCache.get(format(path));

        if (region != null) {
            return region;
        }

        region = new TextureRegion(getOrCreateTexture(path));
        regionCache.put(format(path), region);
        return region;
    }

    public Texture getOrCreateTexture_(String path) {
        Texture texture=cache.get(format(path));
        if (texture == null) {
            path=GdxStringUtils.appendImagePath(path);
            texture = new Texture( path);
            cache.put(format(path), texture);
        }
        return texture;
    }

}
