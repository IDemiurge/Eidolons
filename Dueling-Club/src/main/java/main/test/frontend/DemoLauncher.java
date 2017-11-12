package main.test.frontend;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import main.client.cc.logic.items.ItemGenerator;
import main.client.dc.Launcher;
import main.data.filesys.PathFinder;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.libgdx.EngineEmulator;
import main.libgdx.gui.menu.MainMenuScreen;
import main.libgdx.screens.*;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;
import main.system.options.GraphicsOptions.GRAPHIC_OPTION;
import main.system.options.OptionsMaster;
import org.dizitart.no2.Nitrite;

import java.util.function.Supplier;

import static main.system.GuiEventType.SCREEN_LOADED;
import static main.system.GuiEventType.SWITCH_SCREEN;

public class DemoLauncher extends Game {
    public static final int FRAMERATE = 60;
    private static Nitrite db;
    private static String quickTypes =
     "units;bf obj;terrain;missions;places;scenarios;party;";
    private DC_Game coreGame;
    private EngineEmulator engine;
    private ScreenViewport viewport;

    public DemoLauncher() {


/*        DC_Engine.systemInit();
        DC_Engine.init();
        coreGame = new DC_Game(false);
        coreGame.init();
        DC_Game.game=(coreGame);
        coreGame.start(true);*/
    }

    public static void initQuickLaunch() {
        CoreEngine.setSelectivelyReadTypes(quickTypes);
        ItemGenerator.setGenerationOn(false);

    }

    public static void main(String[] args) {
/*        db = Nitrite.builder()
                .compressed()
                .filePath(PathFinder.getXML_PATH() + "test.db")
                .openOrCreate("user", "password");
        final ObjectRepository<LwjglApplicationConfiguration> repository = db.getRepository(LwjglApplicationConfiguration.class);
        LwjglApplicationConfiguration configuration = repository.find().firstOrDefault();
        if (configuration == null) {
            configuration = getConf();
            repository.insert(configuration);
        }*/
        Eidolons.setApplication(new LwjglApplication(new DemoLauncher(), getConf()));

    }

    protected static LwjglApplicationConfiguration getConf() {
//        Eidolons. getApplication().getGraphics().setFullscreenMode();
        LwjglApplicationConfiguration conf = new LwjglApplicationConfiguration();
        conf.title = "Eidolons: Battlecraft v" + Launcher.VERSION;
//        if (Gdx.graphics.isGL30Available())
        conf.useGL30 = true;
        conf.resizable = true;
        OptionsMaster.init();

        conf.fullscreen = //false;
         OptionsMaster.getGraphicsOptions().getBooleanValue(GRAPHIC_OPTION.FULLSCREEN);

        conf.foregroundFPS=FRAMERATE;
        conf.backgroundFPS=isStopOnInactive()? -1 : FRAMERATE;
        conf.width = 1600;
        conf.height = 900;


//        System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
//        conf.width   = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
//        conf.height =   LwjglApplicationConfiguration.getDesktopDisplayMode().height;
        try {
            RESOLUTION resolution =
             new EnumMaster<RESOLUTION>().retrieveEnumConst(RESOLUTION.class,
              OptionsMaster.getGraphicsOptions().getValue(GRAPHIC_OPTION.RESOLUTION));
            if (resolution != null) {
                String[] parts = resolution.toString().substring(1).
                 split("x");
                Integer w =
                 StringMaster.getInteger(
                  parts[0]);
                Integer h =
                 StringMaster.getInteger(parts[1]);
                if (!conf.fullscreen){
                    w=w*95/100;
                    h=h*90/100;
                }
                conf.width = w;
                conf.height = h;
                if (w<1500)
                    conf.useGL30 = false;
                System.out.println("resolution width "+w );
                System.out.println("resolution height "+h );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            conf.addIcon(PathFinder.getImagePath() + "mini/new/logo32.png", FileType.Absolute);
            conf.addIcon(PathFinder.getImagePath() + "mini/new/logo64.png", FileType.Absolute);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conf;
    }

    private static boolean isStopOnInactive() {
        return true;
    }

    @Override
    public void create() {
        GuiEventManager.bind(SWITCH_SCREEN, this::screenSwitcher);
        GuiEventManager.bind(SCREEN_LOADED, this::onScreenLoadDone);
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
//if (!Showcase.isRunning())
        initEngine();
    }

    protected void initEngine() {
        engine = new EngineEmulator();
    }

    public void onScreenLoadDone(EventCallbackParam param) {
        if (getScreen() == null)
            return;
        else {
            ((ScreenWithLoader) getScreen()).loadDone(param);
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
                case PRE_BATTLE:
                    break;
                case MAIN_MENU:
                    switchScreen(MainMenuScreen::new, newMeta);
                    break;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void render() {
        GuiEventManager.processEvents();

        super.render();
    }

    private void switchScreen(Supplier<ScreenWithLoader> factory, ScreenData meta) {
        final ScreenWithLoader newScreen = factory.get();
        newScreen.initLoadingStage(meta);
        newScreen.setViewPort(viewport);
        newScreen.setData(meta);
        final Screen oldScreen = getScreen();
        setScreen(newScreen);

        if (oldScreen != null) {
            oldScreen.dispose();
        }
    }
}
