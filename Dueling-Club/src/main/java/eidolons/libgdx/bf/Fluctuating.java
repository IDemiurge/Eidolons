package eidolons.libgdx.bf;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.gui.generic.GroupX;
import main.system.auxiliary.RandomWizard;
import main.system.math.MathMaster;

/**
 * Created by JustMe on 12/3/2018.
 */
public class Fluctuating extends GroupX {
    public static final float DEFAULT_ALPHA_FLUCTUATION = 0.4f;
    public static final float DEFAULT_ALPHA_MIN = 0.2f;
    public static final float DEFAULT_ALPHA_MAX = 1f;
    protected static boolean alphaFluctuationOn = true;
    protected float fluctuatingAlpha = 1f;
    protected boolean alphaGrowing = false;
    protected float baseAlpha = 1; //real alpha fluctuates around this value
    protected float alphaPause;
    protected float alphaStep = -1;
    protected float fluctuatingAlphaPauseDuration;
    protected float fluctuatingFullAlphaDuration;
    protected float fluctuatingAlphaRandomness;
    protected float fluctuatingAlphaMin;
    protected float fluctuatingAlphaMax;
    ALPHA_TEMPLATE alphaTemplate;

    public Fluctuating() {
    }

    public Fluctuating(ALPHA_TEMPLATE alphaTemplate) {
        setAlphaTemplate(alphaTemplate);
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

    public void fluctuate(float delta) {
        alphaFluctuation(delta);
    }

    public void act(float delta) {
        super.act(delta);
    }

    public void setFluctuatingAlphaMin(float fluctuatingAlphaMin) {
        this.fluctuatingAlphaMin = fluctuatingAlphaMin;
    }

    public void setFluctuatingAlphaMax(float fluctuatingAlphaMax) {
        this.fluctuatingAlphaMax = fluctuatingAlphaMax;
    }

    protected void alphaFluctuation(float delta) {
        alphaFluctuation(this, delta);
    }

    protected void alphaFluctuation(Actor image, float delta) {
        if (!isAlphaFluctuationOn())
            return;
        if (alphaPause > 0) {
            alphaPause = alphaPause - delta;
            if (alphaPause >= 0)
                return;
            delta = delta - (-alphaPause);
            if (delta <= 0)
                return;
        }
        Color color = getDefaultColor(image);

        fluctuatingAlpha = fluctuatingAlpha + randomizeAlpha(getAlphaFluctuation(delta));

        fluctuatingAlpha = MathMaster.getMinMax(
         fluctuatingAlpha, getAlphaFluctuationMin(),
         getAlphaFluctuationMax());
        if (image != null) //TODO control access!
            image.setColor(color.r, color.g, color.b, fluctuatingAlpha);

    }

    protected Color getDefaultColor(Actor image) {
        if (image != null)
            return image.getColor();
        return GdxColorMaster.WHITE;
    }

    protected float randomizeAlpha(float fluctuatingAlpha) {

        if (fluctuatingAlphaRandomness > 0) {
            //            if (RandomWizard.chance())
            return fluctuatingAlpha * (1 + RandomWizard.getRandomFloatBetween(
             -fluctuatingAlphaRandomness, fluctuatingAlphaRandomness));
        }
        return fluctuatingAlpha;
    }

    protected float getAlphaFluctuation(float delta) {
        float fluctuation = delta * getAlphaFluctuationPerDelta();
        if (getFluctuatingAlpha() <= getAlphaFluctuationMin()) {
            alphaGrowing = true;
            alphaPause = getFluctuatingAlphaPauseDuration() * (
             1 + (RandomWizard.getRandomFloatBetween(
              -fluctuatingAlphaRandomness, fluctuatingAlphaRandomness)) / 2);
        } else if (fluctuatingAlpha >= getAlphaFluctuationMax()) {
            alphaGrowing = false;
            alphaPause = getFluctuatingFullAlphaDuration() * (
             1 + (RandomWizard.getRandomFloatBetween(
              -fluctuatingAlphaRandomness, fluctuatingAlphaRandomness)) / 2);
        }

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
            return fluctuatingAlphaMin * baseAlpha;
        return DEFAULT_ALPHA_MIN;
    }

    protected float getAlphaFluctuationMax() {
        if (fluctuatingAlphaMax != 0)
            return fluctuatingAlphaMax * baseAlpha;
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
        Fluctuating.alphaFluctuationOn = alphaFluctuationOn;
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

    public float getBaseAlpha() {
        return baseAlpha;
    }

    public void setBaseAlpha(float baseAlpha) {
        this.baseAlpha = baseAlpha;

    }

    public enum ALPHA_TEMPLATE {

        MOON(0.1f, 0, 1, 0.5f),
        SUN(0.1f, 0, 5, 0.5f, 0.7f, 1f),
        TOP_LAYER(0.2f, 1, 2, 0.6f, 0.15f, 0.5f),
        LIGHT(0.28f, 4, 0.8f, 2.6f, 0.1f, 0.4f),

        MOONLIGHT(0.4f, 5, 0.5F, 0.6f, 0.1f, 0.9f),
        CLOUD(0.2f, 0, 2, 0.2f, 0.05f, 1f),
        HIGHLIGHT(0.15f, 0, 1, 0.1f, 0.15f, 1f),
        HIGHLIGHT_MAP(0.1f, 0, 1, 0.4f, 0.75f, 1f),

        SHARD_OVERLAY(0.325f, 0.25F, 0.5F, 0.5f, 0.75f, 1f),
        ITEM_BACKGROUND_OVERLAY(0.15f, 0, 1.25f, 0.6f, 0.70f, 1f),

        VIGNETTE(0.1f, 1, 0, 0.3f, 0.4f, 1f),
        ATB_POS(0.4f, 0, 0.5F, 0.2f, 0.6f, 1f),
        OVERLAYS(0.15f, 0, 1, 0.1f, 0.75f, 1f),
        UNIT_VIEW(0.23f, 0, 1, 0.0f, 0.80f, 1f), //EMBLEM COLOR & UNCONSCIOUS

        SHADE_CELL_GAMMA_SHADOW(0.05f, 0.5f, 0.2f, 0, 0.5f, 0.8f),
        SHADE_CELL_GAMMA_LIGHT(0.08f, 1.5f, 2.55f, 0.2f, 0.4f, 0.85f),
        SHADE_CELL_LIGHT_EMITTER(0.10f, 1.5f, 2.5f, 0.2f, 0.85f, 1),
        LIGHT_EMITTER_RAYS(0.25f, 1.0f, 0.5f, 0.4f, 0.25f, 1.0f),

        SHADE_CELL_HIGHLIGHT(0.4f, 1.5f, 0.3f, 0.4f, 0.15f, 1),
        DOORS(0.325f, 1.25F, 0.5F, 0.5f, 0.0f, 1f),

        BLOOM(0.1f, 0F, 0.0F, 0.88f, 0.3f, 1f),
        POST_PROCESS(0.1f, 0F, 0.0F, 0.88f, 0.3f, 1f),;
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
