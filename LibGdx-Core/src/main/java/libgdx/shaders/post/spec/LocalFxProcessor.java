package libgdx.shaders.post.spec;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.bitfire.postprocessing.PostProcessor;

/**
 * Created by JustMe on 12/6/2018.
 *
 * all we have to do is
 * 1) figure out which FX are ON SCREEN
 * 2) adjust their position attrs
 */
public class LocalFxProcessor extends PostProcessor {
    public LocalFxProcessor(boolean useDepth, boolean useAlphaChannel, boolean use32Bits) {
        super(useDepth, useAlphaChannel, use32Bits);
    }

    @Override
    public void render(FrameBuffer dest) {

        super.render(dest);
    }
}
