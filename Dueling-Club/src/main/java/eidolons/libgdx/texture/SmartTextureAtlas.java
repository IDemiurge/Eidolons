package eidolons.libgdx.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import main.system.PathUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 11/15/2017.
 */
public class SmartTextureAtlas extends TextureAtlas {
    private static final Map<String, SmartTextureAtlas> cache = new HashMap<>();
    private String path;

    @Override
    public Sprite createSprite(String name, int index) {
        return super.createSprite(name, index);
    }

    public SmartTextureAtlas(String path) {
        super(path);
        this.path = path;
        main.system.auxiliary.log.LogMaster.devLog("SmartTextureAtlas created: " + path);
    }

    public SmartTextureAtlas(TextureAtlasData data) {
        super(data);
    }

    public static SmartTextureAtlas getAtlas(String path) {
        SmartTextureAtlas atlas =
                cache.get(path.toLowerCase());
        if (atlas != null)
            return atlas;
        atlas = new SmartTextureAtlas(path);
        cache.put(path.toLowerCase(), atlas);
        return atlas;
    }

    public String getPath() {
        return path;
    }

    public static void clearCache() {
        cache.clear();
    }

    @Override
    public AtlasRegion addRegion(String name, TextureRegion textureRegion) {
        return super.addRegion(name.toLowerCase(), textureRegion);
    }

    @Override
    public Array<AtlasRegion> findRegions(String name) {
        Array<AtlasRegion> matched = new Array();
        for (int i = 0, n = getRegions().size; i < n; i++) {
            AtlasRegion region = getRegions().get(i);
            if (region.name.equalsIgnoreCase(name)) matched.add(new AtlasRegion(region));
        }
        return matched;
    }

    public Array<AtlasRegion> findRegionsClosest(String name) {
        Array<AtlasRegion> matched = new Array();
        name = name.toLowerCase();
        for (int i = 0, n = getRegions().size; i < n; i++) {
            AtlasRegion region = getRegions().get(i);
            if (region.name.toLowerCase().contains(name)) matched.add(new AtlasRegion(region));
        }
        return matched;
    }

    @Override
    public Sprite createSprite(String name) {
        Sprite sprite = super.createSprite(name);
        if (sprite == null) {
            String format = StringMaster.getFormat(name);
            name = StringMaster.cropFormat(name);
            String last = StringMaster.getLastPart(name, "_");
            if (NumberUtils.isInteger(last)) {
                sprite = super.createSprite(StringMaster.cropLast(name, "_") + format);
            }
            if (sprite == null) {
                sprite = super.createSprite(StringMaster.getStringBeforeNumerals(name) + format);
            }
        }
        return sprite;
    }

    @Override
    public AtlasRegion findRegion(String name) {
        return findRegion(name, false);
    }

    public AtlasRegion findRegion(String name, boolean recursion) {
        name = FileManager.formatPath(name, true, true);
        Array<AtlasRegion> regions = getRegions();
        for (int i = 0, n = regions.size; i < n; i++)
            if (FileManager.formatPath(regions.get(i).name, true, true)
                    .equals(name)) return regions.get(i);

        if (!recursion) {
            name = formatFileName(name);
            return findRegion(name, true);
        }
        return null;
    }

    private String formatFileName(String name) {
        String numericSuffix = NumberUtils.getNumericSuffix(StringMaster.cropFormat(name));
        String croppedName = name.replace("_" + numericSuffix, "");
        if (croppedName.equals(name)) {
            croppedName = name.replace(" " + numericSuffix, "");
        }
        if (croppedName.equals(name)) {
            croppedName = name.replace(numericSuffix, "");
        }
        if (croppedName.equals(name)) {
           return name;
        }
        return croppedName;
    }

    @Override
    public AtlasRegion addRegion(String name, Texture texture, int x, int y, int width, int height) {
        return super.addRegion(name.toLowerCase(), texture, x, y, width, height);
    }

    public AtlasRegion findRegionFromFullPath(String texturePath) {
        return findRegion(StringMaster.cropFormat(PathUtils.getLastPathSegment(texturePath)));
    }

    public void setPath(String fileName) {
        this.path = fileName;
    }
}
