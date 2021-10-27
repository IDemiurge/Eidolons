package libgdx.bf.generic;

import com.badlogic.gdx.graphics.g2d.Batch;
import libgdx.screens.batch.CustomSpriteBatch;
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

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // blending.null
        ((CustomSpriteBatch) batch).setBlending(blending);
        super.draw(batch, parentAlpha);
        ((CustomSpriteBatch) batch).resetBlending();
    }
}
