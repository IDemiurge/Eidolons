package main.game.battlecraft.logic.meta.scenario;

import main.content.PROPS;
import main.game.battlecraft.logic.meta.universal.MetaDataManager;
import main.libgdx.launch.ScenarioLauncher;
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

        if (StringMaster.isEmpty(missionName)) {
            int missionIndex = (ScenarioLauncher.missionIndex);

            getMetaGame().setMissionIndex(missionIndex);

            setMissionName(StringMaster.openContainer(getMetaGame().getScenario().
             getProperty(PROPS.SCENARIO_MISSIONS)).get(missionIndex));

        } else {
            getMetaGame().setMissionIndex(StringMaster.openContainer(getMetaGame().getScenario().
             getProperty(PROPS.SCENARIO_MISSIONS)).indexOf(missionName));
        }
    }
}
