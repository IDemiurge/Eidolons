package main.test.frontend;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import main.client.dc.Launcher;
import main.game.core.game.DC_Game;
import main.libgdx.Engine;
import main.libgdx.EngineEmulator;
import main.libgdx.ScreenData;
import main.libgdx.screens.DungeonScreen;
import main.libgdx.screens.HeadquarterScreen;
import main.libgdx.screens.MainMenuScreen;
import main.libgdx.screens.ScreenWithLoader;
import main.libgdx.stage.MainMenuStage;
import main.system.GuiEventManager;
import org.dizitart.no2.Nitrite;
import org.jdeferred.Deferred;
import org.jdeferred.impl.DeferredObject;

import java.util.function.Supplier;

import static main.system.GuiEventType.SWITCH_SCREEN;

public class DemoLauncher extends Game {
    private static Nitrite db;
    private DC_Game coreGame;
    private Engine engine;
    private ScreenData newMeta;
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
        Deferred<Boolean, Boolean, Boolean> deferred = new DeferredObject<>();
        OrthographicCamera camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        final MainMenuStage introScreen = new MainMenuStage();
        engine.init(() -> deferred.resolve(true));


        GuiEventManager.bind(SWITCH_SCREEN, obj -> {
            newMeta = (ScreenData) obj.get();
        });
    }

    @Override
    public void render() {
        if (newMeta != null) {
            final ScreenData meta = newMeta;
            newMeta = null;
            switch (meta.getType()) {
                case HEADQUARTERS:
                    switchScreen(HeadquarterScreen::new, meta);
                    break;
                case BATTLE:
                    switchScreen(DungeonScreen::new, meta);
                    break;
                case PRE_BATTLE:
                    break;
                case MAIN_MENU:
                    switchScreen(MainMenuScreen::new, meta);
                    break;
            }
        }
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
