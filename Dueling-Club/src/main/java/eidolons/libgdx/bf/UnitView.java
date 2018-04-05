package eidolons.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import eidolons.game.battlecraft.logic.battlefield.vision.OutlineMaster;
import eidolons.libgdx.GdxImageTransformer;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.overlays.HpBar;
import eidolons.libgdx.gui.panels.dc.InitiativePanel;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.ResourceSourceImpl;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.shaders.GrayscaleShader;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class UnitView extends BaseView {
    protected static AtomicInteger lastId = new AtomicInteger(1);
    protected  int curId;
    private  String name;
    protected int initiativeIntVal;
    protected TextureRegion clockTexture;
    protected HpBar hpBar;
    protected Label initiativeLabel;
    protected Label mainHeroLabel;
    protected Image clockImage;
    protected FadeImageContainer emblemImage;
    protected FadeImageContainer emblemBorder;
    protected FadeImageContainer modeImage;
    protected TextureRegion outline;
    protected Supplier<TextureRegion> outlineSupplier;
    protected boolean greyedOut;
    protected boolean mainHero;
    protected boolean emblemBorderOn;
    protected boolean mobilityState = true;//mobility state, temporary.
    protected boolean flickering;
    protected boolean initialized;
    protected boolean stealth;
    private Tooltip tooltip;
    private float timeTillTurn = 10;
    private float resetTimer;
    private GridUnitView parentView;

    public UnitView(UnitViewOptions o) {
        this(o, lastId.getAndIncrement());
    }

    protected UnitView(UnitViewOptions o, int curId) {
        super(o);
        this.curId = curId;
    }

    public void init(UnitViewOptions o) {
        this.mainHero = o.isMainHero();
        setTeamColor(o.getTeamColor());
        this.name = o.getName();
        init(o.getPortraitTexture(), o.getPortraitPath());
        init(o.getClockTexture(), o.getClockValue());

        addActor(this.modeImage=new FadeImageContainer());
    }


    public void reset() {


        if (isEmblemBorderOn()) {
            emblemBorder = new FadeImageContainer( (
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
        if (outline != null || isHovered()) {
            initiativeLabel.setVisible(false);
            clockImage.setVisible(false);
        } else {
            initiativeLabel.setVisible(true);
            clockImage.setVisible(true);
        }
    }

    protected void updateModeImage(String pathToImage) {
        if (StringMaster.isEmpty(  pathToImage))
        {
            ActorMaster.addFadeOutAction(modeImage, 0.5f);
            return;
        }
        modeImage.setVisible(true);
        modeImage .setImage(pathToImage);
        ActorMaster.addFadeInAction(modeImage, 0.5f);
        modeImage.setPosition(GdxMaster.top(modeImage), GdxMaster.right(modeImage));
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
            if (outline==null )
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
    }

    public LabelStyle getInitiativeFontStyle() {
        return StyleHolder.getSizedColoredLabelStyle(
         0.3f,
         StyleHolder.ALT_FONT, 16,
         getTeamColor());
    }

    @Override
    public void setVisible(boolean visible) {
        if (!visible)
            if (isVisible()){
                setDefaultTexture();
            }
        super.setVisible(visible);
    }

    @Override
    public void act(float delta) {

        super.act(delta);
        updateVisible();
        if (GdxMaster.isHpBarAttached() && !GridPanel.isHpBarsOnTop()) {
            addActor(hpBar);
        }
        if (mainHeroLabel != null) {
            if (!isActive()) {
                mainHeroLabel.setVisible(false);
                return;
            }
            mainHeroLabel.setVisible(true);
            alphaFluctuation(mainHeroLabel, delta);
        }
        if (flickering)
            if (alphaFluctuationOn)
                alphaFluctuation(this, delta/4); //TODO fix speed
//                ActorMaster.addFadeInOrOutIfNoActions(this, 5);
            else if (getColor().a == 0)
                getColor().a = 1;

        if (!(this instanceof GridUnitView)) {
            if (!getParentView().isVisible()){
                setDefaultTexture();
                return;
            }
        }
        if (!mainHero)
            if (resetTimer <= 0 || OutlineMaster.isAutoOutlinesOff()) {
                if (outlineSupplier != null && !OutlineMaster.isAutoOutlinesOff())
                    outline = outlineSupplier.get();
                if (outline != null) {
                    setPortraitTexture(outline);
                } else {
                    if (originalTextureAlt != null) {
                        setPortraitTexture(originalTextureAlt);
                    } else {

                            setPortraitTexture(originalTexture);

                    }
                }
                resetTimer = 0.2f;
            } else {
                resetTimer = resetTimer - delta;
            }

    }

    protected void setDefaultTexture() {
        outline= TextureCache.getSizedRegion(
         InitiativePanel.imageSize,
         OUTLINE_TYPE.UNKNOWN.getImagePath());
        setPortraitTexture(outline);
    }

    protected FadeImageContainer initPortrait(TextureRegion portraitTexture, String path) {
        originalTexture = processPortraitTexture(portraitTexture, path);
        if (isMainHero()){
            return new FadeImageContainer(new Image(originalTexture));
        }
        return   new FadeImageContainer(new Image(getInvisibleQueueTexture()));
    }
    private TextureRegion getInvisibleQueueTexture() {
        return TextureCache.getOrCreateR(OUTLINE_TYPE.UNKNOWN.getImagePath());
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
        if (!(this instanceof GridUnitView)) {
            return new TextureRegion(GdxImageTransformer.size(path, InitiativePanel.imageSize, true));
        }
        return texture;
    }
    public void setOutlineSupplier(Supplier<TextureRegion> outlineSupplier) {
        this.outlineSupplier = outlineSupplier;
    }

    public void setFlickering(boolean flickering) {
        this.flickering = flickering;
        getPortrait().getColor().a = 1;
        getColor().a = 1;
    }

    public void setGreyedOut(boolean greyedOut) {
        this.greyedOut = greyedOut;
        getPortrait().getColor().a = 1;
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


    public Float getTimeTillTurn() {
        return timeTillTurn;
    }

    public void setTimeTillTurn(Float timeTillTurn) {
        this.timeTillTurn = timeTillTurn;
    }

    public void setInitiativeLabelText(String initiativeLabelText) {
        initiativeLabel.setText(initiativeLabelText);

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " for " + name;
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

    public void setToolTip(Tooltip tooltip) {
        addListener(tooltip.getController());
        this.tooltip = tooltip;
    }

    public Tooltip getTooltip() {
        return tooltip;
    }

    public boolean isMainHero() {
        return mainHero;
    }

    public void setMainHero(boolean mainHero) {
        this.mainHero = mainHero;
    }

    public void setParentView(GridUnitView parentView) {
        this.parentView = parentView;
    }

    public GridUnitView getParentView() {
        return parentView;
    }
}
