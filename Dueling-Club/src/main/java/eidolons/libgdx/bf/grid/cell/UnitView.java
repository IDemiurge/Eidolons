package eidolons.libgdx.bf.grid.cell;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.vision.advanced.OutlineMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.overlays.bar.HpBar;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.shaders.GrayscaleShader;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import main.content.CONTENT_CONSTS;
import main.content.enums.GenericEnums;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class UnitView extends BaseView implements HpBarView {
    private static final float OUTLINE_RESET_PERIOD = 0.5f;
    protected static AtomicInteger lastId = new AtomicInteger(1);
    protected int curId;
    protected String name;
    protected HpBar hpBar;
    protected Label mainHeroLabel;
    protected FadeImageContainer emblemImage;
    protected Image emblemLighting;
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
    protected Supplier<String> outlinePathSupplier;
    protected TextureRegion defaultTexture;
    protected TextureRegion defaultTextureSized;

    public UnitView(UnitViewOptions o) {
        this(o, lastId.getAndIncrement());
    }

    protected UnitView(UnitViewOptions o, int curId) {
        super(o);
        this.curId = curId;
        setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.UNIT_BORDER);
    }

    protected static String getAtlasPath() {
        return "unitview/unitview.txt";
    }

    public void init(UnitViewOptions o) {
        this.setMainHero(o.isMainHero());
        setTeamColor(o.getTeamColor());
        this.name = o.getName();
        this.flip = o.getFlip();
        init( o.getPortraitPath());
        addActor(this.modeImage = new FadeImageContainer());

        try {
            initSprite(o);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        if (o.getObj() instanceof Unit) {
            addActor(highlight = new FadeImageContainer(getActiveHighlightImgPath()));
            highlight.setVisible(false);
            GdxMaster.center(highlight);
            highlight.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.HIGHLIGHT_SPEAKER);
        }
    }

    public String getActiveHighlightImgPath() {
        return Images.COLORLESS_BORDER;
    }


    public void reset() {

    }


    protected void updateVisible() {

    }

    public void updateModeImage(String pathToImage) {
        if (StringMaster.isEmpty(pathToImage)) {

            modeImage.setImage("ui/really empty 32.png");
            if (modeImage.getContent() != null) {
//                ActorMaster.addFadeOutAction(modeImage, 0.5f);
            }
            return;
        }
        if (!ImageManager.isImage(pathToImage)) {
            return;
        }
        modeImage.setVisible(true);
        modeImage.setImage(pathToImage);
        ActionMaster.addFadeInAction(modeImage, 0.5f);
        modeImage.setPosition(GdxMaster.getTopY(modeImage), GdxMaster.right(modeImage));
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
                if (isResetOutlineOnHide())
//                if (getUserObject().isResetOutlineOnHide())
                    if (getUserObject() instanceof Unit) {
                        if (!isMainHero())
                            setPortraitTextureImmediately(TextureCache.getOrCreateR(
                                    OUTLINE_TYPE.UNKNOWN.getImagePath()));
                    }
            }
        super.setVisible(visible);
    }

    protected boolean isResetOutlineOnHide() {
        return true;
    }

    @Override
    public float getScaleX() {
        return super.getScaleX();
    }

    @Override
    protected boolean isTransformDisabled() {
        if (forceTransform) {
            return false;
        }
        return super.isTransformDisabled();
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
                resetTimer = OUTLINE_RESET_PERIOD;
            } else {
                resetTimer = resetTimer - delta;
            }
    }

    protected void setDefaultTexture() {
        if (isMainHero()) {
            return;
        }
        if (getUserObject().isWater()) {
            return;
        }
        setPortraitTexture(TextureCache.getOrCreateR(
                OUTLINE_TYPE.UNKNOWN.getImagePath()));
    }

    protected FadeImageContainer initPortrait(TextureRegion portraitTexture, String path) {
        originalTexture = processPortraitTexture(path);
        if (isMainHero()) {
            return new FadeImageContainer(new Image(originalTexture));
        }
        return new FadeImageContainer(new Image(getDefaultTexture()));
    }

    public TextureRegion getDefaultTexture() {
        if (defaultTexture == null) {
            defaultTexture = TextureCache.getOrCreateR(
                    OUTLINE_TYPE.UNKNOWN.getImagePath());
        }
        return defaultTexture;
    }


    protected boolean isHpBarVisible() {
        return true;
    }

    protected void initEmblem(
            TextureRegion emblem) {
        if (emblem != null) {
            emblemLighting = new Image(
                    UnitViewOptions.UNIT_VIEW_ATLAS
                            ? TextureCache.fromAtlas(UnitView.getAtlasPath(), "light")
                            : TextureCache.getOrCreateR(ImageManager.STD_IMAGES.LIGHT.getPath()));
            emblemLighting.setSize(getEmblemSize() * 10 / 9, getEmblemSize() * 10 / 9);
            emblemLighting.setPosition(getWidth() - emblemLighting.getWidth(), getHeight() - emblemLighting.getHeight());
            if (getTeamColor() != null)
                emblemLighting.setColor(getTeamColor());
            addActor(emblemLighting);

            emblemImage = new FadeImageContainer(new Image(emblem));
            addActor(emblemImage);
            emblemImage.setSize(getEmblemSize(), getEmblemSize());
            emblemImage.setPosition(getWidth() - emblemImage.getWidth(), getHeight() - emblemImage.getHeight());
        }
    }

    protected float getEmblemSize() {
        if (isMainHero())
            return 36;
        return 32;
    }


    public void setPortraitTexture(TextureRegion textureRegion) {
        getPortrait().setTexture(TextureCache.getOrCreateTextureRegionDrawable(textureRegion));
    }

    public void setPortraitTextureImmediately(TextureRegion textureRegion) {
        getPortrait().setContentsImmediately(
                new Image(textureRegion));
    }

    public TextureRegion processPortraitTexture(String path) {
        if (flip == CONTENT_CONSTS.FLIP.HOR) {
            return   TextureCache.getFlippedRegion(true, false, path);
        }
        if (flip == CONTENT_CONSTS.FLIP.VERT) {
            return   TextureCache.getFlippedRegion(false,true,  path);
        }
        return TextureCache.getOrCreateR(path);
    }

    public void setOutlineSupplier(Supplier<TextureRegion> outlineSupplier) {
        this.outlineSupplier = outlineSupplier;
    }


    public void setOutlinePathSupplier(Supplier<String> pathSupplier) {
        this.outlinePathSupplier = pathSupplier;
        //null means no outline!
        if (pathSupplier.get() != null)
            if (!TextureCache.isCached(pathSupplier.get())) {
                return;
            }
        this.outlineSupplier = () -> StringMaster.isEmpty(pathSupplier.get()) ? null : TextureCache.getOrCreateR(pathSupplier.get());
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

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
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
        if (getHpBar() != null)
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

    public void resetHpBar() {
        if (getHpBar() == null) {
            setHpBar(new HpBar(getUserObject()));
        }
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
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (parentAlpha == ShaderDrawer.SUPER_DRAW) {
            super.draw(batch, 1);
            return;
        }
        ShaderDrawer.drawWithCustomShader(this, batch,
                greyedOut ?
                        GrayscaleShader.getGrayscaleShader()
//          GrayscaleShader.getGrayscaleShader()
                        : null, true);
    }
}
