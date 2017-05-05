package main.test.frontend;

import com.badlogic.gdx.Game;
import main.client.DC_Engine;
import main.game.core.game.DC_Game;
import main.system.hotkey.DC_KeyManager;

public class DemoLauncher {
    private Game frontGame;
    private DC_Game coreGame;
    private DC_KeyManager keyManager;

    public DemoLauncher() {

        frontGame = new Game() {
            @Override
            public void create() {

            }
        };

        DC_Engine.systemInit();

        DC_Engine.init();

        coreGame = new DC_Game(false);
        coreGame.init();
        DC_Game.setGame(coreGame);
        coreGame.start(true);

        keyManager = new DC_KeyManager(coreGame.getManager());
        keyManager.init();

    }

    public static void main(String[] args) {
        new DemoLauncher();
    }
}