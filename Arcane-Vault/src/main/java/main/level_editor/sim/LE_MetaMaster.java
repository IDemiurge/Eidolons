package main.level_editor.sim;

import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaDataManager;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioPartyManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaDataManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.meta.universal.MetaInitializer;
import eidolons.game.battlecraft.logic.meta.universal.PartyManager;
import eidolons.game.core.game.DC_Game;
import main.level_editor.struct.campaign.Campaign;
import org.mockito.Mock;
import org.mockito.Mockito;

public class LE_MetaMaster extends ScenarioMetaMaster {

    public LE_MetaMaster(Campaign campaign) {
        super(campaign.getName());
    }
    public LE_MetaMaster(String data) {
        super(data);
    }

    @Override
    protected LE_GameSim createGame() {
        return new LE_GameSim(this);
    }

    @Override
    public LE_GameSim init() {
        return (LE_GameSim) super.init();
    }

    @Override
    protected PartyManager createPartyManager() {
        return new ScenarioPartyManager(this) {

        };
    }

    @Override
    protected MetaDataManager createMetaDataManager() {
        return new ScenarioMetaDataManager(this){

        };
    }

    @Override
    protected MetaInitializer createMetaInitializer() {
        return new LE_Initializer(this);
    }
}
