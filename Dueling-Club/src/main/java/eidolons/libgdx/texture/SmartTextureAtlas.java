package eidolons.libgdx.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 11/15/2017.
 */
public class SmartTextureAtlas extends TextureAtlas {
    private static Map<String, SmartTextureAtlas> cache = new HashMap<>();


    public SmartTextureAtlas(String s) {
        super(s);
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

    @Override
    public Sprite createSprite(String name) {
        Sprite sprite = super.createSprite(name);
        if (sprite == null) {
            String format = StringMaster.getFormat(name);
            name = StringMaster.cropFormat(name);
            String last = StringMaster.getLastPart(name, "_");
            if (NumberUtils.isInteger(last))
            {
                sprite = super.createSprite(StringMaster.cropLast(name, "_")+format);
            }
            if (sprite == null)
            {
                sprite = super.createSprite(StringMaster.getStringBeforeNumerals(name) + format);
            }
        }
        return sprite;
    }

    @Override
    public AtlasRegion addRegion(String name, Texture texture, int x, int y, int width, int height) {
        return super.addRegion(name.toLowerCase(), texture, x, y, width, height);
    }
}
