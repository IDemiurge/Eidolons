package eidolons.libgdx.shaders;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import eidolons.game.core.Eidolons;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 5/17/2018.
 */
public class ShaderMaster {
    public static final float SUPER_DRAW = 100;
    public static final int MAX_ITEM_GROUPS = 4;
    private static Map<Actor, Runnable> map = new HashMap<>();
    private static Map<SHADER, ShaderProgram> shaderMap = new HashMap<>();

    public static ShaderProgram getShader(SHADER shader) {
        ShaderProgram program = shaderMap.get(shader);
        if (program == null) {
            String vert = null;
            String frag = null;
            program = new ShaderProgram(vert, frag);
            shaderMap.put(shader, program);
        }
        return program;
    }

    public enum SHADER{
    DARKEN,
    GRAYSCALE,
    FISH_EYE,

}

// substitute vars
    public static void compileDynamicShader(){
//        PathFinder.getShadersPath()
//        ShaderProgram shader = new ShaderProgram(vert, frag);
    }

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
        drawWithCustomShader(actor, batch, shader, nullMeansOriginal, true);
    }

    private static Runnable getDrawRunnable(Actor actor, Batch batch) {
        Runnable runnable = map.get(actor);
        if (runnable == null) { //TODO CLEAR CACHE INSTEAD!
            runnable = () -> {
                if (batch.isDrawing())
                    actor.draw(batch, ShaderMaster.SUPER_DRAW);
                else
                    actor.draw(Eidolons.getScreen().getBatch(), ShaderMaster.SUPER_DRAW);
            };
            map.put(actor, runnable);
        }
        return runnable;
    }
}
