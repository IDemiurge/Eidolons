package main.level_editor.backend.sim;

import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaDataManager;

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
    public String getSoloDungeonPath() {
        return super.getSoloDungeonPath();
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
