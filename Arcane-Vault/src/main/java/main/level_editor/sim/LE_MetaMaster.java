package main.level_editor.sim;

import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioPartyManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaDataManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaInitializer;
import eidolons.game.battlecraft.logic.meta.universal.PartyManager;
import main.level_editor.LevelEditor;
import main.level_editor.functions.model.LE_TreeModel;
import main.level_editor.struct.campaign.Campaign;

public class LE_MetaMaster extends ScenarioMetaMaster {

    private Campaign campaign;

    public LE_MetaMaster(Campaign campaign) {
        super(campaign.getName());
        this.campaign = campaign;
    }

    @Override
    public void gameStarted() {
        if (campaign != null) {
            LevelEditor.getModel().setTreeModel(new LE_TreeModel(campaign));
        } else {
            try {
                LevelEditor.getModel().setTreeModel(new LE_TreeModel(LevelEditor.getCurrent()));
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }

    }

    public LE_MetaMaster(String data) {
        super(data);
    }

    @Override
    public LE_GameSim getGame() {
        return (LE_GameSim) super.getGame();
    }

    @Override
    protected LE_GameSim createGame() {
        return new LE_GameSim(this);
    }

    @Override
    public void reinit() {
    }

    @Override
    public LE_GameSim init() {

        game = createGame();
        game.setMetaMaster(this);
        metaGame = initializer.initMetaGame(data);

        if (campaign != null)
            metaDataManager.setMissionPath(data);
        else {

            metaDataManager.setMissionPath(data);
        }

        return (LE_GameSim) game;
    }

    @Override
    protected PartyManager createPartyManager() {
        return new ScenarioPartyManager(this) {

        };
    }

    @Override
    public MetaInitializer<ScenarioMeta> getInitializer() {
        return super.getInitializer();
    }

    @Override
    public PartyManager<ScenarioMeta> getPartyManager() {
        return super.getPartyManager();
    }

    @Override
    public LE_MetaDataManager getMetaDataManager() {
        return (LE_MetaDataManager) super.getMetaDataManager();
    }

    @Override
    protected MetaDataManager createMetaDataManager() {
        return new LE_MetaDataManager(this) {

        };
    }

    @Override
    protected MetaInitializer createMetaInitializer() {
        return new LE_Initializer(this);
    }
}
