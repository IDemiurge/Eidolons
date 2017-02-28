package main.libgdx.gui.panels.dc;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.texture.TextureCache;

public class ExtendButton extends Group {
    private final static String extendButtonImagePath = "UI\\components\\extend_button.png";

    public ExtendButton() {
        Image back = new Image(TextureCache.getOrCreate(extendButtonImagePath));
        setHeight(back.getHeight());
        setWidth(back.getWidth());
        addActor(back);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {

        return super.hit(x, y, touchable) != null ? this : null;
    }
}
