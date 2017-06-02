package main.game.battlecraft.logic.meta.scenario;

import main.game.battlecraft.logic.meta.universal.MetaDataManager;

/**
 * Created by JustMe on 5/14/2017.
 */
public class ScenarioMetaDataManager extends MetaDataManager<ScenarioMeta> {
    public ScenarioMetaDataManager(ScenarioMetaMaster scenarioMetaMaster) {
        super(scenarioMetaMaster);
    }

    public String getDataPath() {
        return getMaster().  getBattleMaster().getMissionResourceFolderPath() ;
    }

    @Override
    public  ScenarioMetaMaster  getMaster() {
        return (ScenarioMetaMaster) super.getMaster();
    }
}
