package eidolons.game.battlecraft.logic.mission.test;

import eidolons.game.battlecraft.logic.meta.scenario.Scenario;
import eidolons.game.battlecraft.logic.mission.universal.*;
import eidolons.game.battlecraft.logic.mission.universal.stats.MissionStatManager;
import eidolons.game.core.game.DC_Game;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 5/8/2017.
 */
public class TestMissionMaster extends MissionMaster<TestMission> {


    private ObjType TUTORIAL_TYPE;

    public TestMissionMaster(DC_Game game) {
        super(game);
    }

    @Override
    protected ScriptManager createScriptManager() {
        return new TestScriptExecutor(this);
    }


    @Override
    protected TestMission createMission() {
        return new TestMission(new Scenario(TUTORIAL_TYPE));
    }

    @Override
    protected PlayerManager<TestMission> createPlayerManager() {
        return new PlayerManager<>(this);
    }

    @Override
    protected MissionOutcomeManager createOutcomeManager() {
        return new MissionOutcomeManager(this);
    }

    @Override
    protected MissionConstructor createConstructor() {
        return new TestMissionConstructor(this);
    }

    @Override
    protected MissionStatManager createStatManager() {
        return new MissionStatManager(this);
    }

    @Override
    protected MissionOptionManager createOptionManager() {
        return new MissionOptionManager(this);
    }
}
