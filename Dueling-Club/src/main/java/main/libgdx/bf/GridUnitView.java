package main.libgdx.bf;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.GdxMaster;
import main.libgdx.anims.ActorMaster;
import main.libgdx.bf.overlays.HpBar;
import main.libgdx.gui.panels.dc.InitiativePanel;
import main.libgdx.gui.panels.dc.unitinfo.datasource.ResourceSourceImpl;
import main.libgdx.gui.tooltips.ToolTip;
import main.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.images.ImageManager.STD_IMAGES;

import static main.system.GuiEventType.ADD_OR_UPDATE_INITIATIVE;

public class GridUnitView extends UnitView {
    private Image arrow;
    private Image emblemLighting;
    private Image icon;
    private int arrowRotation;
    private float alpha = 1f;

    private UnitView initiativeQueueUnitView;
    private boolean cellBackground;

    public GridUnitView(UnitViewOptions o) {
        super(o);
        init(o.getDirectionPointerTexture(), o.getDirectionValue(), o.getIconTexture(), o.getEmblem());
        cellBackground = o.cellBackground;
        initQueueView(o);
        setVisible(false);
    }

    @Override
    public void setToolTip(ToolTip toolTip) {
        super.setToolTip(toolTip);
        if (initiativeQueueUnitView != null) {
            initiativeQueueUnitView.setToolTip(toolTip);
        }
    }


    public UnitView getInitiativeQueueUnitView() {
        return initiativeQueueUnitView;
    }

    private void initQueueView(UnitViewOptions o) {
        setHoverResponsive(o.isHoverResponsive());
        initiativeQueueUnitView = new UnitView(o, curId);
        initiativeQueueUnitView.setSize(InitiativePanel.imageSize, InitiativePanel.imageSize);
        initiativeQueueUnitView.setHoverResponsive(isHoverResponsive());
    }


    @Override
    public void setBorder(TextureRegion texture) {
        super.setBorder(texture);
        if (initiativeQueueUnitView != null) {
            initiativeQueueUnitView.setBorder(texture);
        }
    }

    @Override
    public void updateInitiative(Integer val) {
//        super.updateInitiative(val);
        initiativeQueueUnitView.updateInitiative(val);
        GuiEventManager.trigger(ADD_OR_UPDATE_INITIATIVE, initiativeQueueUnitView);

    }

    @Override
    public void reset() {
        super.reset();
        if (emblemImage != null) {
            emblemImage.setPosition(getWidth() - emblemImage.getWidth(),
             getHeight() - emblemImage.getHeight());
            emblemLighting.setPosition(getWidth() - emblemLighting.getWidth(),
             getHeight() - emblemLighting.getHeight());
            if (getTeamColor() != null)
                emblemLighting.setColor(getTeamColor());

        }
        if (arrow != null)
            arrow.setPosition(getWidth() / 2 - arrow.getWidth() / 2, 0);


    }

    private void init(TextureRegion arrowTexture, int arrowRotation, Texture iconTexture, TextureRegion emblem) {

        if (arrowTexture != null) {
            arrow = new Image(arrowTexture);
            addActor(arrow);
            arrow.setOrigin(getWidth() / 2 + arrow.getWidth(), getHeight() / 2 + arrow.getHeight());
main.system.auxiliary.log.LogMaster.log(1,
 arrow.getOriginX()+" getOriginX " + arrow.getOriginY()+" getOriginY " );
            arrow.setPosition(getWidth() / 2 - arrow.getWidth() / 2, 0);
            this.arrowRotation = arrowRotation + 90;
        }

        if (iconTexture != null) {
            icon = new Image(iconTexture);
            addActor(icon);
            icon.setPosition(0, getHeight() - icon.getImageHeight());
        }

        if (emblem != null) {
            emblemLighting = new Image(TextureCache.getOrCreateR(STD_IMAGES.LIGHT.getPath()));
            emblemLighting.setSize(getEmblemSize() * 10 / 9, getEmblemSize() * 10 / 9);
            emblemLighting.setPosition(getWidth() - emblemLighting.getWidth(), getHeight() - emblemLighting.getHeight());
            if (getTeamColor() != null)
                emblemLighting.setColor(getTeamColor());
            addActor(emblemLighting);

            emblemImage = new Image(emblem);
            addActor(emblemImage);
            emblemImage.setSize(getEmblemSize(), getEmblemSize());
            emblemImage.setPosition(getWidth() - emblemImage.getWidth(), getHeight() - emblemImage.getHeight());
        }
        setInitialized(true);
    }

    private float getEmblemSize() {
        if (mainHero)
            return 36;
        return 32;
    }

    @Override
    public void setVisible(boolean visible) {
        if (this.isVisible()!=visible)
        {
            super.setVisible(visible);
            if (getParent() instanceof GridCellContainer) {
                ((GridCellContainer) getParent()).recalcUnitViewBounds();

            }
        }
    }
//    public void setVisibleVal(int val) {
//        val = Math.max(0, val);
//        val = Math.min(100, val);
//        alpha = val * 0.01f;
//        if (alpha < 1) {
//            GuiEventManager.trigger(REMOVE_FROM_INITIATIVE_PANEL, initiativeQueueUnitView);
//        } else {
//            GuiEventManager.trigger(ADD_OR_UPDATE_INITIATIVE, initiativeQueueUnitView);
//        }
//    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (alpha != 1f) {
            parentAlpha = alpha;
        }
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
//        if (isIgnored())
//            return; TODO make it work with actions
        if (getY() < 0)
            setY(0);
        if (getX() < 0)
            setX(0);
        if (emblemLighting != null)
//            if (isActive() || isHovered())
            alphaFluctuation(emblemLighting, delta);
//            else
//                emblemLighting.setColor(getTeamColor());
        super.act(delta);
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        if (initiativeQueueUnitView != null)
            initiativeQueueUnitView.setActive(active);
    }

    public void updateRotation(int val) {
        if (arrow != null) {

            ActorMaster.addRotateByAction(arrow, arrowRotation, val % 360 + 90);
            arrowRotation = val + 90;
//            arrow.setRotation(arrowRotation);
        }
    }

    public boolean isHpBarVisible() {
        if (!getHpBar().getDataSource().canHpBarBeVisible() )
            return false;
        if (!isCellBackground()&& getHpAlwaysVisible())
            return true;
        if (!getHpBar().getDataSource().isHpBarVisible() )
            return false;


        return true;
    }

    protected void updateVisible() {
        if (isIgnored())
            return;
        if (outline != null) {
            if (emblemImage != null)
                emblemImage.setVisible(false);
            if (modeImage != null)
                modeImage.setVisible(false);
            if (arrow != null)
                arrow.setVisible(false);
            if (getHpBar() != null)
                getHpBar().setVisible(false);

        } else {
            if (emblemImage != null)
                emblemImage.setVisible(true);
            if (modeImage != null)
                modeImage.setVisible(true);
            if (arrow != null)
                arrow.setVisible(true);
            if (getHpBar() != null)
                getHpBar().setVisible(isHpBarVisible());
        }
    }

    @Override
    public float getWidth() {
        if (super.getWidth() == 0)
            return GridConst.CELL_W;
        return super.getWidth();
    }

    @Override
    public float getHeight() {
        if (super.getHeight() == 0)
            return GridConst.CELL_H;
        return super.getHeight();
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();

        if (icon != null) {
            icon.setPosition(0, getHeight() - icon.getImageHeight());
        }

        if (arrow != null) {
            arrow.setOrigin(arrow.getWidth() / 2, getHeight() / 2);
            arrow.setX(getWidth() / 2 - arrow.getWidth() / 2);
            arrow.setRotation(arrowRotation);
        }

//        main.system.auxiliary.log.LogMaster.log(1, this + " Scale=" + getScaledWidth());
        if (getScaledWidth() == 0)
            return;
        if (getScaledHeight() == 0)
            return;
        Image image = clockImage;
        if (image != null) {
            image.setScaleX(getScaledWidth());
            image.setScaleY(getScaledHeight());
        }
        image = emblemImage;
        if (image != null) {
            image.setScaleX(getScaledWidth());
            image.setScaleY(getScaledHeight());
        }
//        image = arrow;
//        if (image != null) {
//            image.setScaleX(getScaledWidth());
//            image.setScaleY(getScaledHeight());
//        }
    }


    @Override
    public void setMobilityState(boolean mobilityState) {
        super.setMobilityState(mobilityState);
        if (initiativeQueueUnitView != null) {
            initiativeQueueUnitView.setMobilityState(mobilityState);
            GuiEventManager.trigger(ADD_OR_UPDATE_INITIATIVE, initiativeQueueUnitView);
        }
    }

/*    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && this.getTouchable() != Touchable.enabled) {
            return null;
        }
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? this : null;
    }*/

    public int getId() {
        return curId;
    }

    public boolean isCellBackground() {
        return cellBackground;
    }

    public void resetHpBar(ResourceSourceImpl resourceSource) {
        super.resetHpBar(resourceSource);
        if (initiativeQueueUnitView!=null )
        initiativeQueueUnitView.resetHpBar(
         resourceSource  );
    }

    @Override
    public void setHpBar(HpBar hpBar) {
        super.setHpBar(hpBar);
        hpBar.setPosition(GdxMaster.centerWidth(hpBar),-hpBar.getHeight()/2);
    }

    @Override
    public void setTeamColor(Color teamColor) {
        super.setTeamColor(teamColor);
        if (initiativeQueueUnitView!=null )
            initiativeQueueUnitView.setTeamColor(teamColor);
    }

    @Override
    public void setTeamColorBorder(boolean teamColorBorder) {
        super.setTeamColorBorder(teamColorBorder);
        if (initiativeQueueUnitView!=null )
            initiativeQueueUnitView.setTeamColorBorder(teamColorBorder);
    }

    public void createHpBar(ResourceSourceImpl resourceSource) {
         setHpBar(new HpBar(resourceSource));
         if (initiativeQueueUnitView!=null )
        initiativeQueueUnitView.setHpBar(new HpBar(resourceSource));
    }

    public void animateHpBarChange() {
        if (!getHpBar().isVisible())
            return ;

        getHpBar().animateChange();
        if (initiativeQueueUnitView!=null )
        initiativeQueueUnitView. getHpBar().animateChange();
    }
}
