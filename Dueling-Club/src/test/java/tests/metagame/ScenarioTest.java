package tests.metagame;

import main.game.battlecraft.DC_Engine;
import main.game.core.Eidolons;
import main.system.launch.CoreEngine;
import main.test.frontend.GdxLauncher;
import org.junit.Test;

/**
 * Created by JustMe on 5/16/2017.
 */
public class ScenarioTest  {

    private String typeName="Pride and Treachery";

    @Test
    public void test(){
        CoreEngine.setItemGenerationOff(true);
        DC_Engine.mainMenuInit();
        Eidolons.initScenario(typeName);
        DC_Engine.gameStartInit();
        GdxLauncher.main(null );
        Eidolons.mainGame.getMetaMaster().gameStarted();
//        Eidolons.mainGame.getMetaMaster().getGame().init( );
        Eidolons.mainGame.getMetaMaster().getGame().dungeonInit( );
        Eidolons.mainGame.getMetaMaster().getGame().battleInit( );
        Eidolons.mainGame.getMetaMaster().getGame().start(true);
//        WaitMaster.waitForInput(WAIT_OPERATIONS.ACTION_COMPLETE);
    }
}
