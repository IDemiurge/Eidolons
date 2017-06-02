package main.game.battlecraft.logic.meta.tutorial;

import main.game.battlecraft.logic.meta.scenario.dialogue.DialogueFactory;
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
        getGame().getCombatMaster().setChancesOff(true);
        getGame().getCombatMaster().setDiceAverage(true);
        getGame().getCombatMaster().setRollsAverage(true);
        getGame().getCombatMaster().setActionsBlocked(true);
    }

    @Override
    public TestGame getGame() {
        return (TestGame) super.getGame();
    }

    @Override
    protected DialogueFactory createDialogueFactory() {
        return new DialogueFactory(){
            //TODO special dialogues with controls
        };
    }

//    @Override
//    protected IntroFactory createIntroFactory() {
//        return new TutorialInitializer(this);
//    }

    @Override
    protected DC_Game createGame() {
        return new TestGame();
    }

    @Override
    protected PartyManager<TutorialMeta> createPartyManager() {

        return new TutorialPartyManager(this);
    }

    @Override
    protected MetaDataManager<TutorialMeta> createMetaDataManager() {
        return null;
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
