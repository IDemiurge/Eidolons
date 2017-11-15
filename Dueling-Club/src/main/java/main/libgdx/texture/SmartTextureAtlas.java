package main.libgdx.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Created by JustMe on 11/15/2017.
 */
public class SmartTextureAtlas extends TextureAtlas {
    public SmartTextureAtlas(String s) {
        super(s);
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
        public AtlasRegion addRegion(String name, Texture texture, int x, int y, int width, int height) {
        return super.addRegion(name.toLowerCase(), texture, x, y, width, height);
    }
}
