package eidolons.game.core;

import com.badlogic.gdx.Gdx;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaDataManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.DC_GameManager;
import eidolons.game.core.game.DC_GameObjMaster;
import eidolons.game.core.state.DC_StateManager;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.audio.MusicEnums;
import eidolons.system.audio.MusicMaster;
import eidolons.system.libgdx.GdxAdapter;
import eidolons.system.libgdx.GdxBeans;
import eidolons.system.libgdx.GdxStatic;
import eidolons.system.test.Debugger;
import main.game.bf.Coordinates;
import main.game.core.game.Game;
import main.system.ExceptionMaster;
import main.system.auxiliary.log.FileLogManager;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.Flags;
import main.system.sound.AudioEnums;

import javax.swing.*;
import java.io.IOException;
import java.util.function.Supplier;

/**
 * Created by JustMe on 2/15/2017.
 */
public class Eidolons {
    public static final String NAME = "eidolons";
    public static final String SUFFIX = "demo";
    public static final String EXTENSION = "";
    public static DC_Game game;
    public static DC_GameManager gameManager;
    public static DC_GameObjMaster gameMaster;
    public static DC_StateManager stateManager;

    public static EidolonsGame mainGame;

    private static Unit mainHero;
    public static Unit MAIN_HERO;
    private static Party party;
    private static boolean battleRunning;
    private static SCOPE scope = SCOPE.MENU;
    private static boolean logicThreadBusy;
    private static int customThreadsUsed = 0;
    private static Unit bufferedMainHero;
    private static Supplier<GdxBeans> gdxBeansProvider;

    public static boolean initScenario(MetaGameMaster master) {
        mainGame = new EidolonsGame();
        mainGame.setMetaMaster(master);
        mainGame.init();
        if (mainGame.isAborted()) {
            master.gameExited();
            showMainMenu();
            return false;
        }

        return true;
    }


    public static EidolonsGame getMainGame() {
        return mainGame;
    }

    public static DC_Game getGame() {
        return game;
    }

    public static void setMainHero(Unit mainHero) {
        Eidolons.mainHero = mainHero;
        MAIN_HERO = mainHero;
    }

    public static Coordinates getPlayerCoordinates() {
        return getMainHero().getCoordinates();
    }

    public static Unit getMainHero() {
        if (mainHero == null) {
            if (game == null)
                return null;
            try {
                mainHero = game.getMetaMaster().getPartyManager().getParty().getLeader();
            } catch (Exception e) {
                //                e.printStackTrace();
            }
            if (mainHero == null) {
                try {
                    mainHero = (Unit) game.getPlayer(true).getHeroObj();
                } catch (Exception e) {
                    //                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
            MAIN_HERO = mainHero;
        }
        return mainHero;
    }

    public static void gameExited() {
        //        DC_Game toFinilize = game;
        game.getMetaMaster().gameExited();

        SpecialLogger.getInstance().appendAnalyticsLog(SPECIAL_LOG.MAIN,
                "GAME EXIT TO MAIN MENU");
        game.exit(true);
        game = null;
        mainHero = null;
        DC_Game.game = null;
        Game.game = null;

    }

    public static Party getParty() {
        if (party == null)
            return getGame().getMetaMaster().getPartyManager().getParty();
        return party;
    }

    public static void setParty(Party party) {
        Eidolons.party = party;
    }

    public static boolean isBattleRunning() {
        return battleRunning;
    }

    public static void setBattleRunning(boolean battleRunning) {
        Eidolons.battleRunning = battleRunning;
    }

    public static void restart() {
        new Thread(() -> {
            getGame().getMetaMaster().getMissionMaster().
                    getOutcomeManager().restart();
        }, "restart thread").start();
    }

    public static void exitFromGame() {
        exitToMenu();
    }

    public static void exitToMenu() {

        Flags.setIggDemoRunning(false);
        DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__ENTER);
        try {
            DC_Game.game.getMetaMaster().gameExited();
            // if (MacroGame.game != null) {
            //     if (MacroGame.game.getLoop() != null) {
            //         MacroGame.game.getLoop().setExited(true);
            //     }
            // }

            GdxAdapter.getInstance().getGdxApp().exited();
            gameExited();
            showMainMenu();
            MusicMaster.getInstance().scopeChanged(MusicEnums.MUSIC_SCOPE.MENU);
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
            //            DialogMaster.confirm("Game exit failed!");
            //            exitGame();
        }
    }

    public static void showMainMenu() {
        GdxAdapter.getInstance().getGdxApp().showMainMenu();
        ScenarioMetaDataManager.missionIndex = 0;
    }

    public static void activateMainHeroAction(String action) {
        activateMainHeroAction(Eidolons.getMainHero().getAction(action));
    }

    public static void activateMainHeroAction(DC_ActiveObj action) {
        Eidolons.getGame().getLoop().actionInputManual(
                new ActionInput((action), Eidolons.getMainHero()));
    }

    public static void exitGame() {
        DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__ENTER);
        SpecialLogger.getInstance().writeLogs();
        Debugger.writeLog();
        try {
            FileLogManager.writeAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gdx.app.exit();
        // System.exit(0);
    }

    public static void onGdxThread(Runnable p) {
        Gdx.app.postRunnable(p);
    }

    public static void onThisOrNonGdxThread(Runnable o) {
        if (GdxStatic.isLwjglThread()) {
            onNonGdxThread(o);
        } else o.run();
    }

    public static void onThisOrGdxThread(Runnable o) {
        if (!GdxStatic.isLwjglThread()) {
            Gdx.app.postRunnable(o);
        } else o.run();
    }

    public static void onNonGdxThread(Runnable o) {
        if (!logicThreadBusy) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    logicThreadBusy = true;
                    try {
                        o.run();
                    } catch (Exception e) {
                        ExceptionMaster.printStackTrace(e);
                    } finally {
                        logicThreadBusy = false;
                    }
                }
            });
        } else
            onNewThread(o);
    }

    public static void onNewThread(Runnable o) {
        //TODO
        // ThreadPool
        new Thread(o, "single task thread " + customThreadsUsed++).start();
    }

    public static SCOPE getScope() {
        return scope;
    }

    public static void setScope(SCOPE scope) {
        Eidolons.scope = scope;
    }


    public static void tryIt(Runnable o) {
        try {
            o.run();
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }
    }

    public static void bufferMainHero() {
        bufferedMainHero = getMainHero();
    }

    public static void resetMainHero() {
        if (bufferedMainHero == null) {
            return;
        }
        setMainHero(bufferedMainHero);
        bufferedMainHero = null;
    }

    public static void setGdxBeansProvider(Supplier<GdxBeans> gdxBeansProvider) {
        Eidolons.gdxBeansProvider = gdxBeansProvider;
    }

    public static Supplier<GdxBeans> getGdxBeansProvider() {
        return gdxBeansProvider;
    }


    public enum SCOPE {
        MENU, BATTLE, MAP
    }
}
