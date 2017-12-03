package main.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.GdxMaster;
import main.libgdx.StyleHolder;
import main.libgdx.anims.ActorMaster;
import main.libgdx.bf.overlays.HpBar;
import main.libgdx.gui.panels.dc.InitiativePanel;
import main.libgdx.gui.panels.dc.unitinfo.datasource.ResourceSourceImpl;
import main.libgdx.gui.tooltips.ToolTip;
import main.libgdx.shaders.GrayscaleShader;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.log.LogMaster;
import main.system.options.GameplayOptions.GAMEPLAY_OPTION;
import main.system.options.OptionsMaster;

import java.util.concurrent.atomic.AtomicInteger;

public class UnitView extends BaseView {
    protected static AtomicInteger lastId = new AtomicInteger(1);
    private static Boolean hpAlwaysVisible;
    protected final int curId;
    private final String name;
    protected int initiativeIntVal;
    protected TextureRegion clockTexture;
    protected HpBar hpBar;
    protected Label initiativeLabel;
    protected Label mainHeroLabel;
    protected Image clockImage;
    protected Image emblemImage;
    protected Image emblemBorder;
    protected Image modeImage;
    protected TextureRegion outline;
    protected boolean greyedOut;
    protected boolean mainHero;
    protected boolean emblemBorderOn;
    protected boolean mobilityState = true;//mobility state, temporary.
    protected boolean flickering;
    protected boolean initialized;
    protected boolean stealth;
    private ToolTip tooltip;

    public UnitView(UnitViewOptions o) {
        this(o, lastId.getAndIncrement());
    }

    protected UnitView(UnitViewOptions o, int curId) {
        super(o);
        this.mainHero = o.isMainHero();
        this.curId = curId;
        setTeamColor(o.getTeamColor());
        init(o.getClockTexture(), o.getClockValue());
        this.name = o.getName();
    }

    public static Boolean getHpAlwaysVisible() {
        if (hpAlwaysVisible == null) {
            hpAlwaysVisible = OptionsMaster.getGameplayOptions().getBooleanValue(GAMEPLAY_OPTION.HP_BARS_ALWAYS_VISIBLE);
        }
        return hpAlwaysVisible;
    }

    @Override
    public String toString() {
     return     getClass().getSimpleName() + " for " +name;
    }

    public HpBar getHpBar() {
        return hpBar;
    }

    public void setHpBar(HpBar hpBar) {
        if (this.hpBar != null) {
            this.hpBar.remove();
        }
        this.hpBar = hpBar;
        if (!GridPanel.isHpBarsOnTop() || !(this instanceof GridUnitView)) {
            addActor(hpBar);
            hpBar.setPosition(GdxMaster.centerWidth(hpBar), -hpBar.getHeight() / 2);
        }
        if ((this instanceof GridUnitView))
            hpBar.setVisible(false);
        else
            hpBar.setQueue(true);
    }

    @Override
    public void setHovered(boolean hovered) {
        super.setHovered(hovered);
        getHpBar().setLabelsDisplayed(hovered);
    }

    public void setToolTip(ToolTip toolTip) {
        addListener(toolTip.getController());
        this.tooltip = toolTip;
    }

    public ToolTip getTooltip() {
        return tooltip;
    }

    public void reset() {


        if (isEmblemBorderOn()) {
            emblemBorder = new Image(TextureCache.getOrCreateR(
             CellBorderManager.teamcolorPath));
            emblemBorder.setBounds(emblemImage.getX() - 6, emblemImage.getY() - 6
             , this.emblemImage.getWidth() + 10, this.emblemImage.getHeight() + 10);
            addActor(emblemBorder);
        }
    }

    protected void init(TextureRegion clockTexture, int clockVal) {
        this.initiativeIntVal = clockVal;
        if (!(this instanceof GridUnitView))
            if (clockTexture != null) {
                this.clockTexture = clockTexture;
                initiativeLabel = new Label(
                 String.valueOf(clockVal),
                 getInitiativeFontStyle());
                clockImage = new Image(clockTexture);
                addActor(clockImage);
                addActor(initiativeLabel);
            }

        if (!(this instanceof GridUnitView))
            if (mainHero) {
                mainHeroLabel = new Label("Your Turn!", StyleHolder.getSizedLabelStyle(StyleHolder.DEFAULT_FONT, 20));
                addActor(clockImage);
                mainHeroLabel.setPosition(0, GdxMaster.top(mainHeroLabel));
            }
        setInitialized(true);
    }

    protected void updateVisible() {
    }

    protected void updateModeImage(String pathToImage) {
        removeActor(modeImage);
        if (pathToImage == null)
            return;
        modeImage = new Image(TextureCache.getOrCreateR(pathToImage));
        addActor(this.modeImage);
        modeImage.setVisible(true);
        modeImage.setPosition(0, 0);
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
        if (!GridPanel.isHpBarsOnTop() || !(this instanceof GridUnitView))
            if (hpBar != null)
                hpBar.setPosition(GdxMaster.centerWidth(hpBar), -hpBar.getHeight() / 2);
        if (isInitialized())
            reset();
    }

    public void updateInitiative(Integer val) {
        if (clockTexture != null) {
            initiativeIntVal = val;
            initiativeLabel.setText(String.valueOf(val));
            initiativeLabel.setStyle(
             getInitiativeFontStyle());
            initiativeLabel.setPosition(
             clockImage.getX() + (clockTexture.getRegionWidth() / 2 - initiativeLabel.getWidth() / 2),
             clockImage.getY() + (clockTexture.getRegionHeight() / 2 - initiativeLabel.getHeight() / 2));
        } else {
            LogMaster.error("Initiative set to wrong object type != OBJ_TYPES.UNITS");
        }
    }

    public LabelStyle getInitiativeFontStyle() {
        return StyleHolder.getSizedColoredLabelStyle(
         0.3f,
         StyleHolder.ALT_FONT, 16,
        getTeamColor());
    }

    @Override
    public void act(float delta) {

        super.act(delta);
        updateVisible();
        if (mainHeroLabel != null) {
            if (!isActive()) {
//                new MapMaster<>()
//                 .getKeyForValue(DungeonScreen.getInstance().getGridPanel().getUnitMap())
//                getCurId()

                mainHeroLabel.setVisible(false);
                return;
            }
            if (!OptionsMaster.getGameplayOptions().
             getBooleanValue(GAMEPLAY_OPTION.MANUAL_CONTROL))
                return;
            mainHeroLabel.setVisible(true);
            alphaFluctuation(mainHeroLabel, delta);
        }
//        alphaFluctuation(emblemBorder, delta);


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
        if (flickering)
            if (SuperActor.alphaFluctuationOn) //TODO fix
                ActorMaster.addFadeInOrOutIfNoActions(this, 5);
            else if (getColor().a == 0)
                getColor().a = 1;
        if (outline != null) {
//            batch.draw(outline, getX(), getY());
            getPortrait().setDrawable(new TextureRegionDrawable(outline));
        } else {
            if (originalTextureAlt != null) {
                getPortrait().setDrawable(TextureCache.getOrCreateTextureRegionDrawable(originalTextureAlt));
            } else {
                {
                    getPortrait().setDrawable(TextureCache.getOrCreateTextureRegionDrawable(originalTexture));
                }
            }
        }
        super.draw(batch, parentAlpha);

        if (batch.getShader() == GrayscaleShader.getGrayscaleShader())
            batch.setShader(shader);

        if (!(this instanceof GridUnitView)) {
//            getHpBar().drawAt(batch, 0, 0);
            return;
        }
    }

    public void setFlickering(boolean flickering) {
        this.flickering = flickering;
//        if (!flickering) {
        getPortrait().getColor().a = 1;
        getColor().a = 1;
    }

    public void setGreyedOut(boolean greyedOut) {
        this.greyedOut = greyedOut;
        getPortrait().getColor().a = 1;
//        if (getColor().a ==0)
        getColor().a = 1;

    }


    public int getCurId() {
        return curId;
    }

    public int getInitiativeIntVal() {
        return initiativeIntVal;
    }


    public boolean getMobilityState() {
        return mobilityState;
    }

    public void setMobilityState(boolean mobilityState) {
        this.mobilityState = mobilityState;
    }

    public boolean isEmblemBorderOn() {
        return emblemBorderOn;
    }

    public void setEmblemBorderOn(boolean emblemBorderOn) {
        this.emblemBorderOn = emblemBorderOn;
    }

    public TextureRegion getOutline() {
        return outline;
    }

    public void setOutline(TextureRegion outline) {
        this.outline = outline;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public void resetHpBar(ResourceSourceImpl resourceSource) {
        if (getHpBar() == null)
            hpBar = new HpBar((resourceSource));
        getHpBar().reset(resourceSource);
    }

    @Override
    public void setBorder(TextureRegion texture) {
        super.setBorder(texture);
        if (getHpBar() == null)
            getHpBar().setZIndex(Integer.MAX_VALUE);
    }
}
