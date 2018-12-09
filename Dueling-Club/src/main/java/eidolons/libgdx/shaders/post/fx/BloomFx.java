package eidolons.libgdx.shaders.post.fx;

import com.bitfire.postprocessing.effects.Bloom;

/**
 * Created by JustMe on 12/6/2018.
 */
public class BloomFx extends Bloom {
    private float base;
    private float fluctuation=1;

    public BloomFx(int fboWidth, int fboHeight) {
        super(fboWidth, fboHeight);
    }

    @Override
    public void applyCoef(float a) {
        this.fluctuation=a;
        setBloomIntesity(2*base*settings.bloomIntensity * a);
        setBloomSaturation(1);
        setBlurAmount(base*settings.blurAmount * a);
        setThreshold(base*settings.bloomThreshold * a);
    }

    public void setBase(float base) {
        this.base = base;
        applyCoef(fluctuation);
    }
}
