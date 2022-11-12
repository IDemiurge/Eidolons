package eidolons.game.battlecraft.logic.meta.scenario;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.meta.universal.MetaGame;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.mission.quest.QuestMission;
import main.system.auxiliary.ContainerUtils;
import main.system.launch.Flags;

import java.util.List;

/**
 * Created by JustMe on 5/12/2017.
 */
public class ScenarioMeta extends MetaGame {
    private final Scenario scenario;
    private int missionIndex;
    private List<String> missions;
    private int missionNumber;

    public ScenarioMeta(Scenario scenario, MetaGameMaster master) {
        super(master);
        this.scenario = scenario;
    }

    @Override
    public MetaGameMaster getMaster() {
        return   super.getMaster();
    }

    public Scenario getScenario() {
        return scenario;
    }

    public QuestMission getMission() {
        return (QuestMission) getMaster().getMissionMaster().getMission();
    }

    public int getMissionIndex() {
        return missionIndex;
    }

    public void setMissionIndex(int missionIndex) {
        this.missionIndex = missionIndex;
    }

    public boolean isFinalLevel() {
        return getMissionIndex() + 1 >= ContainerUtils.openContainer(getScenario().getProperty(PROPS.SCENARIO_MISSIONS)).size();
    }

    public boolean isPartyRespawn() {
        if (Flags.isMacro()) {
            return false;
        }
        if (getMissionIndex() == 0)
            return true;
        return isRestarted();
    }

    public void setMissions(List<String> missions) {
        this.missions = missions;
        missionNumber = missions.size();
    }

    public List<String> getMissions() {
        return missions;
    }

    public int getMissionNumber() {
        return missionNumber;
    }
}
