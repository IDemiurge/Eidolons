package main.libgdx.bf;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.game.bf.Coordinates;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.tooltips.ValueTooltip;

import java.util.Arrays;

public class OverlayView extends BaseView {
    public static final float SCALE = 0.5F;
    private Image image;
    private Coordinates.DIRECTION direction;

    public OverlayView(UnitViewOptions viewOptions) {
        super(viewOptions);
        image = new Image(viewOptions.getPortrateTexture());
        addActor(image);

        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(Arrays.asList(new ValueContainer(viewOptions.getName(), "")));
         addListener(tooltip.getController());
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
