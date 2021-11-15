package eidolons.game.exploration.handlers;

import eidolons.game.battlecraft.ai.advanced.engagement.EngageEvent;
import eidolons.game.battlecraft.ai.advanced.engagement.EngagementHandler;
import eidolons.game.battlecraft.ai.advanced.engagement.PlayerStatus;
import eidolons.game.core.game.DC_Game;
import eidolons.system.audio.MusicEnums;
import eidolons.system.audio.MusicMaster;
import main.entity.Ref;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 8/2/2017.
 */
public class ExplorationMaster {
    static boolean explorationOn=true;
    private static boolean testMode;
    private static boolean realTimePaused;
    private static boolean waiting;
    DC_Game game;
    ExplorationTimeMaster timeMaster;
    private final ExplorationActionHandler actionHandler;
    private boolean toggling;
    private final EngagementHandler engagementHandler;
    private PlayerStatus playerStatus;

    public ExplorationMaster(DC_Game game) {
        this.game = game;
        timeMaster = new ExplorationTimeMaster(this);
        actionHandler = new ExplorationActionHandler(this);
        engagementHandler =new EngagementHandler(this);
    }

    public void act(float delta) {
        engagementHandler.act(delta);
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
            game.getLogManager().logBattleEnds();
            game.fireEvent(new Event(Event.STANDARD_EVENT_TYPE.COMBAT_ENDS, new Ref(game)));
            MusicMaster.getInstance().scopeChanged(MusicEnums.MUSIC_SCOPE.ATMO);
        } else {
            GuiEventManager.trigger(GuiEventType.COMBAT_STARTED );
            game.fireEvent(new Event(Event.STANDARD_EVENT_TYPE.COMBAT_STARTS, new Ref(game)));
            game.getLogManager().logBattleStarts();
        }
        game.startGameLoop();
    }

    public DC_Game getGame() {
        return game;
    }


    public ExplorationTimeMaster getTimeMaster() {
        return timeMaster;
    }
    public ExplorationActionHandler getActionHandler() {
        return actionHandler;
    }

    public void init() {
        // explorationOn = getAggroMaster().checkExplorationDefault();
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
        getEngagementHandler().getEvents().addEvent(event);
    }
}
