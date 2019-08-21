package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.bf.overlays.HpBar;
import eidolons.libgdx.gui.panels.dc.atb.AtbPanel;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.gui.tooltips.UnitViewTooltipFactory;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.log.LogMaster;

import java.util.function.Supplier;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;
import static main.system.GuiEventType.ADD_OR_UPDATE_INITIATIVE;

/**
 * Created by JustMe on 4/6/2018.
 */
public class QueueView extends UnitView {
    protected int initiativeIntVal;
    protected Label initiativeLabel;
    protected ImageContainer clockImage;
    protected boolean queueMoving = true;//queueMoving, temporary. 
    protected GridUnitView parentView;

    protected QueueView(UnitViewOptions o, int curId) {
        super(o, curId);
        init(o.getClockValue());
    }

    @Override
    protected void initSprite(UnitViewOptions o) {
        //TODO could do it of course , but..
    }

    @Override
    public BattleFieldObject getUserObject() {
        return parentView.getUserObject();
    }

    protected void init(int clockVal) {
        this.initiativeIntVal = clockVal;
        initiativeLabel = new Label(
                String.valueOf(clockVal),
                getInitiativeFontStyle());
        clockImage = new ImageContainer(PathFinder.getComponentsPath()
                + "dc/atb/readiness bg.png");
        addActor(clockImage);
        addActor(initiativeLabel);

        addListener(new SmartClickListener(this) {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (getUserObject().isDetectedByPlayer()) {
                    getUserObject().getGame().getManager().setHighlightedObj(getUserObject());
//                    parentView.setHovered(true);
//                    parentView.setBorder(true);
                } else
                    GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP,
                            UnitViewTooltipFactory.create((UnitView) event.getListenerActor(), getUserObject()));
                super.enter(event, x, y, pointer, fromActor);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                getUserObject().getGame().getManager().setHighlightedObj(null);
                super.exit(event, x, y, pointer, toActor);
            }
        });
        setInitialized(true);
    }

    protected void updateVisible() {
        if (getOutline() != null || isHovered()) {
            initiativeLabel.setVisible(false);
            clockImage.setVisible(false);
        } else {
            initiativeLabel.setVisible(true);
            clockImage.setVisible(true);
        }
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();

        if (initiativeLabel != null) {
            if (AtbPanel.isLeftToRight())
                clockImage.setPosition(GdxMaster.right(clockImage) + 6, -5);
            initiativeLabel.setPosition(
                    clockImage.getX() +
                            (clockImage.getWidth() / 2 - initiativeLabel.getWidth() * 1.23f),
                    clockImage.getY() +
                            (clockImage.getHeight() / 2 - initiativeLabel.getHeight() / 2));
        }
        if (!GridMaster.isHpBarsOnTop())
            if (hpBar != null)
                hpBar.setPosition(GdxMaster.centerWidth(hpBar), -hpBar.getHeight() / 2);
        if (isInitialized())
            reset();

        //        Image image = clockImage;
        //        if (image != null) {
        //            image.setScaleX(getScaledWidth());
        //            image.setScaleY(getScaledHeight());
        //        }
    }

    public int getInitiativeIntVal() {
        return initiativeIntVal;
    }

    public boolean isQueueMoving() {
        return queueMoving;
    }

    public void setQueueMoving(boolean queueMoving) {
        this.queueMoving = queueMoving;
        GuiEventManager.trigger(ADD_OR_UPDATE_INITIATIVE, this);
    }

    public void updateInitiative(Integer val) {
        initiativeIntVal = val;
        if (getOutline() == null)
            initiativeLabel.setText(String.valueOf(val));
        else
            initiativeLabel.setText("?");

        initiativeLabel.setStyle(
                getInitiativeFontStyle());
        initiativeLabel.setPosition(
                clockImage.getX() + (clockImage.getWidth() / 2 - initiativeLabel.getWidth() / 2),
                clockImage.getY() + (clockImage.getHeight() / 2 - initiativeLabel.getHeight() / 2));

        GuiEventManager.trigger(ADD_OR_UPDATE_INITIATIVE, this);
    }

    public LabelStyle getInitiativeFontStyle() {
        return StyleHolder.getSizedColoredLabelStyle(
                0.3f,
                StyleHolder.ALT_FONT, 16,
                getTeamColor());
    }

    protected void checkResetOutline(float delta) {
        if (!getParentView().isVisible()) {
            setDefaultTexture();
            return;
        }
        super.checkResetOutline(delta);
    }

    protected void setDefaultTexture() {
        if (getUserObject() == null) {
            return;
        }
        if (isMainHero()) {
            return;
        }
        if (getUserObject().isWater()) {
            return;
        }
        setOutline(TextureCache.getSizedRegion(
                AtbPanel.imageSize,
                OUTLINE_TYPE.UNKNOWN.getImagePath()));
        setPortraitTexture(getOutline());
    }

    protected boolean isHpBarVisible() {
        return true;
    }

    //    @Override
    //    public void draw(Batch batch, float parentAlpha) {
    //        if (parentAlpha== ShaderMaster.SUPER_DRAW)
    //            super.draw(batch, 1);
    //        else
    //        ShaderMaster.drawWithCustomShader(this, batch,
    //         greyedOut ? GrayscaleShader.getGrayscaleShader() : null, true);
    //    }

    protected void setPortraitTexture(TextureRegion textureRegion) {
        getPortrait().setTexture(TextureCache.getOrCreateTextureRegionDrawable(textureRegion));
    }

    protected TextureRegion processPortraitTexture(TextureRegion region, String path) {
        Texture texture = GdxImageMaster.size(path, AtbPanel.imageSize, true);
        if (texture == null)
            return region;
        return new TextureRegion(texture);
    }

    public void setOutlineSupplier(Supplier<TextureRegion> outlineSupplier) {
        this.outlineSupplier = outlineSupplier;
    }

    public void setHpBar(HpBar hpBar) {
        if (this.hpBar != null) {
            this.hpBar.remove();
        }
        this.hpBar = hpBar;
        //        if (!GridPanel.isHpBarsOnTop()) {
        addActor(hpBar);
        hpBar.setPosition(GdxMaster.centerWidth(hpBar), -hpBar.getHeight() / 2);
        //        }
        hpBar.setQueue(true);
    }

    @Override
    public void setBorder(TextureRegion texture) {
        super.setBorder(texture);
        if (getHpBar() == null)
            getHpBar().setZIndex(Integer.MAX_VALUE);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " for " + name;
    }

    public GridUnitView getParentView() {
        return parentView;
    }

    public void setParentView(GridUnitView parentView) {
        this.parentView = parentView;
    }

    public void setInitiativeLabelText(String text) {
        initiativeLabel.setText(text);
    }
}

