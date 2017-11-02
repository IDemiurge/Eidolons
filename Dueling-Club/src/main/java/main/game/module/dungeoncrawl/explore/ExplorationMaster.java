package main.game.module.dungeoncrawl.explore;

import main.client.cc.logic.party.PartyObj;
import main.game.core.game.DC_Game;
import main.game.module.dungeoncrawl.ai.AggroMaster;
import main.libgdx.anims.AnimMaster;
import main.libgdx.anims.AnimMaster3d;

/**
 * Created by JustMe on 8/2/2017.
 */
public class ExplorationMaster {
    static boolean explorationOn;
    private static boolean testMode   ;
    private   ExploreEnemyPartyMaster enemyPartyMaster;
    private   ExplorePartyMaster partyMaster;
    private   ExploreCleaner cleaner;
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
        cleaner =new ExploreCleaner(this);
        actionHandler = new ExplorationActionHandler(this);
        partyMaster = new ExplorePartyMaster(this);
        enemyPartyMaster = new ExploreEnemyPartyMaster(this);
    }

    public static void setTestMode(boolean testMode) {
        ExplorationMaster.testMode = testMode;
    }

    public ExplorePartyMaster getPartyMaster() {
        return partyMaster;
    }

    public ExploreEnemyPartyMaster getEnemyPartyMaster() {
        return enemyPartyMaster;
    }

    public static boolean isTestMode() {
        return testMode;
    }

    public static boolean isExplorationSupported(DC_Game game) {
//        if (testMode)
//            return true;
//        if (game.getGameMode() == GAME_MODES.ARENA)
//            return false;
//            return true;
//        }
//TODO only if disabled by <?>>
        return true;
    }

    public   void switchExplorationMode(boolean on) {
        if (explorationOn == on)
            return;
        explorationOn = on;
        explorationToggled();
    }

    private   ExplorerUnit createExplorerUnit(PartyObj partyObj) {
        ExplorerUnit e = new ExplorerUnit(null);

        return e;
    }


    private   void explorationToggled() {
        //speed up resets?
        //cache unit state?
        if (isExplorationOn()){
            //TODO quick-fix
          cleaner.cleanUpAfterBattle();
            game.getLogManager().logBattleEnds();
            getResetter().setFirstResetDone(false);
        } else
        {
            game.getLogManager().logBattleStarts();
            AggroMaster.getLastAggroGroup().forEach(unit -> {
                AnimMaster.getInstance().getConstructor().preconstructAll(unit);
                AnimMaster3d.preloadAtlases(unit);
            });
            getResetter().setFirstResetDone(false);
        }
        getResetter().setFirstResetDone(false);
        game.startGameLoop();
        game.getManager().reset();
        //exceptions: triggers, scripts,

    }


    public static boolean isExplorationOn() {
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

    public ExploreCleaner getCleaner() {
        return cleaner;
    }

    public ExplorationActionHandler getActionHandler() {
        return actionHandler;
    }

    public void init() {
        explorationOn = getCrawler().checkExplorationDefault();
    }

    public ExploreGameLoop getLoop() {
        if (!explorationOn)
            return null ;
        return (ExploreGameLoop) game.getLoop();
    }
}
