package libgdx.shaders.post.fx;

import com.bitfire.postprocessing.effects.Bloom;

/**
 * Created by JustMe on 12/6/2018.
 */
public class BloomFx extends Bloom {
    private float base;
    private float fluctuation = 1;
    boolean noDarken;
    boolean noDesat;
    boolean noBlur;

    public BloomFx(int fboWidth, int fboHeight) {
        super(fboWidth, fboHeight);
    }

    public BloomFx(int fboWidth, int fboHeight, boolean noDarken, boolean noBlur) {
        super(fboWidth, fboHeight);
        this.noDarken = noDarken;
        this.noBlur = noBlur;
    }

    @Override
    public void applyCoef(float a) {
        this.fluctuation = a;
        setBloomIntesity(2 * base * settings.bloomIntensity * a);
        setBaseIntesity(1.5f);
        setBloomSaturation(1);

        if (noBlur) {
            blur.setAmount(0);
        } else {
            setBlurAmount(base * settings.blurAmount * a);
        }

        if (noDarken) {
            threshold.setTreshold(1.00f);
//            threshold.setParams(Threshold.Param.Threshold, gamma);
        } else {
            setThreshold(base * settings.bloomThreshold * a);
        }
    }

    public void setBase(float base) {
        this.base = base;
        applyCoef(fluctuation);
    }
}
