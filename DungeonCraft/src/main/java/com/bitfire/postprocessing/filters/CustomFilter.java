package com.bitfire.postprocessing.filters;

import eidolons.libgdx.shaders.GrayscaleShader;
import eidolons.libgdx.shaders.ShaderMaster.SHADER;

/**
 * Created by JustMe on 12/3/2018.
 */
public class CustomFilter extends Filter<Filter> {
    public CustomFilter(SHADER shader) {
        super(GrayscaleShader.getGrayscaleShader());
    }

    @Override
    public void rebind() {

    }

    @Override
    protected void onBeforeRender() {

    }
}
