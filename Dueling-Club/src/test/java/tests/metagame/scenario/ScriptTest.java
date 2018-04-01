package tests.metagame.scenario;

import eidolons.game.battlecraft.logic.battle.mission.MissionBattleMaster;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.launch.ScenarioLauncher;
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
