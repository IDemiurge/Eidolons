package eidolons.game.battlecraft.logic.meta.scenario;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.meta.universal.MetaDataManager;
import eidolons.libgdx.launch.ScenarioLauncher;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 5/14/2017.
 */
public class ScenarioMetaDataManager extends MetaDataManager<ScenarioMeta> {
    private String missionName;

    public ScenarioMetaDataManager(ScenarioMetaMaster scenarioMetaMaster) {
        super(scenarioMetaMaster);
    }

    public String getDataPath() {
        return getMaster().getBattleMaster().getMissionResourceFolderPath();
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

    public void initMissionName() {
        String missionName =
         getMissionName();
        getMetaGame().setMissions(ContainerUtils.openContainer(getMetaGame().getScenario().
         getProperty(PROPS.SCENARIO_MISSIONS)));
        if (StringMaster.isEmpty(missionName)) {
            int missionIndex = (ScenarioLauncher.missionIndex);

            getMetaGame().setMissionIndex(missionIndex);

            setMissionName(ContainerUtils.openContainer(getMetaGame().getScenario().
             getProperty(PROPS.SCENARIO_MISSIONS)).get(missionIndex));

            setMissionPath(ContainerUtils.openContainer(getMetaGame().getScenario().
             getProperty(PROPS.SCENARIO_PATHS)).get(missionIndex));
        } else {
            getMetaGame().setMissionIndex(ContainerUtils.openContainer(getMetaGame().getScenario().
             getProperty(PROPS.SCENARIO_MISSIONS)).indexOf(missionName));
        }
    }
}
