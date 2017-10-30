package main.libgdx.bf;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.entity.obj.BattleFieldObject;
import main.game.bf.Coordinates;

public class OverlayView extends BaseView {
    public static final float SCALE = 0.5F;
    private Coordinates.DIRECTION direction;

    public OverlayView(UnitViewOptions viewOptions, BattleFieldObject bfObj) {
        super(viewOptions);
        if (portrait != null)
        portrait.remove();
        portrait = new Image(viewOptions.getPortrateTexture());
        addActor(portrait);

//        ValueTooltip tooltip = new ValueTooltip();
//        tooltip.setUserObject(Arrays.asList(new ValueContainer(viewOptions.getName(), "")));
//         addListener(tooltip.getController());

        final UnitViewTooltip tooltip = new UnitViewTooltip(this);
        tooltip.setUserObject(UnitViewTooltipFactory.create(bfObj));
        addListener(tooltip.getController());
        addListener(UnitViewFactory.createListener(bfObj));
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible)
            return ;
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
