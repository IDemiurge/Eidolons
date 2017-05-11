package main.test.frontend;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import main.client.dc.Launcher;
import main.game.core.game.DC_Game;
import main.libgdx.screens.IntroScreen;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;

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
        db = Nitrite.builder()
                .compressed()
                .filePath("/tmp/test.db")
                .openOrCreate("user", "password");
        final ObjectRepository<LwjglApplicationConfiguration> repository = db.getRepository(LwjglApplicationConfiguration.class);
        LwjglApplicationConfiguration configuration = repository.find().firstOrDefault();
        if (configuration == null) {
            configuration = getConf();
            repository.update(configuration, true);
        }
        new LwjglApplication(new DemoLauncher(), configuration);
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
        setScreen(new IntroScreen());
    }
}
