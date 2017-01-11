package main.libgdx.bf;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class OverlayView extends BaseView {
    private Image image;

    public OverlayView(UnitViewOptions viewOptions) {
        super(viewOptions);
        image = new Image(viewOptions.getPortrateTexture());
        addActor(image);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return super.hit(x, y, touchable) != null ? this : null;
    }
}
