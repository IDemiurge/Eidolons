package com.bitfire.postprocessing.filters;

import libgdx.shaders.GrayscaleShader;
import libgdx.shaders.ShaderMaster;

/**
 * Created by JustMe on 12/3/2018.
 */
public class CustomFilter extends Filter<Filter> {

    public CustomFilter(ShaderMaster.SHADER shader) {
        super(GrayscaleShader.getGrayscaleShader());
    }

    @Override
    public void rebind() {

    }

    @Override
    protected void onBeforeRender() {

    }
}
