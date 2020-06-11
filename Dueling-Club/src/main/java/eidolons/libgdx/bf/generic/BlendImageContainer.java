package eidolons.libgdx.bf.generic;

import main.content.enums.GenericEnums;

public class BlendImageContainer extends FadeImageContainer {
    private GenericEnums.BLENDING blending;

    public BlendImageContainer(String path, GenericEnums.BLENDING blending) {
        super(path);
        this.blending = blending;
    }

    public void setBlending(GenericEnums.BLENDING blending) {
        this.blending = blending;
    }

    public GenericEnums.BLENDING getBlending() {
        return blending;
    }
}
