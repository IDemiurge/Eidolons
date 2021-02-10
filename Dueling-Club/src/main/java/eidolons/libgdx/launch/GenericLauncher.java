package eidolons.libgdx.launch;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxEvents;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.assets.Assets;
import eidolons.libgdx.screens.GameScreen;
import eidolons.libgdx.screens.ScreenMaster;
import eidolons.libgdx.screens.ScreenWithAssets;
import eidolons.libgdx.screens.ScreenWithLoader;
import eidolons.libgdx.screens.dungeon.DungeonScreen;
import eidolons.libgdx.screens.load.ScreenLoader;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import eidolons.libgdx.utils.GdxTimeMaster;
import eidolons.libgdx.video.VideoMaster;
import eidolons.system.audio.MusicEnums;
import eidolons.system.audio.MusicMaster;
import eidolons.system.data.MetaDataUnit;
import eidolons.system.data.MetaDataUnit.META_DATA;
import eidolons.system.graphics.RESOLUTION;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;

import java.awt.*;

/**
 * Created by JustMe on 11/30/2017.
 */
public abstract class GenericLauncher extends Game {
    public static final int FRAMERATE = 60;
    private ScreenLoader screenLoader;
    public GameScreen gameScreen;
    protected ScreenViewport viewport;
    protected boolean fullscreen;
    public static GenericLauncher instance;
    private GLProfiler profiler;
    private boolean logProfiler;


    public abstract String getOptionsPath();

    @Override
    public void create() {
        instance = this;
        TextureCache.getInstance();
        GdxMaster.setLoadingCursor();
        //move
        MusicMaster.preload(MusicEnums.MUSIC_SCOPE.MENU);
        MusicMaster.getInstance().scopeChanged(MusicEnums.MUSIC_SCOPE.MENU);
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        ScreenMaster.setMainViewport(viewport);
        screenLoader.screenInit();

        profiler = new GLProfiler(Gdx.graphics);
        GuiEventManager.bind(GuiEventType.TOGGLE_LOG_GL_PROFILER, p -> {
            logProfiler = !logProfiler;
            if (logProfiler)
                profiler.enable();
            else
                profiler.disable();
        });
        GuiEventManager.bind(GuiEventType.LOG_DIAGNOSTICS, p -> {
            logProfiler();
            TextureCache.getInstance().logDiagnostics();
        });
    }

    protected ScreenLoader createScreenLoader() {
        return new ScreenLoader(this);
    }

    private void logProfiler() {
        System.out.println("  Drawcalls: " + profiler.getDrawCalls() +
                ", Calls: " + profiler.getCalls() +
                ", TextureBindings: " + profiler.getTextureBindings() +
                ", ShaderSwitches:  " + profiler.getShaderSwitches() +
                "vertexCount: " + profiler.getVertexCount().value);
        profiler.reset();
    }

    public void start() {
        GuiEventManager.setManager(new GdxEvents());
        screenLoader = createScreenLoader();
        screenLoader.engineInit();
        if (CoreEngine.isGraphicsOff())
            return;
        ScreenMaster.setApplication(new LwjglApplication(this,
                getConf()));
        if (!CoreEngine.isLevelEditor())
            OptionsMaster.applyGraphicsOptions();
        Eidolons.setLauncher(this);
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
        return Flags.isMe(); //TODO this messes up loading ?
    }

    protected String getTitle() {
        return "Eidolons: Netherflame v" + CoreEngine.VERSION +
                (Flags.isIDE() ? "IDE" : "");
    }


    public LwjglApplicationConfiguration getConf() {
        LwjglApplicationConfiguration conf = new LwjglApplicationConfiguration();
        conf.title = getTitle();
        //if (Gdx.graphics.isGL30Available())
        //conf.useGL30 = true;
        conf.resizable = false;
        conf.samples = 4;
        conf.fullscreen = false;
        if (!CoreEngine.isLevelEditor())
            fullscreen = OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.FULLSCREEN);
        if (Flags.isGraphicTestMode()) {
            fullscreen = true;
        }
        conf.foregroundFPS = FRAMERATE;
        if (!Flags.isJar())
            conf.backgroundFPS = isStopOnInactive() ? 1 : FRAMERATE;
        else
            conf.backgroundFPS = isStopOnInactive() ? -1 : FRAMERATE;
        // conf.vSyncEnabled = OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.VSYNC);
        initResolution(conf);
        initIcon(conf);

        return conf;
    }

    protected void initIcon(LwjglApplicationConfiguration conf) {

        try {
            if (CoreEngine.isLevelEditor()) {
                //                conf.addIcon(PathFinder.getImagePath() + Images.LOGO_EDITOR_32, FileType.Absolute);
                conf.addIcon(PathFinder.getImagePath() + Images.LOGO_EDITOR_64, FileType.Absolute);
                conf.addIcon(PathFinder.getImagePath() + Images.LOGO_EDITOR_32, FileType.Absolute);
            } else {
                conf.addIcon(PathFinder.getImagePath() + Images.LOGO32, FileType.Absolute);
                conf.addIcon(PathFinder.getImagePath() + Images.LOGO64, FileType.Absolute);
            }
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
            conf.width = 1920;
            conf.height = 1080;
            try {
                RESOLUTION resolution =
                        new EnumMaster<RESOLUTION>().retrieveEnumConst(RESOLUTION.class,
                                OptionsMaster.getGraphicsOptions().getValue(GRAPHIC_OPTION.RESOLUTION));
                if (resolution == null) {
                    resolution = RESOLUTION._1920x1080;
                }
                Dimension dimension = ScreenMaster.getResolutionDimensions(resolution, fullscreen);
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

            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }


    @Override
    public void resize(int width, int height) {
        if (viewport == null)
            return;
        viewport.update(width, height);

        if (gameScreen != null) gameScreen.resize(width, height);
        else getScreen().resize(width, height);

        if (VideoMaster.player != null) {
            VideoMaster.player.resize(width, height);
        }
    }

    @Override
    public void render() {
        GdxTimeMaster.act(Gdx.graphics.getDeltaTime());
        if (VideoMaster.player != null) {
            if (getScreen() instanceof DungeonScreen) {
                VideoMaster.player.stop();
                resize((int) (1920 / ((float) Gdx.graphics.getWidth() / 1920)),
                        (int) (1050 / ((float) Gdx.graphics.getHeight() / 1050)));
                resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            } else {
                VideoMaster.player.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }
        }
            try {
                render_();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                if (!Flags.isIDE()) {
                    main.system.auxiliary.log.LogMaster.important("Game Crashed! " );
                    System.exit(-1);
                }
            }
            if (logProfiler) {
                logProfiler();
            }
    }

    public void render_() {
        GuiEventManager.processEvents();
        super.render();
        if (!Assets.get().getManager().update()) {
            main.system.auxiliary.log.LogMaster.devLog("Assets being loaded...");
            if (getScreen() instanceof ScreenWithLoader) {
                ((ScreenWithLoader) getScreen()).loadTick();
            }
        } else {
            getScreen().assetsLoaded();
        }
    }

    @Override
    public ScreenWithAssets getScreen() {
        return (ScreenWithAssets) super.getScreen();
    }

    @Override
    public void setScreen(Screen screen) {
        super.setScreen(screen);
        if (screen instanceof GameScreen)
            this.gameScreen = (GameScreen) screen;
    }

}
