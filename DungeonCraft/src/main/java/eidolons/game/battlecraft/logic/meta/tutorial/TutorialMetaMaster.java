package eidolons.game.battlecraft.logic.meta.tutorial;

import eidolons.game.battlecraft.logic.meta.universal.MetaDataManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.logic.meta.universal.MetaInitializer;
import eidolons.game.battlecraft.logic.meta.universal.PartyManager;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.TestGame;

/**
 * Created by JustMe on 6/2/2017.
 * <p>
 * >> Highlight scripts
 * >> Block actions
 * >> Respawn
 * >> Pre-determined outcomes – no misses, no crits,
 * >> Overlay dialogues
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

//    @Override
//    protected DialogueFactory createDialogueFactory() {
//        return new DialogueFactory() {
//            @Override
//            protected String getFileRootPath() {
//            return     StringMaster.buildPath(
//             master.getMetaDataManager().getDataPath()
//             , TextMaster.getLocale(),
//             StringMaster.getPathSeparator()+ getFileName());
//            }
//
//        };
//    }

    @Override
    protected DC_Game createGame() {
        return new TestGame(this);
    }

    @Override
    protected PartyManager createPartyManager() {

        return new TutorialPartyManager(this);
    }

    @Override
    protected MetaDataManager<TutorialMeta> createMetaDataManager() {
        return new TutorialMetaDataManager(this);
    }

    @Override
    protected MetaInitializer<TutorialMeta> createMetaInitializer() {
        return new TutorialInitializer(this);
    }
}
