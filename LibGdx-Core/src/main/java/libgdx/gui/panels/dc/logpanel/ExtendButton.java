package libgdx.gui.panels.dc.logpanel;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import libgdx.texture.TextureCache;
import libgdx.gui.generic.btn.ButtonStyled;
import main.system.auxiliary.StrPathBuilder;

public class ExtendButton extends Group {
    private final static String extendButtonImagePath =
      new StrPathBuilder().build_(ButtonStyled.STD_BUTTON.PULL.getPath());

    public ExtendButton() {
        Image back = new Image(TextureCache.getOrCreateR(extendButtonImagePath));
        setHeight(back.getHeight());
        setWidth(back.getWidth());
        addActor(back);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return super.hit(x, y, touchable) != null ? this : null;
    }
}
