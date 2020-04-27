package tests.metagame.scenario;

import eidolons.game.battlecraft.logic.mission.quest.QuestMissionMaster;
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
        QuestMissionMaster master= (QuestMissionMaster) Eidolons.game.getMissionMaster();
       master.getScriptManager().parseScripts(getScriptText());

    }
}
