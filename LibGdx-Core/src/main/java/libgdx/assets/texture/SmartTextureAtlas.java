package libgdx.assets.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import libgdx.assets.AssetEnums;
import main.system.PathUtils;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.log.LogMaster;

import java.util.HashMap;
import java.util.Map;

import static main.system.auxiliary.log.LogMaster.important;
import static main.system.auxiliary.log.LogMaster.log;

/**
 * Created by JustMe on 11/15/2017.
 */
public class SmartTextureAtlas extends TextureAtlas {
    private final Map<String, AtlasRegion> cache = new HashMap<>();
    private String path;
    private AssetEnums.ATLAS type;

    public static boolean isCached() {
        return false;
    }

    @Override
    public Sprite createSprite(String name, int index) {
        return super.createSprite(name, index);
    }

    public SmartTextureAtlas(String path) {
        super(path);
        this.path = path;
        LogMaster.devLog("SmartTextureAtlas created: " + path);
    }

    public SmartTextureAtlas(TextureAtlasData data) {
        super(data);
    }

    public String getPath() {
        return path;
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
                sprite = super.createSprite(StringMaster.cropAfter(name, "_") + format);
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

    public AtlasRegion findRegion(String orig, boolean recursion) {
        AtlasRegion region = (isCached() ? cache.get(orig) : null);
        if (region != null) {
            return region;
        }
        String name = FileManager.formatPath(orig, true, true);
        Array<AtlasRegion> regions = getRegions();

        for (int i = 0, n = regions.size; i < n; i++) {
            String s = regions.get(i).name;
            // if (Flags.isJar()) {
            //     s = FileManager.formatPath(s, true, true);
            // }
            if (s.equalsIgnoreCase(name)) {
                region = regions.get(i);
                if (isCached()) {
                    cache.put(orig, region);
                }
                return region;
            }
        }

        // if (!recursion) {
        //     name = formatFileName(name);
        //     important(type + " - Recursion for region: " + name);
        //     return findRegion(name, true);
        // } else

        log(type + " - Atlas region not found: " + name);
        return null;
    }

    private String formatFileName(String name) {
        String numericSuffix = NumberUtils.getNumericSuffix(StringMaster.cropFormat(name));
        if (numericSuffix.isEmpty()) {
            return name;
        }
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

        important(type + " - " + name + "[***] PATH CHANGED Atlas region =>  " + croppedName);
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

    public void setType(AssetEnums.ATLAS type) {
        this.type = type;
    }

    public AssetEnums.ATLAS getType() {
        return type;
    }
}
