package eidolons.game.module.dungeoncrawl.explore;

import eidolons.game.battlecraft.ai.advanced.engagement.EngageEvent;
import eidolons.game.battlecraft.ai.advanced.engagement.EngagementHandler;
import eidolons.game.battlecraft.ai.advanced.engagement.PlayerStatus;
import eidolons.game.battlecraft.ai.explore.AggroMaster;
import eidolons.game.core.game.DC_Game;
import eidolons.system.audio.MusicMaster;
import eidolons.system.audio.MusicMaster.MUSIC_SCOPE;
import main.entity.Ref;
import main.game.logic.event.Event;

/**
 * Created by JustMe on 8/2/2017.
 */
public class ExplorationMaster {
    static boolean explorationOn=true;
    private static boolean testMode;
    private static boolean realTimePaused;
    private static boolean waiting;
    DC_Game game;
    ExplorationAiMaster aiMaster;
    ExplorationTimeMaster timeMaster;
    private ExploreEnemyPartyMaster enemyPartyMaster;
    private ExplorePartyMaster partyMaster;
    private ExploreCleaner cleaner;
    private ExplorationResetHandler resetter;
    private ExplorationActionHandler actionHandler;
    private AggroMaster aggroMaster;
    private boolean toggling;
    private EngagementHandler engagementHandler;
    private PlayerStatus playerStatus;

    public ExplorationMaster(DC_Game game) {
        this.game = game;
        aggroMaster = new AggroMaster(this);
        aiMaster = new ExplorationAiMaster(this);
        timeMaster = new ExplorationTimeMaster(this);
        resetter = new ExplorationResetHandler(this);
        cleaner = new ExploreCleaner(this);
        actionHandler = new ExplorationActionHandler(this);
        partyMaster = new ExplorePartyMaster(this);
        enemyPartyMaster = new ExploreEnemyPartyMaster(this);
        engagementHandler =new EngagementHandler(this);
    }

    public void act(float delta) {
        engagementHandler.act(delta);
        aiMaster.act(delta);
    }
    public static boolean isWaiting() {
        return waiting;
    }

    public static void setWaiting(boolean waiting) {
        ExplorationMaster.waiting = waiting;
    }

    public static boolean isRealTimePaused() {
        return realTimePaused;
    }

    public static void setRealTimePaused(boolean realTimePaused) {
        ExplorationMaster.realTimePaused = realTimePaused;
    }

    public static boolean isTestMode() {
        return testMode;
    }

    public static void setTestMode(boolean testMode) {
        ExplorationMaster.testMode = testMode;
    }

    public static boolean isExplorationSupported(DC_Game game) {
        //boss fights?
        return true;
    }

    public  boolean isInCombat() {
        return !explorationOn;
    }
    public static boolean isExplorationOn() {
        return explorationOn;
    }

    public ExplorePartyMaster getPartyMaster() {
        return partyMaster;
    }

    public ExploreEnemyPartyMaster getEnemyPartyMaster() {
        return enemyPartyMaster;
    }

    public void switchExplorationMode(boolean on) {
        if (explorationOn == on)
            return;
        explorationOn = on;
        toggling=true;
        explorationToggled();
        toggling=false;
    }

    private void explorationToggled() {
        if (isExplorationOn()) {
            //TODO quick-fix
            cleaner.cleanUpAfterBattle();
            game.getLogManager().logBattleEnds();
            game.fireEvent(new Event(Event.STANDARD_EVENT_TYPE.COMBAT_ENDS, new Ref(game)));
            getResetter().setResetNotRequired(false);
            MusicMaster.getInstance().scopeChanged(MUSIC_SCOPE.ATMO);
        } else {
            game.fireEvent(new Event(Event.STANDARD_EVENT_TYPE.COMBAT_STARTS, new Ref(game)));
            game.getLogManager().logBattleStarts();
            getResetter().setResetNotRequired(false);
        }
        getResetter().setResetNotRequired(false);
        game.startGameLoop();
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

    public AggroMaster getAggroMaster() {
        return aggroMaster;
    }

    public ExploreCleaner getCleaner() {
        return cleaner;
    }

    public ExplorationActionHandler getActionHandler() {
        return actionHandler;
    }

    public void init() {
        explorationOn = getAggroMaster().checkExplorationDefault();
    }

    public ExploreGameLoop getLoop() {
        if (!explorationOn)
            return null;
        return (ExploreGameLoop) game.getLoop();
    }

    public boolean isToggling() {
        return toggling;
    }

    public void setToggling(boolean toggling) {
        this.toggling = toggling;
    }

    public EngagementHandler getEngagementHandler() {
        return engagementHandler;
    }

    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }

    public void setPlayerStatus(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }

    public void event(EngageEvent event) {
        if (isInCombat()) {
            getEngagementHandler().getEvents().processNow(event);
            return;
        }
        getEngagementHandler().getEvents().addEvent(event);
    }
}
