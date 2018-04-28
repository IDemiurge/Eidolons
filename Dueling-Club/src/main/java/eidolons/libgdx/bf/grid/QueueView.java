package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import eidolons.libgdx.GdxImageTransformer;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.overlays.HpBar;
import eidolons.libgdx.gui.panels.dc.InitiativePanel;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.ResourceSourceImpl;
import eidolons.libgdx.shaders.GrayscaleShader;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.system.GuiEventManager;
import main.system.auxiliary.log.LogMaster;

import java.util.function.Supplier;

import static main.system.GuiEventType.ADD_OR_UPDATE_INITIATIVE;

/**
 * Created by JustMe on 4/6/2018.
 */
public class QueueView extends UnitView {
    protected int initiativeIntVal;
    protected TextureRegion clockTexture; 
    protected Label initiativeLabel; 
    protected Image clockImage; 
    protected boolean queueMoving = true;//queueMoving, temporary. 
     protected GridUnitView parentView;
    
    protected QueueView(UnitViewOptions o, int curId) {
        super(o, curId);
        init(o.getClockTexture(), o.getClockValue());
    }
  

    protected void init(TextureRegion clockTexture, int clockVal) {
        this.initiativeIntVal = clockVal;
        if (clockTexture != null) {
            this.clockTexture = clockTexture;
            initiativeLabel = new Label(
             String.valueOf(clockVal),
             getInitiativeFontStyle());
            clockImage = new Image(clockTexture);
            addActor(clockImage);
            addActor(initiativeLabel);
        }

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
            if (InitiativePanel.isLeftToRight())
                clockImage.setPosition(GdxMaster.right(clockImage), 0);
            initiativeLabel.setPosition(
             clockImage.getX() + (clockTexture.getRegionWidth() / 2 - initiativeLabel.getWidth()),
             clockImage.getY() + (clockTexture.getRegionHeight() / 2 - initiativeLabel.getHeight() / 2));
        }
        if (!GridPanel.isHpBarsOnTop())
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

    public void updateInitiative(Integer val) {
        if (clockTexture != null) {
            initiativeIntVal = val;
            if (getOutline() == null)
                initiativeLabel.setText(String.valueOf(val));
            else
                initiativeLabel.setText("?");

            initiativeLabel.setStyle(
             getInitiativeFontStyle());
            initiativeLabel.setPosition(
             clockImage.getX() + (clockTexture.getRegionWidth() / 2 - initiativeLabel.getWidth() / 2),
             clockImage.getY() + (clockTexture.getRegionHeight() / 2 - initiativeLabel.getHeight() / 2));
        } else {
            LogMaster.error("Initiative set to wrong object type != OBJ_TYPES.UNITS");
        }
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
        setOutline(TextureCache.getSizedRegion(
         InitiativePanel.imageSize,
         OUTLINE_TYPE.UNKNOWN.getImagePath()));
        setPortraitTexture(getOutline());
    }


    protected boolean isHpBarVisible() {
        return true;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        ShaderProgram shader = null;

        if (isVisible()) {
            if (greyedOut) {
                shader = batch.getShader();
                batch.setShader(GrayscaleShader.getGrayscaleShader());
            }
        }


        super.draw(batch, parentAlpha);

        if (batch.getShader() == GrayscaleShader.getGrayscaleShader())
            batch.setShader(shader);

    }

    protected void setPortraitTexture(TextureRegion textureRegion) {
        getPortrait().setTexture(TextureCache.getOrCreateTextureRegionDrawable(textureRegion));
    }

    protected TextureRegion processPortraitTexture(TextureRegion texture, String path) {
        return new TextureRegion(GdxImageTransformer.size(path, InitiativePanel.imageSize, true));
    }

    public void setOutlineSupplier(Supplier<TextureRegion> outlineSupplier) {
        this.outlineSupplier = outlineSupplier;
    }


    public void resetHpBar(ResourceSourceImpl resourceSource) {
        if (getHpBar() == null)
            hpBar = new HpBar((resourceSource));
        getHpBar().reset(resourceSource);
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

    public void setQueueMoving(boolean queueMoving) {
        this.queueMoving = queueMoving;
        GuiEventManager.trigger(ADD_OR_UPDATE_INITIATIVE, this);
    }

    public GridUnitView getParentView() {
        return parentView;
    }

    public void setInitiativeLabelText(String text) {
        initiativeLabel.setText(text);
    }

    public void setParentView(GridUnitView parentView) {
        this.parentView = parentView;
    }
}

