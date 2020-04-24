package tests;

import eidolons.game.battlecraft.DC_Engine;

/**
 * Created by JustMe on 4/7/2018.
 */
public class MultiLauncher {

    String playerParty, enemyParty, dungeonPath;
    Runnable afterEngineInit;
    private boolean testMeta;

    public MultiLauncher(String playerParty, String enemyParty, String dungeonPath) {
        this.playerParty = playerParty;
        this.enemyParty = enemyParty;
        this.dungeonPath = dungeonPath;
    }

    public MultiLauncher(String playerParty, String enemyParty, String dungeonPath, boolean testMeta) {
        this.playerParty = playerParty;
        this.enemyParty = enemyParty;
        this.dungeonPath = dungeonPath;
        this.testMeta = testMeta;
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

//     TODO
//      TestLauncher launcher = new TestLauncher(GAME_SUBCLASS.TEST);
//        launcher.setFAST_MODE(false);
//        launcher.setSUPER_FAST_MODE(false);
//        launcher.PARTY_CODE = CODE.PRESET;
//        launcher.ENEMY_CODE = CODE.PRESET;
//        launcher.PLAYER_PARTY = playerParty;
//        launcher.ENEMY_PARTY = enemyParty;
//        launcher.setDungeon(dungeonPath);
//        FAST_DC.setLauncher(launcher);
//        DC_Game game = launcher.initDC_Game();
//        if (isTestMeta())
//            game.setMetaMaster(new TestMetaMaster());
//        else if (dungeonPath != null) {
//            game.setMetaMaster(new ScenarioMetaMaster(dungeonPath));
//        }
//
//        game.start(true);
    }


    public void setAfterEngineInit(Runnable afterEngineInit) {
        this.afterEngineInit = afterEngineInit;
    }

    public boolean isTestMeta() {
        return testMeta;
    }

    public void setTestMeta(boolean testMeta) {
        this.testMeta = testMeta;
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
