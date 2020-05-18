package eidolons.libgdx.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import eidolons.libgdx.shaders.ShaderDrawer;

public class ScissorMaster {
    public static void drawInRectangle(Actor actor, Batch batch, float x, float y, float v, float height) {
        drawInRectangle(actor, batch, x, y, v, height, null);
    }

    public static void drawInRectangle(Actor actor, Batch batch, float x, float y, float width, float height, Runnable drawRunnable) {

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
