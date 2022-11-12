package eidolons.game.battlecraft.logic.mission.test;

import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.battlecraft.logic.meta.testenv.TestEnvMGM;
import eidolons.game.battlecraft.logic.mission.quest.CombatScriptExecutor;
import eidolons.game.battlecraft.logic.mission.universal.MissionConstructor;
import eidolons.game.battlecraft.logic.mission.universal.MissionMaster;
import eidolons.game.battlecraft.logic.mission.universal.MissionOptionManager;
import eidolons.game.battlecraft.logic.mission.universal.PlayerManager;
import eidolons.game.battlecraft.logic.mission.universal.stats.MissionStatManager;
import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/8/2017.
 */
public class TestMissionMaster extends MissionMaster<TestMission> {


    public TestMissionMaster(DC_Game game) {
        super(game);
    }

    @Override
    public CombatScriptExecutor getScriptManager() {
        return (CombatScriptExecutor) super.getScriptManager();
    }

    @Override
    public TestEnvMGM getMetaMaster() {
        return (TestEnvMGM) super.getMetaMaster();
    }

    protected CombatScriptExecutor createScriptManager() {
        return new CombatScriptExecutor(this);
    }


    @Override
    protected TestMission createMission() {
        return new TestMission();
    }

    @Override
    protected PlayerManager<TestMission> createPlayerManager() {
        return new PlayerManager(this);
    }

    @Override
    protected MissionConstructor createConstructor() {
        return new TestMissionConstructor(this);
    }

    @Override
    protected MissionStatManager createStatManager() {
        return new TestMissionStatManager(this);
    }

    @Override
    protected MissionOptionManager createOptionManager() {
        return new MissionOptionManager(this);
    }


}
