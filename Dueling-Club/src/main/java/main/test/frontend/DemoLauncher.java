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
import main.libgdx.screens.IntroScreen;
import main.libgdx.screens.MainMenuScreen;
import org.dizitart.no2.Nitrite;

public class DemoLauncher extends Game {
    private static Nitrite db;
    private DC_Game coreGame;

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

//        conf.width = GuiManager.getScreenWidthInt();
//        conf.height = GuiManager.getScreenHeightInt();
//        conf.fullscreen = true;
        return conf;
    }

    @Override
    public void create() {
        OrthographicCamera camera = new OrthographicCamera();
        Viewport viewport = new ScreenViewport(camera);
        final IntroScreen introScreen = new IntroScreen(viewport, () -> {
            final Screen old = getScreen();
            setScreen(new MainMenuScreen(viewport));
            if (old != null) {
                old.dispose();
            }
        });
        setScreen(introScreen);
    }

    @Override
    public void render() {
        super.render();
    }
}
