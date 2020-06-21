package eidolons.libgdx.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.libgdx.texture.Textures;

public class ScissorMaster {
    public static void drawInRectangle(Actor actor, Batch batch, float x, float y, float v, float height) {
        drawInRectangle(actor, batch, x, y, v, height, null);
    }


    public static void drawWithAlphaMask(Actor actor, Batch batch, float x, float y,
                                         float width, float height
            , Runnable drawRunnable, TextureRegion alphaMask) {
        //disable RGB color, only enable ALPHA to the frame buffer
        Gdx.gl.glColorMask(false, false, false, true);

        //change the blending function for our alpha map
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);

        //draw alpha mask sprite(s)
        batch.draw(Textures.BLACK, 0, 0, GdxMaster.getWidth(), GdxMaster.getHeight());
        batch.draw(alphaMask, x, y, width, height);
        //flush the batch to the GPU
        batch.flush();

        Gdx.gl.glColorMask(true, true, true, true);
        // ((CustomSpriteBatch) batch).setBlending(GenericEnums.BLENDING.SCREEN);
        batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
        // batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        // Gdx.gl.glScissor(clipX, clipY, clipWidth, clipHeight);
        if (drawRunnable == null) {
            actor.draw(batch, ShaderDrawer.SUPER_DRAW);
        } else {
            drawRunnable.run();
        }
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
        // batch.flush();
    }
    public static void drawInRectangle(Actor actor, Batch batch, float x, float y,
                                       float width, float height, Runnable drawRunnable) {



        if (width<=0 || height<= 0)
            return;
        Rectangle scissors = new Rectangle();
        Rectangle clipBounds = null;
        clipBounds = new Rectangle(x, y, width, height);

        batch.flush();
        actor.getStage().calculateScissors(clipBounds, scissors);
        ScissorStack.pushScissors(scissors);
        if (drawRunnable == null) {
            actor.draw(batch, ShaderDrawer.SUPER_DRAW);
        } else {
            drawRunnable.run();
        }
        batch.flush();
        try {
            ScissorStack.popScissors();
        } catch (Exception e) {
        }
    }
}
