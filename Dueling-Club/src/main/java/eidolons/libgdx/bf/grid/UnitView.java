package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.vision.OutlineMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.overlays.HpBar;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.shaders.DarkGrayscaleShader;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class UnitView extends BaseView implements HpBarView{
    protected static AtomicInteger lastId = new AtomicInteger(1);
    protected int curId;
    protected String name;
    protected HpBar hpBar;
    protected Label mainHeroLabel;
    protected FadeImageContainer emblemImage;
    protected FadeImageContainer modeImage;
    protected TextureRegion outline;
    protected Supplier<TextureRegion> outlineSupplier;
    protected boolean greyedOut;
    protected boolean mainHero;
    protected boolean flickering;
    protected boolean initialized;
    protected Tooltip tooltip;
    protected float timeTillTurn = 10;
    protected float resetTimer;

    public UnitView(UnitViewOptions o) {
        this(o, lastId.getAndIncrement());
    }

    protected UnitView(UnitViewOptions o, int curId) {
        super(o);
        this.curId = curId;
        setAlphaTemplate(ALPHA_TEMPLATE.UNIT_VIEW);
    }

    public void init(UnitViewOptions o) {
        this.setMainHero(o.isMainHero());
        setTeamColor(o.getTeamColor());
        this.name = o.getName();
        init(o.getPortraitTexture(), o.getPortraitPath());
        addActor(this.modeImage = new FadeImageContainer());
    }


    public void reset() {

    }


    protected void updateVisible() {

    }

    protected void updateModeImage(String pathToImage) {
        if (StringMaster.isEmpty(pathToImage)) {
            if (modeImage.getContent() != null)
                ActorMaster.addFadeOutAction(modeImage, 0.5f);
            return;
        }
        if (!ImageManager.isImage(pathToImage)) {
            return;
        }
        modeImage.setVisible(true);
        modeImage.setImage(pathToImage);
        ActorMaster.addFadeInAction(modeImage, 0.5f);
        modeImage.setPosition(GdxMaster.top(modeImage), GdxMaster.right(modeImage));
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        if (isInitialized())
            reset();
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
            if (isVisible()) {
                setDefaultTexture();
            }
        super.setVisible(visible);
    }

    @Override
    public void act(float delta) {

        super.act(delta);
        updateVisible();
        if (GdxMaster.isHpBarAttached() && !GridMaster.isHpBarsOnTop()) {
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
                alphaFluctuation(this, delta / 4); //TODO fix speed
//                ActorMaster.addFadeInOrOutIfNoActions(this, 5);
            else if (getColor().a == 0)
                getColor().a = 1;

        checkResetOutline(delta);

    }

    protected void checkResetOutline(float delta) {
        if (!isMainHero())
            if (resetTimer <= 0 || OutlineMaster.isAutoOutlinesOff()) {

                if (outlineSupplier != null && !OutlineMaster.isAutoOutlinesOff())
                    setOutline(outlineSupplier.get());
                if (getOutline() != null) {
                    setPortraitTexture(getOutline());
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
        if (isMainHero()) {
            return;
        }
        setPortraitTexture(TextureCache.getOrCreateR(
         OUTLINE_TYPE.UNKNOWN.getImagePath()));
    }

    protected FadeImageContainer initPortrait(TextureRegion portraitTexture, String path) {
        originalTexture = processPortraitTexture(portraitTexture, path);
        if (isMainHero()) {
            return new FadeImageContainer(new Image(originalTexture));
        }
        return new FadeImageContainer(new Image(getDefaultTexture()));
    }

    protected TextureRegion getDefaultTexture() {
        return TextureCache.getOrCreateR(OUTLINE_TYPE.UNKNOWN.getImagePath());
    }


    protected boolean isHpBarVisible() {
        return true;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (parentAlpha== ShaderDrawer.SUPER_DRAW)
        {
            super.draw(batch, 1);
            return;
        }
        ShaderDrawer.drawWithCustomShader(this, batch,
         greyedOut ?
          DarkGrayscaleShader.getShader_()
//          GrayscaleShader.getGrayscaleShader()
          : null, true);
    }

    protected void setPortraitTexture(TextureRegion textureRegion) {
        getPortrait().setTexture(TextureCache.getOrCreateTextureRegionDrawable(textureRegion));
    }

    protected TextureRegion processPortraitTexture(TextureRegion texture, String path) {
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

    @Override
    public BattleFieldObject getUserObject() {
        return (BattleFieldObject) super.getUserObject();
    }
    public int getCurId() {
        return curId;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + " for " + name;
    }

    public void resetHpBar( ) {
        if (getHpBar() == null)
            hpBar = new HpBar(getUserObject());
        getHpBar().reset();
    }
    public HpBar getHpBar() {
        return hpBar;
    }

    public void setHpBar(HpBar hpBar) {
        if (this.hpBar != null) {
            this.hpBar.remove();
        }
        this.hpBar = hpBar;
        hpBar.setVisible(false);
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



    public TextureRegion getOutline() {
        return outline;
    }

    public void setOutline(TextureRegion outline) {
        this.outline = outline;
    }

    public void setMainHero(boolean mainHero) {
        this.mainHero = mainHero;
    }
}
