package eidolons.game.core.launch;

import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.GameFactory;
import eidolons.game.core.game.GameFactory.GAME_SUBCLASS;
import eidolons.game.core.state.Loader;
import eidolons.test.Preset;
import eidolons.test.PresetMaster;

/**
 * Created by JustMe on 8/3/2017.
 */
public class GameLauncher extends TestLauncher {
    private GAME_SUBCLASS gameClass = GAME_SUBCLASS.TEST;

    public GameLauncher(GAME_SUBCLASS gameClass) {
        super(gameClass);
    }

    public GameLauncher(DC_Game game) {
        super(game, null, null);
    }

    public DC_Game launchPreset(String presetPath) {
        Preset p = PresetMaster.loadPreset(presetPath);
        PresetMaster.setPreset(p);
        if (game == null) {
            game = initGame();
        } else {
            game.dungeonInit();
            initData();
            game.battleInit();
        }

        game.start(true);
        return game;
    }

    @Override
    public void initFlags() {

    }

    @Override
    public void initLaunch() {

    }

    public DC_Game launchSavedGame(String savePath) {
        return Loader.loadNewGame(savePath);
    }

    public DC_Game initGame() {
        this.game = GameFactory.createGame(gameClass);

        game.init();
        game.dungeonInit();
        initData();
        game.battleInit();
        return game;
    }
}
