package eidolons.libgdx.shaders.post.fx;

import com.bitfire.postprocessing.effects.CustomPostEffect;
import com.bitfire.postprocessing.filters.RadialBlur;
import com.bitfire.postprocessing.filters.RadialBlur.Quality;

/**
 * Created by JustMe on 12/3/2018.
 */
public class BlurFx extends CustomPostEffect {


    private   final RadialBlur blur;
    private float base;

    public BlurFx() {
        blur= new RadialBlur(Quality.High);
        this.filter = blur;
        base = blur.getStrength();
    }

    public void setBase(float base) {
        this.base = base;
    }

    @Override
    public void applyCoef(float a) {
        if (isSquareFluctuate())
            blur.setStrength(base/10*a*a);
        else
            blur.setStrength(base/12*a);
    }

    private boolean isSquareFluctuate() {
        return true;
    }
}
