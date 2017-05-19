package main.test.frontend;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import main.client.dc.Launcher;
import main.game.core.game.DC_Game;
import main.libgdx.Engine;
import main.libgdx.EngineEmulator;
import main.libgdx.ScreenData;
import main.libgdx.screens.*;
import org.dizitart.no2.Nitrite;
import org.jdeferred.Deferred;
import org.jdeferred.impl.DeferredObject;

public class DemoLauncher extends Game {
    private static Nitrite db;
    private DC_Game coreGame;
    private Engine engine;
    private ScreenData newMeta;

    public DemoLauncher() {


/*        DC_Engine.systemInit();
        DC_Engine.init();
        coreGame = new DC_Game(false);
        coreGame.init();
        DC_Game.game=(coreGame);
        coreGame.start(true);*/

        engine = new EngineEmulator();

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
        Deferred<Boolean, Boolean, Boolean> deferred = new DeferredObject<>();
        OrthographicCamera camera = new OrthographicCamera();
        Viewport viewport = new ScreenViewport(camera);
        final IntroScreen introScreen = new IntroScreen(viewport, () -> {
            final Screen old = getScreen();
            final MainMenuScreen mainScreen = new MainMenuScreen(viewport, s -> newMeta = engine.getMeta(s));
            deferred.done(aBoolean -> mainScreen.hideLoader());
            setScreen(mainScreen);
            if (old != null) {
                old.dispose();
            }
        });
        engine.init(() -> deferred.resolve(true));
        setScreen(introScreen);
    }

    @Override
    public void render() {


        if (newMeta != null) {
            final ScreenData meta = newMeta;
            newMeta = null;
            switch (meta.getType()) {
                case HEADQUARTERS:
                    switchScreen(new HeadquarterScreen(meta), meta);
                    break;
                case BATTLE:
                    switchScreen(new DungeonScreen(meta), meta);
                    break;
                case PRE_BATTLE:
                    break;
            }
        }
        super.render();
    }

    private void switchScreen(ScreenWithLoader screen, ScreenData meta) {
        engine.load(meta, screen::hideLoader);
        final Screen oldScreen = getScreen();
        setScreen(screen);
        oldScreen.dispose();
    }
}
