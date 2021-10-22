package main.level_editor.backend.sim;

import eidolons.entity.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.location.struct.Floor;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.battlecraft.logic.meta.universal.MetaDataManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaInitializer;
import eidolons.game.battlecraft.logic.meta.universal.SpawnManager;
import eidolons.game.core.game.DC_Game;
import libgdx.Adapter;
import main.level_editor.LevelEditor;
import main.level_editor.backend.struct.campaign.Campaign;

public class LE_MetaMaster extends ScenarioMetaMaster {

    private Campaign campaign;

    public LE_MetaMaster(Campaign campaign) {
        super(campaign.getName());
        this.campaign = campaign;
    }

    @Override
    public Floor getFloor() {
        return LevelEditor.getCurrent();
    }

    @Override
    public void gameStarted() {
        if (campaign != null) {
            LevelEditor.getModel().setTreeModel(
                     campaign );
        } else {
            try {
                LevelEditor.getModel().setTreeModel( LevelEditor.getCurrent( ).getWrapper());
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
        Adapter.afterGameInit(DC_Game.game);
        metaGame = initializer.initMetaGame(data);

        if (campaign != null)
            metaDataManager.setMissionPath(data);
        else {

            metaDataManager.setMissionPath(data);
        }

        return (LE_GameSim) game;
    }

    @Override
    protected SpawnManager createSpawnManager() {
        return new SpawnManager(this) {
            @Override
            protected Unit findMainHero() {
                return null;
            }
        };
    }

    @Override
    public MetaInitializer<ScenarioMeta> getInitializer() {
        return super.getInitializer();
    }

    @Override
    public SpawnManager getPartyManager() {
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
