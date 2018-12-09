package com.bitfire.postprocessing.effects;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.bitfire.postprocessing.PostProcessorEffect;
import com.bitfire.postprocessing.filters.CustomFilter;
import com.bitfire.postprocessing.filters.Filter;
import com.bitfire.postprocessing.utils.PingPongBuffer;
import eidolons.libgdx.shaders.ShaderMaster.SHADER;

/**
 * Created by JustMe on 12/3/2018.
 */
public class CustomPostEffect extends PostProcessorEffect {

    protected PingPongBuffer pingPongBuffer;
    protected Filter<Filter> filter;

    public CustomPostEffect() {
    }

    public CustomPostEffect(Filter filter) {
        this.filter = filter;
    }
    public CustomPostEffect(SHADER shader) {
        filter = new CustomFilter(shader);
//        pingPongBuffer= new PingPongBuffer()
    }

    @Override
    public void rebind() {
//        pingPongBuffer.rebind();
        filter.rebind();
    }

    @Override
    public void render(FrameBuffer src, FrameBuffer dest) {
        restoreViewport(dest);
        filter.setInput(src).setOutput(dest).render();
//        pingPongBuffer.begin();
//        filter.render(pingPongBuffer);
//        pingPongBuffer.end();
//        out = pingPongBuffer.getResultTexture();
    }

    @Override
    public void dispose() {
        filter.dispose();
    }
}
