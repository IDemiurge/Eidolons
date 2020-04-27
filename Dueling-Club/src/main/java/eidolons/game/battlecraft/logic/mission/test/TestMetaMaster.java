package eidolons.game.battlecraft.logic.mission.test;

import eidolons.game.battlecraft.logic.meta.scenario.ScenarioInitializer;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaDataManager;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioPartyManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaDataManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.meta.universal.MetaInitializer;
import eidolons.game.battlecraft.logic.meta.universal.PartyManager;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import org.mockito.Mockito;

/**
 * Created by EiDemiurge on 9/26/2018.
 */
public class TestMetaMaster extends MetaGameMaster {
    public TestMetaMaster( ) {
        super(null  );
        game = Eidolons.game;
    }

    @Override
    protected DC_Game createGame() {
        return new DC_Game(false);
    }

    @Override
    protected PartyManager createPartyManager() {
        return Mockito.mock(ScenarioPartyManager.class);
    }

    @Override
    protected MetaDataManager createMetaDataManager() {
        return Mockito.mock(ScenarioMetaDataManager.class);
    }

    @Override
    protected MetaInitializer createMetaInitializer() {
        return Mockito.mock(ScenarioInitializer.class);
    }

    @Override
    public boolean isRngQuestsEnabled() {
        return false;
    }

    @Override
    public void gameStarted() {
//        super.gameStarted();
    }

    @Override
    public boolean isRngDungeon() {
        return false;
    }
}
