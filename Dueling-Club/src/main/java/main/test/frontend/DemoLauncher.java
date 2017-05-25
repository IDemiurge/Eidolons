package main.test.frontend;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import main.client.dc.Launcher;
import main.game.core.game.DC_Game;
import main.libgdx.EngineEmulator;
import main.libgdx.screens.*;
import main.system.EngineEventManager;
import main.system.EngineEventType;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import org.dizitart.no2.Nitrite;

import java.util.function.Supplier;

import static main.system.GuiEventType.SWITCH_SCREEN;

public class DemoLauncher extends Game {
    private static Nitrite db;
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

        new LwjglApplication(new DemoLauncher(), getConf());
    }

    private static LwjglApplicationConfiguration getConf() {
        LwjglApplicationConfiguration conf = new LwjglApplicationConfiguration();
        conf.title = "Eidolons: Battlecraft v" + Launcher.VERSION;
        conf.useGL30 = true;

        conf.width = 1600;
        conf.height = 900;
        conf.fullscreen = false;
        conf.resizable = false;

        return conf;
    }

    @Override
    public void create() {
        engine = new EngineEmulator();
        EngineEventManager.trigger(EngineEventType.LOAD_MAIN_SCREEN, ScreenType.MAIN_MENU);
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);

        GuiEventManager.bind(SWITCH_SCREEN, this::screenSwitcher);
    }

    private void screenSwitcher(EventCallbackParam param) {
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
    public void render() {
        GuiEventManager.processEvents();

        super.render();
    }

    private void switchScreen(Supplier<ScreenWithLoader> factory, ScreenData meta) {
        final ScreenWithLoader newScreen = factory.get();
        newScreen.setViewPort(viewport);
        newScreen.setData(meta);
        final Screen oldScreen = getScreen();
        setScreen(newScreen);

        if (oldScreen != null) {
            oldScreen.dispose();
        }
    }
}
