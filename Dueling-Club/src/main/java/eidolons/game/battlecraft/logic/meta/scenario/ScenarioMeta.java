package eidolons.game.battlecraft.logic.meta.scenario;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.battle.mission.Mission;
import eidolons.game.battlecraft.logic.meta.universal.MetaGame;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.system.auxiliary.ContainerUtils;
import main.system.launch.CoreEngine;

import java.util.List;

/**
 * Created by JustMe on 5/12/2017.
 */
public class ScenarioMeta extends MetaGame {
    private Scenario scenario;
    private int missionIndex;
    private List<String> missions;
    private int missionNumber;

    public ScenarioMeta(Scenario scenario, MetaGameMaster<ScenarioMeta> master) {
        super(master);
        this.scenario = scenario;
    }

    /*
    number of mission

    save/load data


     */

    @Override
    public ScenarioMetaMaster getMaster() {

        return (ScenarioMetaMaster) super.getMaster();
    }

    public Scenario getScenario() {
        return scenario;
    }


    public Mission getMission() {
        return getMaster().getBattleMaster().getBattle().getMission();
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
//        if (getMissionIndex().checkProperty(PROPS.MISSION_BRIEFING_DATA))
        if (CoreEngine.isMacro()) {
            return false;
        }
        if (getMissionIndex() == 0)
            return true;
        if (isRestarted()) {
            return true;
        }
        return false;
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