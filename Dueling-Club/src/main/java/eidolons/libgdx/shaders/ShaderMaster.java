package eidolons.libgdx.shaders;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by JustMe on 5/17/2018.
 */
public class ShaderMaster {
    public static void drawWithCustomShader(Runnable draw, Batch batch,
                                             ShaderProgram shader) {
        ShaderProgram originalShader = batch.getShader();
        batch.setShader(shader);
         draw.run();
        batch.setShader(originalShader);
    }
}
