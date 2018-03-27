package tests;

import main.entity.obj.unit.Unit;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.game.core.launch.PresetLauncher.LAUNCH;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.launch.CoreEngine;
import main.test.frontend.FAST_DC;
import org.junit.Before;

/**
 * Created by JustMe on 3/27/2017.
 */
public class FastDcTest {

    protected DC_Game game;


    protected String getDungeonPath() {
        return null;
    }

    protected String getHeroParty() {
        return null;
    }

    protected String getEnemyParty() {
        return null;
    }

    @Before
    public void init() {
        LogMaster.setOff(isLoggingOff()); //log everything* or nothing to speed up
        CoreEngine.setGraphicsOff(isGraphicsOff());

        FAST_DC.main(new String[]{
         FAST_DC.PRESET_OPTION_ARG + StringMaster.wrapInParenthesis(LAUNCH.JUnit.name()),
         getHeroParty(),
         getEnemyParty(),
         getDungeonPath()
        });
        game = Eidolons.game;
        //        PathFinder.setJUnitMode(true); to find all test/resources

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

    protected boolean isLoggingOff() {
        return true;
    }

    protected boolean isGraphicsOff() {
        return true;
    }

    protected Unit getHero() {
        return game.getManager().getMainHero();
    }
}
