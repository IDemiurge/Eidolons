package eidolons.libgdx.bf;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.bf.mouse.InputController;
import eidolons.libgdx.gui.panels.GroupX;
import main.system.auxiliary.RandomWizard;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 8/17/2017.
 */
public abstract class SuperActor extends GroupX implements Borderable {
    public static final float DEFAULT_ALPHA_FLUCTUATION = 0.4f;
    public static final float DEFAULT_ALPHA_MIN = 0.2f;
    public static final float DEFAULT_ALPHA_MAX = 1f;
    protected static boolean alphaFluctuationOn = true;
    private static boolean cullingOff;
    protected Image border = null;
    protected TextureRegion borderTexture;
    protected float fluctuatingAlpha = 1f;
    protected boolean alphaGrowing = false;
    protected boolean teamColorBorder;
    protected Color teamColor;
    protected float scaledWidth = 1;
    protected float scaledHeight = 1;
    protected boolean hovered;
    protected boolean active;
    ALPHA_TEMPLATE alphaTemplate;
    private float alphaPause;
    private boolean hoverResponsive;
    private float alphaStep = -1;
    private float fluctuatingAlphaPauseDuration;
    private float fluctuatingFullAlphaDuration;
    private float fluctuatingAlphaRandomness, fluctuatingAlphaMin, fluctuatingAlphaMax;
    private Boolean withinCamera;

    public SuperActor() {
    }

    public SuperActor(ALPHA_TEMPLATE alphaTemplate) {
        setAlphaTemplate(alphaTemplate);
    }

    public static boolean isCullingOff() {
        return cullingOff;
    }

    public static void setCullingOff(boolean cullingOff) {
        SuperActor.cullingOff = cullingOff;
    }

    public void setAlphaTemplate(ALPHA_TEMPLATE alphaTemplate) {

        this.alphaTemplate = alphaTemplate;
        this.alphaStep = alphaTemplate.alphaStep;
        this.fluctuatingAlphaMax = alphaTemplate.max;
        this.fluctuatingAlphaMin = alphaTemplate.min;
        this.fluctuatingAlphaPauseDuration = alphaTemplate.fluctuatingAlphaPauseDuration;
        this.fluctuatingFullAlphaDuration = alphaTemplate.fluctuatingFullAlphaDuration;
        this.fluctuatingAlphaRandomness = alphaTemplate.fluctuatingAlphaRandomness;

    }

    public void setFluctuatingAlphaMin(float fluctuatingAlphaMin) {
        this.fluctuatingAlphaMin = fluctuatingAlphaMin;
    }

    public void setFluctuatingAlphaMax(float fluctuatingAlphaMax) {
        this.fluctuatingAlphaMax = fluctuatingAlphaMax;
    }

    @Override
    public TextureRegion getBorder() {
        return borderTexture;
    }

    @Override
    public void setBorder(TextureRegion texture) {
        if (border != null) {
            removeActor(border);
        }
        alphaGrowing = false;
        fluctuatingAlpha = 0.75f;

        if (texture == null) {
            border = null;
            borderTexture = null;
            setTeamColorBorder(false);
        } else {
            addActor(border = new Image(texture));
            borderTexture = texture;
            updateBorderSize();
        }
    }

    protected void updateBorderSize() {
        if (border != null) {
            border.setX(-6);
            border.setY(-6);
            if (getHeight() == 0)
                return;
            if (getWidth() == 0)
                return;
            border.setHeight(getHeight() + 12);
            border.setWidth(getWidth() + 12);
        }
    }

    protected boolean isIgnored() {
        if (!isVisible())
            return true;
//        if (getColor().a == 0)
//            return true; TODO why was this here?.. not to draw?
        if (isCullingOff())
            return false;
        return !isWithinCamera();
    }

    public boolean isWithinCamera() {
        if (withinCamera != null)
            return withinCamera;
        if (isCachedPosition()) {
            withinCamera = getController().isWithinCamera(this);
            getController().addCachedPositionActor(this);
            return withinCamera;
        }
        return getController().isWithinCamera(this);
    }

    public void cameraMoved() {
        this.withinCamera = null;
    }

    public boolean isCachedPosition() {
        return false;
    }

    public InputController getController() {
        return Eidolons.getScreen().controller;
    }

    @Override
    public void act(float delta) {
//        if (isIgnored())
//            return;
        if (isTransform()) {
            if (isTransformDisabled())
                setTransform(false);
        } else {
            if (!isTransformDisabled())
                setTransform(true);
        }
        super.act(delta);
        alphaFluctuation(delta);
    }

    protected boolean isTransformDisabled() {
        if (getScaleX() != 1) {
            return false;
        }
        return !(getScaleY() != 1);
    }

    protected void alphaFluctuation(float delta) {
        alphaFluctuation(border, delta);
    }

    protected void alphaFluctuation(Actor image, float delta) {
        if (!isAlphaFluctuationOn())
            return;
        if (alphaPause > 0) {
            alphaPause = alphaPause - delta;
            if (alphaPause >= 0) return;
            delta = delta - (-alphaPause);
            if (delta <= 0)
                return;
        }
        Color color = null;
        if (image != null)
            color = image.getColor();
        else color = GdxColorMaster.WHITE;

        if (isTeamColorBorder())
            if (getTeamColor() != null) {
                color = getTeamColor();
            }
        fluctuatingAlpha = fluctuatingAlpha + randomizeAlpha(getAlphaFluctuation(delta));

        fluctuatingAlpha = MathMaster.getMinMax(
         fluctuatingAlpha, getAlphaFluctuationMin(),
         getAlphaFluctuationMax());
        if (image != null) //TODO control access!
            image.setColor(color.r, color.g, color.b, fluctuatingAlpha);

    }

    private float randomizeAlpha(float fluctuatingAlpha) {
        if (fluctuatingAlphaRandomness > 0) {
//            if (RandomWizard.chance())
            int alpha = (int) (fluctuatingAlphaRandomness * 100);
            int mod =
             RandomWizard.getRandomIntBetween(
              -alpha, alpha);
            return
             fluctuatingAlpha +
              fluctuatingAlpha / 100 * mod;
        }
        return fluctuatingAlpha;
    }

    protected float getAlphaFluctuation(float delta) {
        float fluctuation = delta * getAlphaFluctuationPerDelta();
        if (getFluctuatingAlpha() <= getAlphaFluctuationMin()) {
            if (alphaGrowing) {
                alphaPause = getFluctuatingFullAlphaDuration();
            } else {
                alphaPause = getFluctuatingAlphaPauseDuration();
            }
            alphaGrowing = !alphaGrowing;
        } else if (fluctuatingAlpha >= getAlphaFluctuationMax())
            alphaGrowing = !alphaGrowing;

        if (!alphaGrowing)
            return -fluctuation;
        return fluctuation;
    }

    protected float getFluctuatingAlpha() {
        return fluctuatingAlpha;
    }

    public void setFluctuatingAlpha(float fluctuatingAlpha) {
        this.fluctuatingAlpha = fluctuatingAlpha;
    }

    protected float getAlphaFluctuationMin() {
        if (fluctuatingAlphaMin != 0)
            return fluctuatingAlphaMin;
        return DEFAULT_ALPHA_MIN;
    }

    protected float getAlphaFluctuationMax() {
        if (fluctuatingAlphaMax != 0)
            return fluctuatingAlphaMax;
        return DEFAULT_ALPHA_MAX;
    }

    protected float getAlphaFluctuationPerDelta() {
        if (alphaStep != -1)
            return alphaStep;
        return DEFAULT_ALPHA_FLUCTUATION;
    }

    public boolean isAlphaFluctuationOn() {
        return alphaFluctuationOn;
    }

    public static void setAlphaFluctuationOn(boolean alphaFluctuationOn) {
        SuperActor.alphaFluctuationOn = alphaFluctuationOn;
    }

    @Override
    public boolean isTeamColorBorder() {
        return teamColorBorder;
    }

    @Override
    public void setTeamColorBorder(boolean teamColorBorder) {
        this.teamColorBorder = teamColorBorder;
    }

    @Override
    public Color getTeamColor() {
        return teamColor;
    }

    @Override
    public void setTeamColor(Color teamColor) {
        this.teamColor = teamColor;
    }

    public float getScaledWidth() {
        return scaledWidth;
    }

    public void setScaledWidth(float scaledWidth) {
        this.scaledWidth = scaledWidth;
    }

    public float getScaledHeight() {
        return scaledHeight;
    }

    public void setScaledHeight(float scaledHeight) {
        this.scaledHeight = scaledHeight;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public boolean isActive() {
        if (active)
            return active;
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isHoverResponsive() {
        return hoverResponsive;
    }

    public void setHoverResponsive(boolean hoverResponsive) {
        this.hoverResponsive = hoverResponsive;
    }

    public float getAlphaStep() {
        return alphaStep;
    }

    public void setAlphaStep(float alphaStep) {
        this.alphaStep = alphaStep;
    }

    public float getFluctuatingAlphaPauseDuration() {
        return fluctuatingAlphaPauseDuration;
    }

    public void setFluctuatingAlphaPauseDuration(float fluctuatingAlphaPauseDuration) {
        this.fluctuatingAlphaPauseDuration = fluctuatingAlphaPauseDuration;
    }

    public float getFluctuatingFullAlphaDuration() {
        return fluctuatingFullAlphaDuration;
    }

    public void setFluctuatingFullAlphaDuration(float fluctuatingFullAlphaDuration) {
        this.fluctuatingFullAlphaDuration = fluctuatingFullAlphaDuration;
    }

    public float getFluctuatingAlphaRandomness() {
        return fluctuatingAlphaRandomness;
    }

    public void setFluctuatingAlphaRandomness(float fluctuatingAlphaRandomness) {
        this.fluctuatingAlphaRandomness = fluctuatingAlphaRandomness;
    }

    public enum ALPHA_TEMPLATE {
        MOON(0.1f, 0, 1, 0.5f),
        SUN(0.1f, 0, 5, 0.5f, 0.7f, 1f),
        TOP_LAYER(0.2f, 1, 2, 0.6f, 0.15f, 0.5f),
        LIGHT(0.24f, 3, 1, 2.6f, 0.1f, 0.5f),
        MOONLIGHT(0.4f, 5, 0.5F, 0.6f, 0.1f, 0.9f),
        CLOUD(0.2f, 0, 2, 0.2f, 0.05f, 1f),
        HIGHLIGHT(0.15f, 0, 1, 0.1f, 0.15f, 1f),
        HIGHLIGHT_MAP(0.1f, 0, 1, 0.4f, 0.75f, 1f),
        VIGNETTE(0.1f, 1, 0, 0.3f, 0.4f, 1f),
        ATB_POS(0.4f, 0, 0.5F, 0.2f, 0.6f, 1f),;
        float alphaStep;
        float fluctuatingAlphaPauseDuration;
        float fluctuatingFullAlphaDuration;
        float fluctuatingAlphaRandomness;
        float min, max;

        ALPHA_TEMPLATE(float alphaStep, float fluctuatingAlphaPauseDuration, float fluctuatingFullAlphaDuration, float fluctuatingAlphaRandomness, float min, float max) {
            this.alphaStep = alphaStep;
            this.fluctuatingAlphaPauseDuration = fluctuatingAlphaPauseDuration;
            this.fluctuatingFullAlphaDuration = fluctuatingFullAlphaDuration;
            this.fluctuatingAlphaRandomness = fluctuatingAlphaRandomness;
            this.min = min;
            this.max = max;
        }

        ALPHA_TEMPLATE(float alphaStep, float fluctuatingAlphaPauseDuration,
                       float fluctuatingFullAlphaDuration,
                       float fluctuatingAlphaRandomness) {
            this.alphaStep = alphaStep;
            this.fluctuatingAlphaPauseDuration = fluctuatingAlphaPauseDuration;
            this.fluctuatingFullAlphaDuration = fluctuatingFullAlphaDuration;
            this.fluctuatingAlphaRandomness = fluctuatingAlphaRandomness;
        }

    }
}
