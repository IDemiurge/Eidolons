package eidolons.libgdx.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.grid.GridPanel;
import eidolons.libgdx.launch.GenericLauncher;
import eidolons.libgdx.screens.dungeon.GenericDungeonScreen;
import eidolons.system.graphics.RESOLUTION;
import eidolons.system.options.GraphicsOptions;
import eidolons.system.options.OptionsMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;

import java.awt.*;

public class ScreenMaster {
    public static final Integer WIDTH_WINDOWED = 95;
    public static final Integer HEIGHT_WINDOWED = 90;
    public static Application gdxApplication;
    public static LwjglApplication application;
    private static boolean fullscreen;
    public static RESOLUTION resolution;
    public static ScreenViewport mainViewport;
    public static GenericLauncher launcher;
    private static GameScreen screen;
    private static SCREEN_TYPE screenType;
    private static SCREEN_TYPE previousScreenType;

    public static LwjglApplication getApplication() {
        return application;
    }

    public static void setApplication(LwjglApplication application) {
        ScreenMaster.application = application;
    }

    public static boolean isFullscreen() {
        return fullscreen;
    }

    public static void setFullscreen(boolean b) {
        if (getApplication() == null)
            return;
//        if (CoreEngine.isMyLiteLaunch()) {
//            return;
//        }
        if (resolution != null)
            if (fullscreen == b)
                return;
        fullscreen = b;
        getApplication().getGraphics().setResizable(true);
        if (fullscreen) {
            int width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
            int height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
            if (GdxMaster.FULLHD_ONLY)
                if (width > 1920) {
                    cannotFullscreen();
                    return;
                }
            if (GdxMaster.FULLHD_ONLY)
                if (height > 1080) {
                    cannotFullscreen();
                    return;
                }

            GdxMaster.setWidth(width);
            GdxMaster.setHeight(LwjglApplicationConfiguration.getDesktopDisplayMode().height);
            getApplication().getGraphics().setUndecorated(true);
            //            getApplication().getGraphics().setFullscreenMode(
            //             new LwjglDisplayMode(width, height,
            //              60, 256, null));

            Gdx.graphics.setWindowedMode(width,
                    LwjglApplicationConfiguration.getDesktopDisplayMode().height);
            getApplication().getApplicationListener().resize(width, height);
            if (getMainViewport() != null)
                getMainViewport().setScreenSize(width, height);

        } else {
            System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
            setResolution(OptionsMaster.getGraphicsOptions().getValue(GraphicsOptions.GRAPHIC_OPTION.RESOLUTION));
            getApplication().getGraphics().setUndecorated(false);
        }
        getApplication().getGraphics().setResizable(false);
    }

    private static void cannotFullscreen() {
        EUtils.onConfirm(
                "Sorry, cannot go fullscreen on your monitor, only 1920x1080 supported yet. ",
                false, () -> {
                });

//        TipMessageMaster.tip(new TipMessageSource("", "", "Carry On", false, ()->{}));

    }

    public static void setResolution(String value) {
        RESOLUTION resolution =
                new EnumMaster<RESOLUTION>().retrieveEnumConst(RESOLUTION.class, value);
        setResolution(resolution);
    }

    public static Dimension getResolutionDimensions() {
        return getResolutionDimensions(getResolution(), isFullscreen());
    }

    public static Dimension getResolutionDimensions(RESOLUTION resolution, boolean fullscreen) {
        String[] parts = resolution.toString().substring(1).
                split("x");
        Integer w =
                NumberUtils.getInteger(
                        parts[0]);
        Integer h =
                NumberUtils.getInteger(parts[1]);
//        if (Gdx.graphics.getDisplayMode())
        if (!fullscreen) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (screenSize.width <= w)
                w = w * WIDTH_WINDOWED / 100;
            if (screenSize.height <= h)
                h = h * HEIGHT_WINDOWED / 100;
        }
        return new Dimension(w, h);
    }

    public static ScreenViewport getMainViewport() {
        return mainViewport;
    }

    public static void setMainViewport(ScreenViewport mainViewport) {
        ScreenMaster.mainViewport = mainViewport;
    }

    public static GameScreen getScreen() {
        if (launcher == null) {
            return null;
        }
        return launcher.gameScreen;
    }

    public static RESOLUTION getResolution() {
        if (resolution == null) {
            resolution = GDX.getCurrentResolution();
        }
        return resolution;
    }

    public static void setResolution(RESOLUTION resolution) {
        GdxMaster.CUSTOM_RESOLUTION = resolution != RESOLUTION._1920x1080;
        if (resolution != null) {
            if (resolution != getResolution()) {
                if (Eidolons.getScope() != null)
                    if (Eidolons.getScope() != Eidolons.SCOPE.MENU) {
                        EUtils.onConfirm(
                                "New resolution will be applied on restart... Ok?", true, () ->
                                        OptionsMaster.saveOptions());
                        return;
                    }
                getApplication().getGraphics().setResizable(true);
                ScreenMaster.resolution = resolution;
                Dimension dimension = getResolutionDimensions(resolution, fullscreen);
                Integer w = (int)
                        dimension.getWidth();
                Integer h = (int)
                        dimension.getHeight();
                GdxMaster.setWidth(w);
                GdxMaster.setHeight(h);
                if (Gdx.graphics.getWidth() != w || Gdx.graphics.getHeight() != h)
                    Gdx.graphics.setWindowedMode(w,
                            h);
                getApplication().getApplicationListener().resize(w, h);
                getApplication().getGraphics().setResizable(false);
                //            getMainViewport().setScreenSize(w, h);
                GdxMaster.resized();
            }
        }
    }

    public static void screenSet(SCREEN_TYPE type) {
        if (screenType != null)
            previousScreenType = screenType;
        screenType = type;
    }

    public static SCREEN_TYPE getScreenType() {
        return screenType;
    }

    public static SCREEN_TYPE getPreviousScreenType() {
        return previousScreenType;
    }

    public static GridPanel getDungeonGrid() {
        if (screen instanceof GenericDungeonScreen) {
            return ((GenericDungeonScreen) screen).getGridPanel();
        }
        return null;
    }
}
