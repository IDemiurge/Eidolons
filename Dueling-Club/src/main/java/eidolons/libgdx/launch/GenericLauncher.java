package eidolons.libgdx.launch;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.core.Eidolons.SCOPE;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.Assets;
import eidolons.libgdx.gui.panels.headquarters.weave.WeaveScreen;
import eidolons.libgdx.screens.*;
import eidolons.libgdx.screens.map.MapScreen;
import eidolons.libgdx.screens.map.layers.Blackout;
import eidolons.libgdx.texture.Images;
import eidolons.macro.AdventureInitializer;
import eidolons.system.audio.MusicMaster;
import eidolons.system.audio.MusicMaster.MUSIC_SCOPE;
import eidolons.system.data.MetaDataUnit;
import eidolons.system.data.MetaDataUnit.META_DATA;
import eidolons.system.graphics.RESOLUTION;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import main.data.filesys.PathFinder;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.awt.*;
import java.util.function.Supplier;

import static main.system.GuiEventType.SCREEN_LOADED;
import static main.system.GuiEventType.SWITCH_SCREEN;

/**
 * Created by JustMe on 11/30/2017.
 */
public class GenericLauncher extends Game {
    public static final int FRAMERATE = 60;
    private static boolean firstInitDone;
    public GameScreen gameScreen;
    protected boolean fullscreen;
    protected ScreenViewport viewport;
    private LwjglApplicationConfiguration conf;

    public static void setFirstInitDone(boolean firstInitDone) {
        GenericLauncher.firstInitDone = firstInitDone;
    }

    @Override
    public void create() {
        GdxMaster.setLoadingCursor();
        MusicMaster.preload(MUSIC_SCOPE.MENU);
        MusicMaster.getInstance().scopeChanged(MUSIC_SCOPE.MENU);
        GuiEventManager.bind(SWITCH_SCREEN, this::trySwitchScreen);
        GuiEventManager.bind(SCREEN_LOADED, this::onScreenLoadDone);
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        Eidolons.setMainViewport(viewport);
        //        if (!CoreEngine.isInitializing() && !CoreEngine.isInitialized()) {
        //            engineInit();
        //        }
        screenInit();
    }

    public void start() {
        engineInit();
        if (CoreEngine.isGraphicsOff())
            return;
        Eidolons.setApplication(new LwjglApplication(this,
         getConf()));

        Eidolons.setLauncher(this);
        Eidolons.setFullscreen(fullscreen);
    }

    protected void screenInit() {
        ScreenData data = new ScreenData(SCREEN_TYPE.MAIN_MENU, "Loading...");
        if (isFirstLoadingScreenShown()) {
            try {
                setScreen(new LoadingScreen());
                render();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN, new EventCallbackParam(data));
        } else {
            trySwitchScreen(new EventCallbackParam(data));
        }

    }

    private boolean isFirstLoadingScreenShown() {
        return false;
    }

    @Override
    public void dispose() {
        try {
            SpecialLogger.getInstance().writeLogs();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        try {
            MetaDataUnit.getInstance().setValue(META_DATA.EXIT, MetaDataUnit.EXIT_OK);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }

        super.dispose();

    }

    protected boolean isStopOnInactive() {
        return  CoreEngine.isFastMode();
    }

    public LwjglApplicationConfiguration getConf() {
        //        Eidolons. getApplication().getGraphics().setFullscreenMode();
        conf = new LwjglApplicationConfiguration();
        conf.title = getTitle();
        //if (Gdx.graphics.isGL30Available())
        //conf.useGL30 = true;
        conf.resizable = false;
        conf.samples = 4;
        conf.fullscreen = false;
        fullscreen = OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.FULLSCREEN);
        conf.foregroundFPS = FRAMERATE;
        if (!CoreEngine.isJar())
            conf.backgroundFPS = isStopOnInactive() ? 1 : FRAMERATE;
        else
            conf.backgroundFPS = isStopOnInactive() ? -1 : FRAMERATE;
        conf.vSyncEnabled = OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.VSYNC);
        initResolution(conf);
        initIcon(conf);

        return conf;
    }

    //        @Override
    public void setForegroundFPS(int value) {
        conf.foregroundFPS = value;
    }

    protected void initIcon(LwjglApplicationConfiguration conf) {
        try {
            conf.addIcon(PathFinder.getImagePath() + Images.LOGO32, FileType.Absolute);
            conf.addIcon(PathFinder.getImagePath() + Images.LOGO64, FileType.Absolute);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    protected void initResolution(LwjglApplicationConfiguration conf) {
        if (fullscreen) {
            //            DisplayMode displayMode = LwjglApplicationConfiguration.getDesktopDisplayMode();
            //            conf.setFromDisplayMode(displayMode);
            System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
            conf.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
            conf.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
            //            conf.fullscreen=true;
            System.out.println("resolution width " + conf.width);
            System.out.println("resolution height " + conf.height);
        } else {
            conf.width = 1600;
            conf.height = 900;
            try {
                RESOLUTION resolution =
                 new EnumMaster<RESOLUTION>().retrieveEnumConst(RESOLUTION.class,
                  OptionsMaster.getGraphicsOptions().getValue(GRAPHIC_OPTION.RESOLUTION));
                if (resolution != null) {
                    Dimension dimension = Eidolons.getResolutionDimensions(resolution, fullscreen);
                    Integer w = (int)
                     dimension.getWidth();
                    Integer h = (int)
                     dimension.getHeight();
                    conf.width = w;
                    conf.height = h;
                    if (w < 1500)
                        conf.useGL30 = false;
                    System.out.println("resolution width " + w);
                    System.out.println("resolution height " + h);
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }

    protected String getTitle() {
        return "Eidolons: Battlecraft v" + CoreEngine.VERSION;
    }

    protected void engineInit() {
        DC_Engine.systemInit();
        OptionsMaster.init();
    }


    @Override
    public void resize(int width, int height) {
        if (viewport == null)
            return;

        viewport.update(width, height);

        if (gameScreen != null) gameScreen.resize(width, height);
        else getScreen().resize(width, height);
    }

    @Override
    public void render() {
        if (CoreEngine.isIDE()) {
            try {
                render_();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        } else
            render_();
    }

    public void render_() {
        GuiEventManager.processEvents();
        super.render();
        Assets.get().getManager().update();

    }

    @Override
    public void setScreen(Screen screen) {
        super.setScreen(screen);
        if (screen instanceof GameScreen)
            this.gameScreen = (GameScreen) screen;
    }

    protected void switchScreen(Supplier<ScreenWithVideoLoader> factory, ScreenData meta) {
        GdxMaster.setLoadingCursor();
        main.system.auxiliary.log.LogMaster.log(1, "switchScreen " + meta.getType());
        Eidolons.screenSet(meta.getType());
        final ScreenWithVideoLoader newScreen = factory.get();
        newScreen.initLoadingStage(meta);
        newScreen.setViewPort(viewport);
        newScreen.setData(meta);
        final Screen oldScreen = getScreen();
        setScreen(newScreen);
        {
            if (oldScreen != null)
                oldScreen.dispose();
        }

        triggerLoaded(meta);
    }

    protected void triggerLoaded(ScreenData data) {
        main.system.auxiliary.log.LogMaster.log(1, "triggerLoaded " + data.getName());
        if (Blackout.isOnNewScreen())
            GuiEventManager.trigger(GuiEventType.FADE_OUT_AND_BACK);
        switch (data.getType()) {
            case BATTLE:
                if (!CoreEngine.isMacro())
                    if (firstInitDone)
                        return;
                Eidolons.onThisOrNonGdxThread(() -> {
                    main.system.auxiliary.log.LogMaster.log(1, "initScenario for dungeon:" + data.getName());
                    if (!Eidolons.initScenario(new ScenarioMetaMaster(data.getName())))
                        return; // INIT FAILED
                    DC_Engine.gameStartInit();
                    MusicMaster.preload(MUSIC_SCOPE.ATMO);
                    Eidolons.mainGame.getMetaMaster().getGame().initAndStart();
                    firstInitDone = true;
                });
                break;
            case MAIN_MENU:
                GuiEventManager.trigger(SCREEN_LOADED,
                 new ScreenData(SCREEN_TYPE.MAIN_MENU, null));
                break;
            default:
                GuiEventManager.trigger(SCREEN_LOADED,
                 new ScreenData(data.getType(), null));
        }
    }

    protected void trySwitchScreen(EventCallbackParam param) {
        if (CoreEngine.isIDE()) {
            try {
                screenSwitcher(param);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                screenSwitcher(new EventCallbackParam(new ScreenData(
                 Eidolons.getPreviousScreenType(), "")));
            }
        } else {
            screenSwitcher(param);
        }
    }

    protected void screenSwitcher(EventCallbackParam param) {
        if (Blackout.isOnNewScreen())
            GuiEventManager.trigger(GuiEventType.FADE_OUT_AND_BACK);
        ScreenData newMeta = (ScreenData) param.get();
        if (newMeta != null) {
            switch (newMeta.getType()) {
                case HEADQUARTERS:
                    switchScreen(HeadquarterScreen::new, newMeta);
                    break;
                case WEAVE:
                    switchScreen(WeaveScreen::getInstance, newMeta);
                    break;
                case BATTLE:
                    switchScreen(DungeonScreen::new, newMeta);
                    Eidolons.setScope(SCOPE.BATTLE);
                    break;
                case MAP:
                    Eidolons.setScope(SCOPE.MAP);
                    switchScreen(() -> MapScreen.getInstance(), newMeta);
                    if (newMeta.getName() != null)
                        AdventureInitializer.setScenario(newMeta.getName());
                    break;
                case PRE_BATTLE:
                    break;
                case MAIN_MENU:
                    Eidolons.setScope(SCOPE.MENU);
                    switchScreen(AnimatedMenuScreen::new, newMeta);
                    WaitMaster.receiveInput(WAIT_OPERATIONS.GDX_READY, true);
                    WaitMaster.markAsComplete(WAIT_OPERATIONS.GDX_READY);
                    break;
            }
        }
    }

    private void initLoadingScreen() {

        //   TODO  different kind of screen...
    }

    private void onScreenLoadDone(EventCallbackParam param) {
        if (getScreen() == null)
            return;
        else {
            ((ScreenWithVideoLoader) getScreen()).loadDone(param);
        }
    }
}
