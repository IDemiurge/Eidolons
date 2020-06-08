package eidolons.libgdx.gui.generic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import eidolons.libgdx.texture.TextureCache;

/**
 * Created by JustMe on 10/14/2018.
 */
public class NoHitImage extends Image {
    private final int w;
    private final int h;

    public NoHitImage(TextureRegion region) {
        super(region);
        w = region.getRegionWidth();
        h = region.getRegionWidth();
    }

    public NoHitImage(Texture texture) {
        super(texture);
        w = texture.getWidth();
        h = texture.getHeight();
    }


    public NoHitImage(String path) {
        this(TextureCache.getOrCreate(path));
    }

    public NoHitImage(float scale, Drawable drawable) {
        super(drawable);
        w = (int) (drawable.getMinWidth() * scale);
        h = (int) (drawable.getMinHeight() * scale);
    }

    @Override
    public float getWidth() {
        return w;
    }

    @Override
    public float getHeight() {
        return h;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
    }
}
