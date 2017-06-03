package main.game.battlecraft.logic.meta.tutorial;

import main.game.battlecraft.logic.meta.universal.*;
import main.game.core.game.DC_Game;
import main.game.core.game.TestGame;

/**
 * Created by JustMe on 6/2/2017.

 >> Highlight scripts
 >> Block actions
 >> Respawn
 >> Pre-determined outcomes – no misses, no crits,
 >> Overlay dialogues
 */
public class TutorialMetaMaster extends MetaGameMaster<TutorialMeta> {
    public TutorialMetaMaster(String data) {
        super(data);
    }

    @Override
    public TestGame getGame() {
        return (TestGame) super.getGame();
    }

    @Override
    public void gameStarted() {
        super.gameStarted();
//set dialogue non-exclusive
        getGame().getCombatMaster().setChancesOff(true);
        getGame().getCombatMaster().setDiceAverage(true);
        getGame().getCombatMaster().setRollsAverage(true);
        getGame().getCombatMaster().setActionsBlocked(true);
    }

    @Override
    protected DC_Game createGame() {
        return new TestGame(this);
    }

    @Override
    protected PartyManager<TutorialMeta> createPartyManager() {

        return new TutorialPartyManager(this);
    }

    @Override
    protected MetaDataManager<TutorialMeta> createMetaDataManager() {
        return new TutorialMetaDataManager(this);
    }

    @Override
    protected ShopManager<TutorialMeta> createShopManager() {
        return null;
    }

    @Override
    protected MetaInitializer<TutorialMeta> createMetaInitializer() {
        return new TutorialInitializer(this);
    }
}
