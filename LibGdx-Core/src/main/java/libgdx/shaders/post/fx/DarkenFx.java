package libgdx.shaders.post.fx;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.bitfire.postprocessing.PostProcessorEffect;
import com.bitfire.postprocessing.filters.Threshold;

public class DarkenFx extends PostProcessorEffect {
    Threshold threshold;

    @Override
    public void rebind() {

    }

    @Override
    public void applyCoef(float a) {
        super.applyCoef(a);
//        threshold.setTreshold();
    }

    @Override
    public void render(FrameBuffer src, FrameBuffer dest) {

    }

    @Override
    public void dispose() {

    }
}
