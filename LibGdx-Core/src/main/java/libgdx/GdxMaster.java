package libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import eidolons.content.consts.VisualEnums;
import libgdx.bf.mouse.GlobalInputController;
import libgdx.gui.dungeon.panels.TablePanel;
import libgdx.screens.*;
import libgdx.screens.batch.ColorBatch;
import libgdx.screens.batch.CustomSpriteBatch;
import libgdx.screens.batch.CustomSpriteBatchImpl;
import libgdx.screens.dungeon.DungeonScreen;
import eidolons.system.options.GraphicsOptions;
import eidolons.system.options.OptionsMaster;
import libgdx.screens.handlers.ScreenMaster;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.game.bf.directions.DIRECTION;
import main.system.launch.Flags;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 8/30/2017.
 */
public class GdxMaster {
    private static final boolean DUMMY = true;
    public static final float fontSizeAdjustCoef = 0.15f;
    public static final float sizeAdjustCoef = 0.25f;
    public static final boolean FULLHD_ONLY = false;
    private static final boolean COLORFUL = false;
    public static boolean CUSTOM_RESOLUTION;
    private static final int DEFAULT_WIDTH = 1824;
    private static final int DEFAULT_HEIGHT = 972;
    private static final int DEFAULT_WIDTH_FULLSCREEN = 1920;
    private static final int DEFAULT_HEIGHT_FULLSCREEN = 1080;
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
    private static boolean cursorSet;
    private static boolean stackRunnables;
    private static Runnable stackRunnable;
    private static VisualEnums.CURSOR cursor;
    private static CustomSpriteBatch batch;
    private static boolean keyInputBlocked;

    public static List<Group> getAncestors(Actor actor) {
        List<Group> list = new ArrayList<>();
        if (actor == null) {
            return list;
        }
        actor = actor.getParent();
        while (actor instanceof Group) {
            list.add((Group) actor);
            actor = actor.getParent();
        }
        return list;
    }

    public static void setKeyInputBlocked(boolean keyInputBlocked) {
        GdxMaster.keyInputBlocked = keyInputBlocked;
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
        if (GdxMaster.CUSTOM_RESOLUTION)
        if (actor.getWidth() == 0)
            if (actor instanceof TablePanel) {
                return GdxMaster.getWidth() / 2 -
                        ((TablePanel) actor).getPrefWidth() / 2;
            }
        return GdxMaster.getWidth() / 2 - actor.getWidth() / 2;
    }

    public static float centerHeight(Actor actor) {
        if (actor.getParent() != null)
            if (actor.getParent().getHeight() != 0)
                return actor.getParent().getHeight() / 2 - actor.getHeight() / 2;
        if (GdxMaster.CUSTOM_RESOLUTION) //TODO EA HACK
        if (actor.getHeight() == 0)
            if (actor instanceof TablePanel) {
                return GdxMaster.getHeight() / 2 -
                        ((TablePanel) actor).getPrefHeight() / 2;
            }
        return GdxMaster.getHeight() / 2 - actor.getHeight() / 2;
    }

    public static float right(Actor actor) {
        if (actor.getParent() != null && actor.getParent().getWidth() != 0) {
            float x = actor.getParent().getWidth() - actor.getWidth();
            actor.setX(x);
            return x;
        }
        float x = GdxMaster.getWidth() - actor.getWidth();
        actor.setX(x);
        return x;
    }

    public static float rightScreen(Actor actor) {
        return GdxMaster.getWidth() - actor.getWidth();
    }

    public static float topScreen(Actor actor) {
        return GdxMaster.getHeight() - actor.getHeight();
    }

    public static float getTopY(Actor actor) {
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

    public static boolean isGdxThread() {
        return isLwjglThread();
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
        return ScreenMaster.getScreen() != null;
    }

    public static float getFontSizeMod() {

        if (fontSizeMod == null) {
            fontSizeMod = (float) (getWidth() * getHeight()) / getDefaultWidth() / getDefaultHeight();
            fontSizeModSquareRoot = (float) Math.sqrt(fontSizeMod);

            if (isFixedSize()) {
                fontSizeMod = 1f;
                fontSizeModSquareRoot = 1f;
            }
        }
        if (fontSizeMod < 0) {
            fontSizeMod = 1f;
        }
        return fontSizeMod * getUserFontScale();
    }

    private static boolean isFixedSize() {
        return true;
    }

    private static int getDefaultWidth() {
        return ScreenMaster.isFullscreen() ? GdxMaster.DEFAULT_WIDTH
                : GdxMaster.DEFAULT_WIDTH_FULLSCREEN;
    }

    private static int getDefaultHeight() {
        return ScreenMaster.isFullscreen() ? GdxMaster.DEFAULT_HEIGHT
                : GdxMaster.DEFAULT_HEIGHT_FULLSCREEN;
    }

    public static Float getWidthMod() {
        if (widthMod == null) {
            widthMod = (float) getWidth() / getDefaultWidth();
            if (isFixedSize()) {
                widthMod = 1f;
            }
        }
        return widthMod;
    }

    public static Float getHeightMod() {
        if (heightMod == null) {
            heightMod = (float) getHeight() / getDefaultHeight();
            if (isFixedSize()) {
                heightMod = 1f;
            }
        }
        return heightMod;
    }

    public static Float getWidthModSquareRoot() {
        if (widthModSquareRoot == null) {
            widthModSquareRoot = (float) getWidth() / getDefaultWidth();
        }
        return widthModSquareRoot;
    }

    public static Float getHeightModSquareRoot() {
        if (heightModSquareRoot == null) {
            heightModSquareRoot = (float) getHeight() / getDefaultHeight();
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
//        if (inputController instanceof InputMultiplexer) {
//            main.system.auxiliary.log.LogMaster.log(1, ">>>>> setInputProcessor InputMultiplexer: " + inputController);
//
//            for (InputProcessor sub : ((InputMultiplexer) inputController).getProcessors()) {
//                main.system.auxiliary.log.LogMaster.log(1, "Processor: " + sub);
//            }
//        } else
//            main.system.auxiliary.log.LogMaster.log(1, ">>>>> setInputProcessor: " + inputController);
        inputController = new InputMultiplexer(inputController,
                GlobalInputController.getInstance());
        Gdx.input.setInputProcessor(inputController);
    }

    public static void top(Actor actor) {
        actor.setY(getTopY(actor));
    }
    public static void top(Actor actor, float offset) {
        actor.setY(getTopY(actor)+offset);
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
        return (Group) child.firstAscendant(clazz);
//        Group actor = child.getParent();
//        while (true) {
//            if (actor == null) {
//                break;
//            }
//            if (ClassMaster.isInstanceOf(actor, clazz)) {
//                return actor;
//            }
//            actor = actor.getParent();
//
//        }
//        return null;
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
            if (userFontScale==0) {
                userFontScale=1f;
            }
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

    public static boolean hasController(InputProcessor inputProcessor, InputProcessor stage) {
        if (inputProcessor instanceof InputMultiplexer) {
            for (InputProcessor processor : ((InputMultiplexer) inputProcessor).getProcessors()) {
                if (hasController(processor, stage)) {
                    return true;
                }
            }

        }
        return inputProcessor.equals(stage);
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
        return !(vector2.y > v.y + target.getHeight());
    }

    private static void setCursor(Cursor cursor) {
        // if (CoreEngine.isSuperLite()) {
        //     return;
        // }
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        Gdx.graphics.setCursor(cursor);
        Gdx.input.setCursorPosition(x, y);
    }

    public static void setCursorType(VisualEnums.CURSOR cursor) {
        if (GdxMaster.cursor == cursor) {
            return;
        }
        Pixmap pm = new Pixmap(GDX.file(cursor.getFilePath()));
        setCursor(Gdx.graphics.newCursor(pm, cursor.x , cursor.y));
        GdxMaster.cursor = cursor;
    }

    public static void setDefaultCursor() {
        setCursorType(VisualEnums.CURSOR.DEFAULT);
    }

    public static void setLoadingCursor() {
        if (Flags.isIDE()) {
            return;
        }
        setCursorType(VisualEnums.CURSOR.LOADING);
    }


    public static void setTargetingCursor() {
        setCursorType(VisualEnums.CURSOR.TARGETING);
    }

    public static boolean isVisibleEffectively(Actor a) {
        if (a == null) {
            return false;
        }
        if (!a.isVisible())
            return false;
        if ( a.getParent()==null )
            return false;
        for (Group group : GdxMaster.getAncestors(a)) {
            if (group == null) {
                continue;
            }
            if (!group.isVisible()) {
                return false;
            }
        }
        return true;
    }

    public static InputMultiplexer getMultiplexer(InputProcessor... processors) {
        InputMultiplexer multiplexer = new InputMultiplexer();
        for (InputProcessor processor : processors) {
            if (processor != null) {
                multiplexer.addProcessor(processor);
            }
        }

        return multiplexer;
    }

    public static void onInputGdx(Runnable r) {
        onInput(r, true, false);
    }

    public static void onInput(Runnable r, Boolean gdx_any_pass, boolean stack) {
        Runnable finalR = r;
        if (!ScreenMaster.getScreen().getController().isStackInput()
                || stack) {
            stackRunnable = r;
            return;
        }
        if (stackRunnable != null) {
            Runnable runnable = stackRunnable;
//        Runnable onInputGdx = Eidolons.getScreen().getController().getOnInput(gdx_any_pass);
//        if (onInputGdx != null) {
            r = () -> {
//                WaitMaster.WAIT(3000);
                finalR.run();
                runnable.run();
            };
            stackRunnable = null;
        }
        ScreenMaster.getScreen().getController().onInputGdx(gdx_any_pass, r);
    }

    public static void onPassInput(Runnable finalR) {
        onInput(finalR, null, false);
    }

    public static void inputPass() {
        ScreenMaster.getScreen().getController().inputPass();
    }

    public static void input() {
        ScreenMaster.getScreen().getController().input();
    }

    public static void setWindowName(String s) {
        Gdx.app.postRunnable(() ->
                Gdx.graphics.setTitle(s));
    }

    public static CustomSpriteBatch createBatchInstance( ) {
        if (DUMMY){

        }
        if (COLORFUL){
            return new ColorBatch();
        }
        if (isWRITE_ATLAS_IMAGES()){
            return new AtlasGenSpriteBatch();
        }
        return new CustomSpriteBatchImpl();
    }

    private static boolean isWRITE_ATLAS_IMAGES() {
        return Flags.isIDE() &&Flags.isMe() ;
    }

    public static CustomSpriteBatch getMainBatch() {
        if (batch == null) {
            batch =  createBatchInstance( );
        }
        return batch;
    }

    public static boolean isKeyPressed(int keyPressed) {
        if (keyInputBlocked)
            return false;
        return Gdx.input.isKeyPressed(keyPressed);
    }


    public static int getAlignForDirection(DIRECTION direction) {
        switch (direction) {
            case UP:
                return Align.top;
            case DOWN:
                return Align.bottom;
            case LEFT:
                return Align.left;
            case RIGHT:
                return Align.right;
            case UP_LEFT:
                return Align.topLeft;
            case UP_RIGHT:
                return Align.topRight;
            case DOWN_RIGHT:
                return Align.bottomRight;
            case DOWN_LEFT:
                return Align.bottomLeft;
        }
        return 0;
    }

    public static Vector2 offset(Vector2 pos, int x, int y) {
        return new Vector2(pos.x+x, pos.y+y);
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
