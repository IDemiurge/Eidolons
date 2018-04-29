package tests;

import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.GameFactory.GAME_SUBCLASS;
import eidolons.game.core.launch.TestLauncher;
import eidolons.game.core.launch.TestLauncher.CODE;
import eidolons.test.frontend.FAST_DC;
import org.junit.Before;

/**
 * Created by JustMe on 4/7/2018.
 */
public class MultiLauncher {
    String playerParty, enemyParty, dungeonPath;
    Runnable afterEngineInit;

    public MultiLauncher(String playerParty, String enemyParty, String dungeonPath) {
        this.playerParty = playerParty;
        this.enemyParty = enemyParty;
        this.dungeonPath = dungeonPath;
    }

    public static void launch(String playerParty, String enemyParty, String dungeonPath) {
        new MultiLauncher(playerParty, enemyParty, dungeonPath).launch();
    }

    public void launch() {
        DC_Engine.fullInit();
//        onEngine
        if (afterEngineInit != null) {
            afterEngineInit.run();
        }

        TestLauncher launcher = new TestLauncher(GAME_SUBCLASS.TEST);
        launcher.setFAST_MODE(false);
        launcher.setSUPER_FAST_MODE(false);
        launcher.PARTY_CODE = CODE.PRESET;
        launcher.ENEMY_CODE = CODE.PRESET;
        launcher.PLAYER_PARTY = playerParty;
        launcher.ENEMY_PARTY = enemyParty;
        launcher.setDungeon(dungeonPath);
        FAST_DC.setLauncher(launcher);
        DC_Game game = launcher.initDC_Game();
        game.start(true);
    }

    public void setAfterEngineInit(Runnable afterEngineInit) {
        this.afterEngineInit = afterEngineInit;
    }

    @Before
    public void init() {

    }

    public enum LAUNCH_OPTION {
        FAST_MODE,

        IMMORTAL,
        INVISIBLE,
        CUSTOM_OPTIONS_PATH,
    }


    public enum LAUNCH_TYPE {
        JUNIT,
        AI_TRAIN,
        MANUAL_TEST,
        COMBAT,
        NORMAL,
        MAP,
    }


}
