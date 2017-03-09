package init;

import main.client.DC_Engine;
import main.game.core.game.DC_Game;
import main.system.auxiliary.log.LogMaster;
import main.test.PresetLauncher;
import main.test.PresetLauncher.LAUNCH;
import main.test.debug.GameLauncher;
import main.test.debug.GameLauncher.CODE;
import res.JUnitResources;
import tests.JUnitTests;

/**
 * Created by JustMe on 3/6/2017.
 */
public class JUnitDcInitializer {

    public static DC_Game game;
    private  JUnitTests tests;



    public JUnitDcInitializer( ) {

        //        PathFinder.setJUnitMode(true); to find all test/resources

        LogMaster.setOff(true); //log everything* or nothing to speed up
        DC_Engine.systemInit();
        DC_Engine.gameInit(false);
        JUnitResources.init();

        LAUNCH launch = PresetLauncher.initLaunch(LAUNCH.JUnit.name());
//        if (JUnitTests.itemGenerationOff)
//            launch.itemGenerationOff=true; TODO other flags
        launch.ENEMY_CODE= CODE.NONE;
        launch.PARTY_CODE=CODE.NONE;
        GameLauncher launcher = new GameLauncher(null , null);
        game = launcher.initDC_Game();
        game.start(true); //TODO
    }
}
