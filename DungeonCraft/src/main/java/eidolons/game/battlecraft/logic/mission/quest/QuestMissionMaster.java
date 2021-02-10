package eidolons.game.battlecraft.logic.mission.quest;

import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.battlecraft.logic.mission.universal.*;
import eidolons.game.battlecraft.logic.mission.universal.stats.MissionStatManager;
import eidolons.game.core.game.DC_Game;

/**
 * Created by JustMe on 5/8/2017.
 */
public class QuestMissionMaster extends MissionMaster<QuestMission> {


    public QuestMissionMaster(DC_Game game) {
        super(game);
    }

    public String getMissionResourceFolderPath() {
        return getMission().getResourceFolderPath();

    }

    @Override
    public CombatScriptExecutor getScriptManager() {
        return (CombatScriptExecutor) super.getScriptManager();
    }

    @Override
    public ScenarioMetaMaster getMetaMaster() {
        return (ScenarioMetaMaster) super.getMetaMaster();
    }

    protected CombatScriptExecutor createScriptManager() {
        return new CombatScriptExecutor(this);
    }


    @Override
    protected QuestMission createMission() {
        return new QuestMission(getMetaMaster().getMetaGame().getScenario());
    }

    @Override
    protected PlayerManager<QuestMission> createPlayerManager() {
        return new PlayerManager(this);
    }

    @Override
    protected MissionOutcomeManager createOutcomeManager() {
        return new MissionOutcomeManager(this);
    }

    @Override
    protected MissionConstructor createConstructor() {
        return new QuestMissionConstructor(this);
    }

    @Override
    protected MissionStatManager createStatManager() {
        return new QuestMissionStatManager(this);
    }

    @Override
    protected MissionOptionManager createOptionManager() {
        return new MissionOptionManager(this);
    }


}
