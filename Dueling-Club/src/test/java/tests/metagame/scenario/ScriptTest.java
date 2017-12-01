package tests.metagame.scenario;

import main.game.battlecraft.logic.battle.mission.MissionBattleMaster;
import main.game.core.Eidolons;
import main.libgdx.launch.ScenarioLauncher;
import org.junit.Before;

/**
 * Created by JustMe on 5/22/2017.
 */
public abstract class ScriptTest {
    private String dummyScenario="scenario2";

    public abstract String getScriptText();

    @Before
    public void init(){
        ScenarioLauncher.launch( dummyScenario);
//        ScenarioMetaMaster master= Eidolons.mainGame.getMetaMaster();
        MissionBattleMaster master= (MissionBattleMaster) Eidolons.game.getBattleMaster();
       master.getScriptManager().parseScripts(getScriptText());

    }
}
