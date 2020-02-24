package main.level_editor.sim;

import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaDataManager;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.battlecraft.logic.meta.universal.MetaDataManager;

public class LE_MetaDataManager extends ScenarioMetaDataManager {
    public LE_MetaDataManager(LE_MetaMaster metaMaster) {
        super(metaMaster);
    }

    @Override
    public String getDataPath() {
        return super.getDataPath();
    }

    @Override
    public String getMissionName() {
        return super.getMissionName();
    }

    @Override
    public String getMissionPath() {
        return super.getMissionPath();
    }

    @Override
    public void setMissionPath(String missionPath) {
        super.setMissionPath(missionPath);
    }

    @Override
    public void initData() {
        super.initData();
    }
}
