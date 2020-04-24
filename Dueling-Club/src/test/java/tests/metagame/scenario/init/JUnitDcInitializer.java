package tests.metagame.scenario.init;

import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 3/6/2017.
 */
public class JUnitDcInitializer {

    public DC_Game game;


    public void launchFastDc() {
        game = Eidolons.game;
    }

    public JUnitDcInitializer() {
        //        PathFinder.setJUnitMode(true); to find all test/resources
        LogMaster.setOff(true); //log everything* or nothing to speed up
        CoreEngine.setGraphicsOff(true);
        launchFastDc();
//        DC_Engine.systemInit();
//        DC_Engine.gameInit(false);
//        JUnitResources.init();
//        LAUNCH launch = PresetLauncher.initLaunch(LAUNCH.JUnit.name());
////        if (JUnitTests.itemGenerationOff)
////            launch.itemGenerationOff=true; TODO other flags
//        launch.ENEMY_CODE= CODE.NONE;
//        launch.PARTY_CODE=CODE.NONE;
//
//        GameLauncher launcher = new GameLauncher(null , null);
//        launcher.PLAYER_PARTY = "";
//        game = launcher.initDC_Game();
//        game.start(true); //TODO
//        game.setStarted(true);
    }
}
