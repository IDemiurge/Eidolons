package eidolons.libgdx.utils.mixer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Created by JustMe on 7/17/2018.
 */
public class GdxMasker {

    String maskPath;

    public GdxMasker(String maskPath) {
        this.maskPath = maskPath;
    }

    public void draw(Batch batch){
        //disable RGB color, only enable ALPHA to the frame buffer
        Gdx.gl.glColorMask(false, false, false, true);

        //change the blending function for our alpha map
        batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_ALPHA);

        //draw alpha mask sprite(s)
//        batch.draw(maskTexture, MASK_OFFSET_X + getX(), MASK_OFFSET_Y + getY());

        //change the function for our source
        batch.setBlendFunction(GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_SRC_ALPHA);

        //draw the source alpha
//        sprite.draw(batch);

        //flush the batch to the GPU
        batch.flush();
    }
}
