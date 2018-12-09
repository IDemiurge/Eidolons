package eidolons.libgdx.shaders.post.fx;

import com.bitfire.postprocessing.effects.Bloom;

/**
 * Created by JustMe on 12/6/2018.
 */
public class SaturateFx extends Bloom{
    private float baseSaturation=1;

    public SaturateFx(int fboWidth, int fboHeight) {
        super(fboWidth, fboHeight);
        setBaseIntesity(1);
        setBloomIntesity(0);
    }

    @Override
    protected boolean isSaturateOnly() {
        return true;
    }

    public void setBase(float v) {
//        if (v==0)
//            baseSaturation=1;
//        else
//            baseSaturation=1/(v*10);
//if (v<0){
//    v = 1 / -v;
//}
        baseSaturation=v;
         setBaseSaturation(v);
    }

    @Override
    public void applyCoef(float a) {
        if (baseSaturation==1) {
            setBaseSaturation(1);
        } else
        setBaseSaturation(baseSaturation*a);
        setBloomIntesity(1);
    }
}
