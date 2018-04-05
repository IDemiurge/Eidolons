package eidolons.game.battlecraft.logic.meta.scenario;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.battle.mission.Mission;
import eidolons.game.battlecraft.logic.meta.universal.MetaGame;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 5/12/2017.
 */
public class ScenarioMeta extends MetaGame {
    private Scenario scenario;
    private int missionIndex;

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
        return getMissionIndex() + 1 >= StringMaster.openContainer(getScenario().getProperty(PROPS.SCENARIO_MISSIONS)).size();
    }

    public boolean isPartyRespawn() {
//        if (getMission().checkProperty(PROPS.MISSION_BRIEFING_DATA))
        if (getMissionIndex() == 0)
            return true;
        if (isRestarted()) {
            setRestarted(false);
            return true;
        }
        return false;
    }
}
