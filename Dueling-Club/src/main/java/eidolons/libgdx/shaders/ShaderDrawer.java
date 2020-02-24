package eidolons.libgdx.shaders;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.game.core.Eidolons;
import main.system.launch.CoreEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 5/17/2018.
 */
public class ShaderDrawer {
    public static final float SUPER_DRAW = 100;
    public static final int MAX_ITEM_GROUPS = 4;
    private static Map<Actor, Runnable> map = new HashMap<>();

    public static void drawWithCustomShader(Actor actor, Batch batch,
                                            ShaderProgram shader) {
        drawWithCustomShader(actor, batch, shader, false);
    }

    public static void drawWithCustomShader(Actor actor, Batch batch,
                                            ShaderProgram shader, boolean nullMeansOriginal, boolean reset) {
        Runnable draw = getDrawRunnable(actor, batch);
        ShaderProgram originalShader = batch.getShader();
        if (originalShader != shader && !(shader == null && nullMeansOriginal))
            batch.setShader(shader);
        draw.run();

        if (reset)
            if (originalShader != shader && !(shader == null && nullMeansOriginal))
                batch.setShader(originalShader);
    }

    public static void drawWithCustomShader(Actor actor, Batch batch,
                                            ShaderProgram shader, boolean nullMeansOriginal
    ) {
        if (CoreEngine.isIDE())
            if (shader!=null && !shader.isCompiled())
            {
                drawWithCustomShader(actor, batch, null , false, true);
                return;
            }

        drawWithCustomShader(actor, batch, shader, nullMeansOriginal, true);
    }

    private static Runnable getDrawRunnable(Actor actor, Batch batch) {
        Runnable runnable = map.get(actor);
        if (runnable == null) { //TODO CLEAR CACHE INSTEAD!
            runnable = () -> {
                if (batch.isDrawing())
                    actor.draw(batch, ShaderDrawer.SUPER_DRAW);
                else
                    actor.draw(Eidolons.getScreen().getBatch(), ShaderDrawer.SUPER_DRAW);
            };
            map.put(actor, runnable);
        }
        return runnable;
    }
}
