package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.module.dungeoncrawl.objects.KeyMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.overlays.HpBar;
import eidolons.libgdx.gui.tooltips.UnitViewTooltip;
import eidolons.libgdx.gui.tooltips.UnitViewTooltipFactory;
import main.game.bf.directions.DIRECTION;

public class OverlayView extends BaseView implements HpBarView{
    public static final float SCALE = 0.5F;
    private DIRECTION direction;
    HpBar hpBar;
    public void resetHpBar( ) {
        if (getHpBar() == null)
            setHpBar(createHpBar());
        getHpBar().reset();
    }

    protected HpBar createHpBar() {
        HpBar bar = new HpBar(getUserObject());
        return bar;
    }

    public HpBar getHpBar() {
        return hpBar;
    }

    @Override
    public BattleFieldObject getUserObject() {
        return (BattleFieldObject) super.getUserObject();
    }

    public void setHpBar(HpBar hpBar) {
        if (this.hpBar != null) {
            this.hpBar.remove();
        }
        this.hpBar = hpBar;
        hpBar.setVisible(false);
        hpBar.setScale(0.66f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (GdxMaster.isHpBarAttached() && !GridMaster.isHpBarsOnTop()) {
            addActor(hpBar);
        }
    }

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
        tooltip.setUserObject(UnitViewTooltipFactory.getSupplier(bfObj));
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

    public DIRECTION getDirection() {
        return direction;
    }

    public void setDirection(DIRECTION direction) {
        this.direction = direction;
    }

    public float getScale() {
      if( KeyMaster.isKey(getUserObject()) ){
            return 0.66f;
        }
        return OverlayView.SCALE;
    }
}
