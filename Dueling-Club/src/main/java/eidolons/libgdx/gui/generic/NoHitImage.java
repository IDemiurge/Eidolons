package eidolons.libgdx.gui.generic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by JustMe on 10/14/2018.
 */
public class NoHitImage extends Image {
    public NoHitImage(TextureRegion region) {
        super(region);
    }

    public NoHitImage(Texture texture) {
        super(texture);
    }

    public NoHitImage(Drawable drawable) {
        super(drawable);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
    }
}
