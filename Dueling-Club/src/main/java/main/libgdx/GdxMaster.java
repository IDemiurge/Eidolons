package main.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import main.data.filesys.PathFinder;
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
        if (actor.getParent() != null &&actor.getParent().getWidth()!=0 )
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

    public static void setWidth(int width) {
        GdxMaster.width = width;
    }
    public static void setHeight(int height) {
        GdxMaster.height = height;
    }

    public static void takeScreenShot() {
        byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);

        Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(),
         Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
        PixmapIO.writePNG(
         new FileHandle( PathFinder.getImagePath()+"big\\screenshots\\" +
          main.system.auxiliary.TimeMaster.getTimeStamp()+(".png")), pixmap);
        pixmap.dispose();
    }
}
