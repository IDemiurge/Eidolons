package eidolons.game.module.dungeoncrawl.explore;

import eidolons.game.battlecraft.ai.explore.AggroMaster;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.libgdx.anims.construct.AnimConstructor;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.audio.MusicMaster;
import eidolons.system.audio.MusicMaster.MUSIC_SCOPE;
import main.system.auxiliary.RandomWizard;
import main.system.sound.SoundMaster.STD_SOUNDS;

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
    }

    public void act(float delta) {
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
//        if (testMode)
//            return true;
//        if (game.getGameMode() == GAME_MODES.ARENA)
//            return false;
//            return true;
//        }
//TODO only if disabled by <?>>
        return true;
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
        explorationToggled();
    }

    private ExplorerUnit createExplorerUnit(Party party) {
        ExplorerUnit e = new ExplorerUnit(null);

        return e;
    }

    private void explorationToggled() {
        //speed up resets?
        //cache unit state?
        if (isExplorationOn()) {
            //TODO quick-fix
            cleaner.cleanUpAfterBattle();
            game.getLogManager().logBattleEnds();
            getResetter().setResetNotRequired(false);

            MusicMaster.getInstance().scopeChanged(MUSIC_SCOPE.ATMO);

            DC_SoundMaster.playStandardSound(RandomWizard.random()
             ? STD_SOUNDS.NEW__BATTLE_END
             : STD_SOUNDS.NEW__BATTLE_END2);

        } else {
            game.getLogManager().logBattleStarts();
            if (AnimConstructor.isPreconstructEnemiesOnCombatStart())
                AggroMaster.getLastAggroGroup().forEach(unit -> {
                    AnimConstructor.preconstructAll(unit);
                });
            getResetter().setResetNotRequired(false);
            try {
                MusicMaster.getInstance().scopeChanged(MUSIC_SCOPE.BATTLE);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        getResetter().setResetNotRequired(false);
        game.startGameLoop();

//        game.getManager().reset();
        //exceptions: triggers, scripts,

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

}
