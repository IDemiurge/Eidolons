package eidolons.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.bf.mouse.GlobalInputController;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.stage.StageX;
import eidolons.system.options.GraphicsOptions;
import eidolons.system.options.OptionsMaster;
import main.data.filesys.PathFinder;
import main.system.auxiliary.ClassMaster;
import main.system.launch.CoreEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 8/30/2017.
 */
public class GdxMaster {
    public static final float fontSizeAdjustCoef = 0.15f;
    public static final float sizeAdjustCoef = 0.25f;
    private static final int DEFAULT_WIDTH = 1500;
    private static final int DEFAULT_HEIGHT = 900;
    private static final int DEFAULT_WIDTH_FULLSCREEN = 1600;
    private static final int DEFAULT_HEIGHT_FULLSCREEN = 960;
    private static int width;
    private static int height;
    private static Float fontSizeMod;
    private static Float widthMod;
    private static Float heightMod;
    private static Float widthModSquareRoot;
    private static Float heightModSquareRoot;
    private static InputProcessor globalInputProcessor;
    private static float fontSizeModSquareRoot;
    private static Float userFontScale;
    private static Float userUiScale;
    private static Float brightness;

    public static List<Group> getAncestors(Actor actor) {
        List<Group> list = new ArrayList<>();
        while (actor != null) {
            list.add(actor.getParent());
            actor = actor.getParent();
        }
        return list;
    }

    public static float adjustPos(boolean x, float pos) {
        if (true) //temp
            return pos;
        if (x)
            return pos
             - (pos * (GdxMaster.getFontSizeMod() - 1) * fontSizeAdjustCoef) / 2;
        return pos
         + (pos * (GdxMaster.getFontSizeMod() - 1) * fontSizeAdjustCoef) / 2;
    }

    public static int adjustFontSize(float size) {
        return Math.round(adjustSize(size, fontSizeAdjustCoef) + size
         * (GdxMaster.getFontSizeModSquareRoot() - 1) * fontSizeAdjustCoef);
    }

    public static float adjustSize(float size) {
        return adjustSize(size, sizeAdjustCoef);
    }

    public static float adjustSize(float size, float coef) {
        return size
         + size
         * (GdxMaster.getFontSizeMod() - 1) * coef;
    }

    public static float adjustHeight(float size) {
        return size
         + size
         * (GdxMaster.getHeightMod() - 1) * sizeAdjustCoef;
    }

    public static float adjustWidth(float size) {
        return size
         + size
         * (GdxMaster.getWidthMod() - 1) * sizeAdjustCoef;
    }

    public static float adjustSizeBySquareRoot(float size) {
        return adjustSizeBySquareRoot(size, sizeAdjustCoef);
    }

    public static float adjustSizeBySquareRoot(float size, float coef) {
        return size
         + size
         * (GdxMaster.getFontSizeModSquareRoot() - 1) * coef;
    }

    public static float adjustSizePlain(float size) {
        return size * GdxMaster.getFontSizeMod();
    }

    public static float centerHeightScreen(Actor actor) {
        return GdxMaster.getHeight() / 2 - actor.getHeight() / 2;
    }

    public static float centerWidthScreen(Actor actor) {
        return GdxMaster.getWidth() / 2 - actor.getWidth() / 2;
    }

    public static float centerWidth(Actor actor) {
        if (actor.getParent() != null)
            if (actor.getParent().getWidth() != 0)
                return (actor.getParent().getWidth() - actor.getWidth()) / 2;
        return GdxMaster.getWidth() / 2 - actor.getWidth() / 2;
    }

    public static float centerHeight(Actor actor) {
        if (actor.getParent() != null)
            if (actor.getParent().getHeight() != 0)
                return actor.getParent().getHeight() / 2 - actor.getHeight() / 2;
        return GdxMaster.getHeight() / 2 - actor.getHeight() / 2;
    }

    public static float right(Actor actor) {
        if (actor.getParent() != null && actor.getParent().getWidth() != 0)
            return actor.getParent().getWidth() - actor.getWidth();
        return GdxMaster.getWidth() - actor.getWidth();
    }

    public static float rightScreen(Actor actor) {
        return GdxMaster.getWidth() - actor.getWidth();
    }

    public static float topScreen(Actor actor) {
        return GdxMaster.getHeight() - actor.getHeight();
    }

    public static float top(Actor actor) {
        if (actor.getParent() != null)
            if (actor.getParent().getHeight() != 0)
                return actor.getParent().getHeight() - actor.getHeight();
        return GdxMaster.getHeight() - actor.getHeight();
    }

    public static Vector3 getCursorPosition() {
        return DungeonScreen.getInstance().getGridStage().getCamera().
         unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
    }

    public static Vector2 getCursorPosition(Actor actor) {
        return getCursorPosition(actor.getStage());
    }

    public static Vector2 getCursorPosition(Stage stage) {
        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        v2 = stage.screenToStageCoordinates(v2);
        return v2;
    }

    public static boolean isLwjglThread() {
        return Thread.currentThread().getName().equalsIgnoreCase("LWJGL Application");
    }

    public static int getWidth() {
        if (width == 0)
            width = Gdx.graphics.getWidth();
        return width;
    }

    public static void setWidth(int width) {
        GdxMaster.width = width;
    }

    public static int getHeight() {
        if (height == 0)
            height = Gdx.graphics.getHeight();
        return height;
    }

    public static void setHeight(int height) {
        GdxMaster.height = height;
    }

    public static boolean isGuiReady() {
        return DungeonScreen.getInstance() != null;
    }

    public static float getFontSizeMod() {

        if (fontSizeMod == null) {
            fontSizeMod = new Float(getWidth() * getHeight()) / getDefaultWidth() / getDefaultHeight();
            fontSizeModSquareRoot = (float) Math.sqrt(fontSizeMod);

            if (isFixedSize()){
                fontSizeMod=1f;
                fontSizeModSquareRoot=1f;
            }
        }
        if (fontSizeMod < 0) {
            fontSizeMod = Float.valueOf(1);
        }
        return fontSizeMod * getUserFontScale();
    }

    private static boolean isFixedSize() {
        return true;
    }

    private static int getDefaultWidth() {
        return Eidolons.isFullscreen() ? GdxMaster.DEFAULT_WIDTH
         : GdxMaster.DEFAULT_WIDTH_FULLSCREEN;
    }

    private static int getDefaultHeight() {
        return Eidolons.isFullscreen() ? GdxMaster.DEFAULT_HEIGHT
         : GdxMaster.DEFAULT_HEIGHT_FULLSCREEN;
    }

    public static Float getWidthMod() {
        if (widthMod == null) {
            widthMod = new Float(getWidth()) / getDefaultWidth();
            if (isFixedSize()){
                widthMod=1f;
            }
        }
        return widthMod;
    }

    public static Float getHeightMod() {
        if (heightMod == null) {
            heightMod = new Float(getHeight()) / getDefaultHeight();
            if (isFixedSize()){
                heightMod=1f;
            }
        }
        return heightMod;
    }

    public static Float getWidthModSquareRoot() {
        if (widthModSquareRoot == null) {
            widthModSquareRoot = new Float(getWidth()) / getDefaultWidth();
        }
        return widthModSquareRoot;
    }

    public static Float getHeightModSquareRoot() {
        if (heightModSquareRoot == null) {
            heightModSquareRoot = new Float(getHeight()) / getDefaultHeight();
        }
        return heightModSquareRoot;
    }

    public static Float getWidthModSquareRoot(float coef) {
        return getWidthModSquareRoot() * coef;
    }

    public static Float getHeightModSquareRoot(float coef) {
        return getHeightModSquareRoot() * coef;
    }

    public static void takeScreenShot() {
        byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);

        Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(),
         Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
        PixmapIO.writePNG(
         GDX.file(PathFinder.getImagePath() + "big/screenshots/" +
          main.system.auxiliary.TimeMaster.getTimeStamp() + (".png")), pixmap);
        pixmap.dispose();
    }


    public static void setInputProcessor(InputProcessor inputController) {
        if (inputController instanceof InputMultiplexer) {
            main.system.auxiliary.log.LogMaster.log(0, ">>>>> setInputProcessor InputMultiplexer: " + inputController);

            for (InputProcessor sub : ((InputMultiplexer) inputController).getProcessors()) {
                main.system.auxiliary.log.LogMaster.log(0, "Processor: " + sub);
            }
        } else
            main.system.auxiliary.log.LogMaster.log(0, ">>>>> setInputProcessor: " + inputController);

        inputController = new InputMultiplexer(inputController,
         GlobalInputController.getInstance());
        Gdx.input.setInputProcessor(inputController);
    }

    public static void center(Actor actor) {
        actor.setPosition(centerWidth(actor), centerHeight(actor));
    }

    public static void centerAndAdjust(Actor actor) {
        actor.setPosition(adjustPos(true, centerWidth(actor)), adjustPos(false, centerHeight(actor)));
    }

    public static void resized() {
        fontSizeMod = null;
    }

    public static boolean isHpBarAttached() {
        return true;
    }

    public static void adjustAndSetSize(Actor actor, int w, int h) {
        actor.setSize(adjustSize(w), adjustSize(h));
    }

    public static Vector2 getAlignedPos(Actor parent, Alignment alignment,
                                        int w, int h) {
        float x = (parent.getWidth() - w) / 2;
        float y = (parent.getHeight() - h) / 2;
        if (alignment.isAlignedWithBottom())
            y = 0;
        if (alignment.isAlignedWithTop())
            y = parent.getHeight() - h;

        if (alignment.isAlignedWithLeft())
            x = 0;
        if (alignment.isAlignedWithRight())
            x = parent.getWidth() - w;
        return new Vector2(x, y);
    }


    public static Group getFirstParentOfClass(Actor child, Class clazz) {
        Group actor = child.getParent();
        while (true) {
            if (actor == null) {
                break;
            }
            if (ClassMaster.isInstanceOf(actor, clazz)) {
                return actor;
            }
            actor = actor.getParent();

        }
        return null;
    }


    public static Array<Actor> getAllChildren(Group group) {
        Array<Actor> list = new Array<>();
        addChildren(list, group);
        return list;
    }

    private static void addChildren(Array<Actor> list, Group group) {
        for (Actor sub : group.getChildren()) {
            list.add(sub);
            if (sub instanceof Group) {
                addChildren(list, ((Group) sub));
            }

        }
    }

    public static float getFontSizeModSquareRoot() {
        return fontSizeModSquareRoot;
    }

    public static Float getUserFontScale() {
        if (userFontScale == null) {
            userFontScale = OptionsMaster.getGraphicsOptions().getFloatValue(GraphicsOptions.GRAPHIC_OPTION.FONT_SIZE) / 100;
        }
        return userFontScale;
    }

    public static void setUserFontScale(Float userFontScale) {
        GdxMaster.userFontScale = userFontScale;
    }

    public static Float getUserUiScale() {
        return userUiScale;
    }

    public static void setUserUiScale(Float userUiScale) {
        if (userUiScale == null) {
            userUiScale = OptionsMaster.getGraphicsOptions().getFloatValue(GraphicsOptions.GRAPHIC_OPTION.UI_SCALE) / 100;
        }
        GdxMaster.userUiScale = userUiScale;
    }

    public static Float getBrightness() {
        if (brightness == null) {
            brightness = OptionsMaster.getGraphicsOptions().getFloatValue(GraphicsOptions.GRAPHIC_OPTION.BRIGHTNESS) / 100;
        }
        return brightness;
    }

    public static void setBrightness(float brightness) {
        GdxMaster.brightness = brightness;
    }

    public static boolean hasController(InputProcessor inputProcessor, StageX gridStage) {
        if (inputProcessor instanceof InputMultiplexer) {
            for (InputProcessor processor : ((InputMultiplexer) inputProcessor).getProcessors()) {
                if (hasController(processor, gridStage)) {
                    return true;
                }
            }

        }
        return inputProcessor.equals(gridStage);
    }

    public static boolean isWithin(Actor target, Vector2 vector2, boolean stage) {
        Vector2 v = stage
         ? target.getParent().localToStageCoordinates(new Vector2(target.getX(), target.getY()))
         : new Vector2(target.getX(), target.getY());

        if (vector2.x < v.x)
            return false;
        if (vector2.y < v.y)
            return false;
        if (vector2.x > v.x + target.getWidth())
            return false;
        if (vector2.y > v.y + target.getHeight())
            return false;

        return true;
    }

    private static void setCursor(Cursor cursor) {
        if (CoreEngine.isFastMode()) {
            return;
        }
        Gdx.graphics.setCursor(cursor);
        Gdx.input.setCursorPosition(Gdx.input.getX() + 1, Gdx.input.getY() + 1);
    }

    public static void setDefaultCursor() {
        Pixmap pm = new Pixmap(GDX.file(PathFinder.getCursorPath()));
        setCursor(Gdx.graphics.newCursor(pm, 0, 0));
    }

    public static void setLoadingCursor() {
        Pixmap pm = new Pixmap(GDX.file(PathFinder.getLoadingCursorPath()));
        setCursor(Gdx.graphics.newCursor(pm, 32, 32));
    }

    public static void setTargetingCursor() {
        Pixmap pm = new Pixmap(GDX.file(PathFinder.getTargetingCursorPath()));
        setCursor(Gdx.graphics.newCursor(pm, 32, 32));
    }

    public enum CURSOR {
        DEFAULT,
        TARGETING,
        LOADING,
        WAITING,
        NO,

    }
    //    protected static void setAttackTargetingCursor() {
    //        Pixmap pm = new Pixmap(GDX.file(PathFinder.getTargetingCursorPath()));
    //        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
    //    }
    //    protected static void setSpellTargetingCursor() {
    //        Pixmap pm = new Pixmap(GDX.file(PathFinder.getTargetingCursorPath()));
    //        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
    //    }
}
