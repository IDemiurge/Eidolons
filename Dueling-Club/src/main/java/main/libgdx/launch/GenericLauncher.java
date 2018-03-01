package main.libgdx.launch;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import main.client.dc.Launcher;
import main.data.filesys.PathFinder;
import main.game.battlecraft.DC_Engine;
import main.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import main.game.core.Eidolons;
import main.game.module.adventure.MacroManager;
import main.libgdx.anims.Assets;
import main.libgdx.screens.*;
import main.libgdx.screens.map.MapScreen;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.CoreEngine;
import main.system.options.GraphicsOptions.GRAPHIC_OPTION;
import main.system.options.OptionsMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.test.frontend.RESOLUTION;

import java.awt.*;
import java.util.function.Supplier;

import static main.system.GuiEventType.SCREEN_LOADED;
import static main.system.GuiEventType.SWITCH_SCREEN;

/**
 * Created by JustMe on 11/30/2017.
 */
public class GenericLauncher extends Game {
    public static final int FRAMERATE = 60;
    protected boolean fullscreen;
    protected ScreenViewport viewport;
    private LwjglApplicationConfiguration conf;
    private boolean firstInitDone;

    @Override
    public void create() {
        GuiEventManager.bind(SWITCH_SCREEN, this::screenSwitcher);
        GuiEventManager.bind(SCREEN_LOADED, this::onScreenLoadDone);
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        Eidolons.setMainViewport(viewport);
        engineInit();
        screenInit();
    }

    public void start() {
        engineInit();
        Eidolons.setApplication(new LwjglApplication(this,
         getConf()));
        Eidolons.setLauncher(this);
        if (fullscreen
         ) {
            Eidolons.setFullscreen(true);
        }
    }

    protected void screenInit() {
        ScreenData data = new ScreenData(ScreenType.MAIN_MENU, "Loading...");
        screenSwitcher(new EventCallbackParam(data));
        WaitMaster.receiveInput(WAIT_OPERATIONS.GDX_READY, true);
        WaitMaster.markAsComplete(WAIT_OPERATIONS.GDX_READY);

    }

    @Override
    public void dispose() {
        try {
            SpecialLogger.getInstance().writeLogs();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        super.dispose();
        System.exit(0);

    }

    protected boolean isStopOnInactive() {
        return false;
    }

    public LwjglApplicationConfiguration getConf() {
//        Eidolons. getApplication().getGraphics().setFullscreenMode();
        conf = new LwjglApplicationConfiguration();
        conf.title = getTitle();
//        if (Gdx.graphics.isGL30Available())
        conf.useGL30 = true;
        conf.resizable = false;

        conf.fullscreen = false;
        fullscreen = OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.FULLSCREEN);
        conf.foregroundFPS = FRAMERATE;
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
            conf.addIcon(PathFinder.getImagePath() + "mini/new/logo32.png", FileType.Absolute);
            conf.addIcon(PathFinder.getImagePath() + "mini/new/logo64.png", FileType.Absolute);
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
        return "Eidolons: Battlecraft v" + Launcher.VERSION;
    }

    protected void engineInit() {
        DC_Engine.systemInit();
        OptionsMaster.init();
    }


    @Override
    public void resize(int width, int height) {
//        viewport.update(width, height);
        if (screen != null) screen.resize(width, height);
//        if (VignetteShader.isUsed()) {
//            ShaderProgram program = VignetteShader.getShader();
//      try{
//        program.use();
//        program.setUniformf("resolution", Display.getWidth(), Display.getHeight());
//      }catch(Exception e){main.system.ExceptionMaster.printStackTrace( e);}
//    }
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

    protected void switchScreen(Supplier<ScreenWithVideoLoader> factory, ScreenData meta) {
        final ScreenWithVideoLoader newScreen = factory.get();
        newScreen.initLoadingStage(meta);
        newScreen.setViewPort(viewport);
        newScreen.setData(meta);
        final Screen oldScreen = getScreen();
        setScreen(newScreen);

        if (oldScreen != null) {
            oldScreen.dispose();
        }
        triggerLoaded(meta);
    }

    protected void triggerLoaded(ScreenData meta) {
        switch (meta.getType()) {
            case BATTLE:
                if (firstInitDone)
                    return;
                new Thread(new Runnable() {
                    public void run() {
                        if (!Eidolons.initScenario(new ScenarioMetaMaster(meta.getName())))
                            return;
                        DC_Engine.gameStartInit();
                        Eidolons.mainGame.getMetaMaster().getGame().dungeonInit();
                        Eidolons.mainGame.getMetaMaster().getGame().battleInit();
                        Eidolons.mainGame.getMetaMaster().getGame().start(true);
                        firstInitDone = true;
                    }
                }, " thread").start();
                break;
            case MAIN_MENU:
                GuiEventManager.trigger(SCREEN_LOADED,
                 new ScreenData(ScreenType.MAIN_MENU, null));
                break;
            default:
                GuiEventManager.trigger(SCREEN_LOADED,
                 new ScreenData(meta.getType(), null));
        }
    }

    protected void screenSwitcher(EventCallbackParam param) {
        ScreenData newMeta = (ScreenData) param.get();
        if (newMeta != null) {
            switch (newMeta.getType()) {
                case HEADQUARTERS:
                    switchScreen(HeadquarterScreen::new, newMeta);
                    break;
                case BATTLE:
                    switchScreen(DungeonScreen::new, newMeta);
                    break;
                case MAP:
                    switchScreen(()->MapScreen.getInstance(), newMeta);
                    if (newMeta.getName() != null)
                    MacroManager.setScenario(newMeta.getName());
                    break;
                case PRE_BATTLE:
                    break;
                case MAIN_MENU:
                    switchScreen(AnimatedMenuScreen::new, newMeta);
                    break;
            }
        }
    }

    public void onScreenLoadDone(EventCallbackParam param) {
        if (getScreen() == null)
            return;
        else {
            ((ScreenWithVideoLoader) getScreen()).loadDone(param);
        }
    }
}
