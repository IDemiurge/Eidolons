package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.tooltips.UnitViewTooltip;
import eidolons.libgdx.gui.tooltips.UnitViewTooltipFactory;
import main.game.bf.Coordinates;

public class OverlayView extends BaseView {
    public static final float SCALE = 0.5F;
    private Coordinates.DIRECTION direction;

    public OverlayView(UnitViewOptions viewOptions, BattleFieldObject bfObj) {
        super(viewOptions);
        if (portrait != null)
            portrait.remove();
        portrait = new FadeImageContainer(new Image(viewOptions.getPortraitTexture()));
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
            return;
    }

    @Override
    public boolean isCachedPosition() {
        return true;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {

        return super.hit(x, y, touchable) != null ? this : null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public Coordinates.DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(Coordinates.DIRECTION direction) {
        this.direction = direction;
    }
}
