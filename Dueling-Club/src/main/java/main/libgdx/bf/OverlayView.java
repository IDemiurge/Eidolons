package main.libgdx.bf;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.game.bf.Coordinates;

public class OverlayView extends BaseView {
    public static final float SCALE = 0.5F;
    private Image image;
    private Coordinates.DIRECTION direction;

    public OverlayView(UnitViewOptions viewOptions) {
        super(viewOptions);
        image = new Image(viewOptions.getPortrateTexture());
        addActor(image);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {

        return super.hit(x, y, touchable) != null ? this : null;
    }

    public Coordinates.DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(Coordinates.DIRECTION direction) {
        this.direction = direction;
    }
}
