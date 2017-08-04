package main.game.core.launch;

import main.game.core.game.DC_Game;
import main.game.core.game.GameFactory;
import main.game.core.game.GameFactory.GAME_SUBCLASS;
import main.game.core.state.Loader;
import main.test.Preset;

/**
 * Created by JustMe on 8/3/2017.
 */
public class GameLauncher {
    private GAME_SUBCLASS gameClass=GAME_SUBCLASS.TEST;

    public GameLauncher(GAME_SUBCLASS gameClass) {
        this.gameClass = gameClass;
    }

    public   DC_Game launchPreset(String presetPath) {
        DC_Game game = initGame();
        PresetLauncher. launchPreset(new Preset("", presetPath));
        game.start(true);
        return game;
    }

    public   DC_Game launchSavedGame(String savePath) {
        return Loader.loadNewGame(savePath);
    }
    public DC_Game initGame() {
        DC_Game game= GameFactory.createGame(gameClass);
        game.init();
        game.dungeonInit();
        game.battleInit();
        return game;
    }
}
