package main.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import main.libgdx.screens.DungeonScreen;

/**
 * Created by JustMe on 8/30/2017.
 */
public class GdxMaster {
    private static final int DEFAULT_WIDTH = 1600;
    private static final int DEFAULT_HEIGHT = 900;
    private static int width;
    private static int height;
    private static Float fontSizeMod;

    public static float centerWidth(Actor actor) {
        if (actor.getParent() != null)
            if (actor.getParent().getWidth() != 0)
            return (actor.getParent().getWidth() - actor.getWidth())/2;
        return GdxMaster.getWidth() / 2 - actor.getWidth() / 2;
    }

    public static float centerHeight(Actor actor) {
        return GdxMaster.getHeight() / 2 - actor.getHeight() / 2;
    }

    public static float top(Actor actor) {
        if (actor.getParent() != null)
            return actor.getParent().getHeight() - actor.getHeight();
        return GdxMaster.getHeight() - actor.getHeight();
    }

    public static float right(Actor actor) {
        if (actor.getParent() != null)
            return actor.getParent().getWidth() - actor.getWidth();
        return GdxMaster.getWidth() - actor.getWidth();
    }

    public static Vector3 getCursorPosition() {
        return DungeonScreen.getInstance().getGridStage().getCamera().
         unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
    }

    public static boolean isLwjglThread() {
        return Thread.currentThread().getName().equalsIgnoreCase("LWJGL Application");
    }

    public static int getWidth() {
        if (width == 0)
            width = Gdx.graphics.getWidth();
        return width;
    }


    public static int getHeight() {
        if (height == 0)
            height = Gdx.graphics.getHeight();
        return height;
    }

    public static boolean isGuiReady() {
        return DungeonScreen.getInstance() != null;
    }

    public static float getFontSizeMod() {
        if (fontSizeMod == null)
            fontSizeMod = new Float(getWidth() * getHeight()) / GdxMaster.DEFAULT_WIDTH / GdxMaster.DEFAULT_HEIGHT;
        if (fontSizeMod < 0) {
            fontSizeMod = Float.valueOf(1);
        }
        return fontSizeMod;
    }

}
