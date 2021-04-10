package eidolons.game.battlecraft.logic.meta.scenario;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.meta.universal.MetaDataManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 5/14/2017.
 */
public class ScenarioMetaDataManager extends MetaDataManager<ScenarioMeta> {
    public static int missionIndex = 0;
    private String missionName;

    public ScenarioMetaDataManager(MetaGameMaster scenarioMetaMaster) {
        super(scenarioMetaMaster);
    }

    public String getDataPath() {
        return getMaster().getMissionMaster().getMissionResourceFolderPath();
    }

    @Override
    public ScenarioMetaMaster getMaster() {
        return (ScenarioMetaMaster) super.getMaster();
    }

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    @Override
    public String nextMission() {

        return getMissionName();
    }

    public void initData() {
        String missionName =
                getMissionName();
        getMetaGame().setMissions(ContainerUtils.openContainer(getMetaGame().getScenario().
                getProperty(PROPS.SCENARIO_MISSIONS)));
        if (StringMaster.isEmpty(missionName)) {
            getMetaGame().setMissionIndex(missionIndex);
            setMissionName(ContainerUtils.openContainer(getMetaGame().getScenario().
                    getProperty(PROPS.SCENARIO_MISSIONS)).get(missionIndex));
        } else {
            getMetaGame().setMissionIndex(ContainerUtils.openContainer(getMetaGame().getScenario().
                    getProperty(PROPS.SCENARIO_MISSIONS)).indexOf(missionName));
        }
    }
}
