package gdx;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.content.consts.Images;
import eidolons.content.consts.VisualEnums;
import eidolons.system.options.GraphicsOptions;
import eidolons.system.options.OptionsMaster;
import gdx.general.AScreen;
import gdx.general.Textures;
import libgdx.GdxEvents;
import libgdx.GdxMaster;
import libgdx.assets.Assets;
import libgdx.assets.texture.TextureCache;
import libgdx.screens.GameScreen;
import libgdx.screens.generic.ScreenWithAssets;
import libgdx.screens.generic.ScreenWithLoader;
import libgdx.screens.handlers.ScreenLoader;
import libgdx.screens.handlers.ScreenMaster;
import libgdx.video.VideoMaster;
import logic.AphosEngine;
import main.data.filesys.PathFinder;
import main.system.GuiEventManager;
import main.system.auxiliary.EnumMaster;
import main.system.launch.CoreEngine;
import main.system.launch.Flags;

import java.awt.*;
import java.util.Map;

public class DemoApp extends Game {

    public static final int FRAMERATE = 60;
    private ScreenLoader screenLoader;
    public GameScreen gameScreen;
    protected ScreenViewport viewport;
    protected boolean fullscreen;
    private final Map<String, Object> launchParameters;

    public DemoApp(Map<String, Object> values) {
        this.launchParameters = values;
        fullscreen = (boolean) launchParameters.get("fullscreen");
    }

    public void start() {
        ScreenMaster.setApplication(new LwjglApplication(this,
                getConf()));
    }
    @Override
    public void create() {
        Textures.init(); //will we use the same?
        GdxMaster.setLoadingCursor();
        GuiEventManager.setManager(new GdxEvents());
        Camera camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        ScreenMaster.setMainViewport(viewport);
        AphosEngine.start();

        Screen combatScreen = new AScreen();
        setScreen(combatScreen);
    }

    protected String getTitle() {
        return "APHOS v" + AphosDemo.VERSION +
                (Flags.isIDE() ? " [IDE]" : "") +
                (Flags.isJar() ? " [JAR]" : "");
    }


    public LwjglApplicationConfiguration getConf() {
        LwjglApplicationConfiguration conf = new LwjglApplicationConfiguration();
        conf.title = getTitle();
        //if (Gdx.graphics.isGL30Available())
        //conf.useGL30 = true;
        conf.resizable = false;
        conf.samples = 4;
//        if (!CoreEngine.isLevelEditor())
//            fullscreen = OptionsMaster.getGraphicsOptions().getBooleanValue(GraphicsOptions.GRAPHIC_OPTION.FULLSCREEN);
//        if (Flags.isGraphicTestMode()) {
//            fullscreen = true;
//        }
        conf.fullscreen = fullscreen;
        conf.foregroundFPS = FRAMERATE;
        if (!Flags.isJar())
            conf.backgroundFPS = isStopOnInactive() ? 1 : FRAMERATE;
        else
            conf.backgroundFPS = isStopOnInactive() ? -1 : FRAMERATE;
        // conf.vSyncEnabled = OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.VSYNC);
        initResolution(conf);
        initIcon(conf);

        ////TODO could break?
        conf.vSyncEnabled = false;
        return conf;
    }

    private boolean isStopOnInactive() {
        return true;
    }

    protected void initIcon(LwjglApplicationConfiguration conf) {

        try {
            if (CoreEngine.isLevelEditor()) {
                //                conf.addIcon(PathFinder.getImagePath() + Images.LOGO_EDITOR_32, FileType.Absolute);
                conf.addIcon(PathFinder.getImagePath() + Images.LOGO_EDITOR_64, Files.FileType.Absolute);
                conf.addIcon(PathFinder.getImagePath() + Images.LOGO_EDITOR_32, Files.FileType.Absolute);
            } else {
                conf.addIcon(PathFinder.getImagePath() + Images.LOGO32, Files.FileType.Absolute);
                conf.addIcon(PathFinder.getImagePath() + Images.LOGO64, Files.FileType.Absolute);
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
                VisualEnums.RESOLUTION resolution =
                        new EnumMaster<VisualEnums.RESOLUTION>().retrieveEnumConst(VisualEnums.RESOLUTION.class,
                                OptionsMaster.getGraphicsOptions().getValue(GraphicsOptions.GRAPHIC_OPTION.RESOLUTION));
                if (resolution == null) {
                    resolution = VisualEnums.RESOLUTION._1920x1080;
                }
                Dimension dimension = ScreenMaster.getResolutionDimensions(resolution, fullscreen);
                Integer w = (int)
                        dimension.getWidth();
                Integer h = (int)
                        dimension.getHeight();
                conf.width = w;
                conf.height = h;
                if (Flags.isIDE()){
                    conf.width = (int) (w*1.25f);
                    conf.height = (int) (h*1.2f);
                }
                if (w < 1500)
                    conf.useGL30 = false;
                System.out.println("resolution width " + w);
                System.out.println("resolution height " + h);

            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }
    //////////////////////////////

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
//        GdxTimeMaster.act(Gdx.graphics.getDeltaTime()); ???

        GuiEventManager.processEvents();
        super.render();
        //TODO do we need this?
        if (!Assets.get().getManager().update()) {
            main.system.auxiliary.log.LogMaster.devLog("Assets being loaded...");
            if (getScreen() instanceof ScreenWithLoader) {
                ((ScreenWithLoader) getScreen()).loadTick();
            }
        } else {
//            getScreen().assetsLoaded();
        }
    }

    @Override
    public Screen getScreen() {
        return super.getScreen();
    }

    @Override
    public void setScreen(Screen screen) {
        super.setScreen(screen);
        if (screen instanceof GameScreen) {
//            ScreenApiImpl api = new ScreenApiImpl(gameScreen);
//            this.gameScreen = (GameScreen) screen;
//            GdxAdapter.getInstance().setScreen(api);
//            if (screen instanceof DungeonScreen){
//                GdxAdapter.getInstance().setDungeon(api);
//            }
        }
    }
}
