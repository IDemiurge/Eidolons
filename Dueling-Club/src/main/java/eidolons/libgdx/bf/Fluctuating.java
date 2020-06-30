package eidolons.libgdx.bf;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.utils.GdxTimeMaster;
import main.content.enums.GenericEnums;
import main.system.auxiliary.RandomWizard;
import main.system.launch.Flags;
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
    protected GenericEnums.ALPHA_TEMPLATE alphaTemplate;
    private int fluctuatingAlphaPeriod;
    public static int fluctuatingAlphaPeriodGlobal = 1;

    static {
        if (Flags.isMainGame()) {
            fluctuatingAlphaPeriodGlobal =1;
//            OptionsMaster.getGraphicsOptions().
//                    getIntValue(GraphicsOptions.GRAPHIC_OPTION.PERFORMANCE_BOOST) / 10 + 1;
        }
    }

    public Fluctuating() {
    }

    public Fluctuating(GenericEnums.ALPHA_TEMPLATE alphaTemplate) {
        setAlphaTemplate(alphaTemplate);
    }

    public void setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE alphaTemplate) {

        this.alphaTemplate = alphaTemplate;
        this.alphaStep = alphaTemplate.alphaStep;
        this.fluctuatingAlphaMax = alphaTemplate.max;
        this.fluctuatingAlphaMin = alphaTemplate.min;
        this.fluctuatingAlphaPauseDuration = alphaTemplate.fluctuatingAlphaPauseDuration;
        this.fluctuatingFullAlphaDuration = alphaTemplate.fluctuatingFullAlphaDuration;
        this.fluctuatingAlphaRandomness = alphaTemplate.fluctuatingAlphaRandomness;
        alphaFluctuationOn = true;
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
        int period =1;// getFluctuatingAlphaPeriod() + 1;
        if (period != 1) {
            if (!GdxTimeMaster.isPeriodNow(period)) {
                return;
            }
        }
        Color color = getDefaultColor(image);

        fluctuatingAlpha = fluctuatingAlpha + randomizeAlpha(getAlphaFluctuation(period * delta));

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

    public int getFluctuatingAlphaPeriod() {
        return fluctuatingAlphaPeriodGlobal;
    }

    public void setFluctuatingAlphaPeriod(int fluctuatingAlphaPeriod) {
        this.fluctuatingAlphaPeriod = fluctuatingAlphaPeriod;
    }

}
