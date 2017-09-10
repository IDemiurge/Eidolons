package main.game.module.dungeoncrawl.explore;

import main.client.cc.logic.party.PartyObj;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.game.core.game.DC_Game.GAME_MODES;

/**
 * Created by JustMe on 8/2/2017.
 */
public class ExplorationMaster {
    static boolean explorationOn;
    private static boolean testMode = true;
    DC_Game game;
    ExplorationAiMaster aiMaster;
    ExplorationTimeMaster timeMaster;
    private ExplorationResetHandler resetter;
    private DungeonCrawler crawler;
    private ExplorationActionHandler actionHandler;

    public ExplorationMaster(DC_Game game) {
        this.game = game;
        aiMaster = new ExplorationAiMaster(this);
        timeMaster = new ExplorationTimeMaster(this);
        resetter = new ExplorationResetHandler(this);
        crawler = new DungeonCrawler(this);
        actionHandler = new ExplorationActionHandler(this);
    }

    public static boolean isTestMode() {
        return testMode;
    }

    public static boolean checkExplorationSupported(DC_Game game) {
        if (testMode)
            return true;
        if (game.getGameMode() == GAME_MODES.DUNGEON_CRAWL) {
            return true;
        }

        return false;
    }

    public static void switchExplorationMode(boolean on) {
        if (explorationOn==on)
            return ;
        explorationOn = on;
        explorationToggled();
    }

    private static ExplorerUnit createExplorerUnit(PartyObj partyObj) {
        ExplorerUnit e = new ExplorerUnit(null);

        return e;
    }


    private static void explorationToggled() {
        //speed up resets?
        //cache unit state?
        Eidolons.game.startGameLoop();
        //exceptions: triggers, scripts,

    }

    public static boolean isExplorationOn() {
        if (testMode)
            return true;
        return explorationOn;
    }

    public DC_Game getGame() {
        return game;
    }

    public ExplorationAiMaster getAiMaster() {
        return aiMaster;
    }

    public ExplorationTimeMaster getTimeMaster() {
        return timeMaster;
    }

    public ExplorationResetHandler getResetter() {
        return resetter;
    }

    public DungeonCrawler getCrawler() {
        return crawler;
    }

    public ExplorationActionHandler getActionHandler() {
        return actionHandler;
    }

}
