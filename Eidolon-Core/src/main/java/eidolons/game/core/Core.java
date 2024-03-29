package eidolons.game.core;

import com.badlogic.gdx.Gdx;
import eidolons.entity.feat.active.ActiveObj;
import eidolons.entity.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaDataManager;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.DC_GameManager;
import eidolons.game.core.game.DC_GameObjMaster;
import eidolons.game.core.state.DC_StateManager;
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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Created by JustMe on 2/15/2017.
 */
public class Core {
    public static final String NAME = "eidolons";
    public static final String SUFFIX = "demo";
    public static final String EXTENSION = "";
    public static DC_Game game;
    public static DC_GameManager gameManager;
    public static DC_GameObjMaster gameMaster;
    public static DC_StateManager stateManager;

    public static EidolonsGame mainGame;

    public static Unit mainHero;
    private static APPLICATION_SCOPE scope = APPLICATION_SCOPE.MENU;
    private static boolean logicThreadBusy;
    private static int customThreadsUsed = 0;
    private static Unit bufferedMainHero;
    private static Supplier<GdxBeans> gdxBeansProvider;

    private static ThreadPoolExecutor executor;

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
        Core.mainHero = mainHero;
    }

    public static Coordinates getPlayerCoordinates() {
        return getMainHero().getCoordinates();
    }

    public static Unit getMainHero() {
        if (mainHero == null) {
            if (game == null)
                return null;
            if (mainHero == null && game != null) {
                if (game.getPlayer(true) != null) {
                    mainHero = (Unit) game.getPlayer(true).getHeroObj();
                }
            }
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
        activateMainHeroAction(Core.getMainHero().getAction(action));
    }

    public static void activateMainHeroAction(ActiveObj action) {
        Core.getGame().getLoop().actionInputManual(
                new ActionInput((action), Core.getMainHero()));
    }

    public static void exitGame() {
        DC_SoundMaster.playStandardSound(AudioEnums.STD_SOUNDS.NEW__ENTER);
        SpecialLogger.getInstance().writeLogs();
        Debugger.writeLog();
        try {
            FileLogManager.writeAll();
        } catch (IOException e) {
            main.system.ExceptionMaster.printStackTrace(e);
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
        // new DefaultDispatchService()
        if (executor == null)
            executor = new ThreadPoolExecutor(8, 20, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        // if (threadPool == null)
        //     threadPool = ThreadPool.wrap(executor, 5);
        executor.execute(o);
        // new Thread(o, "single task thread " + customThreadsUsed++).start();
    }

    public static APPLICATION_SCOPE getScope() {
        return scope;
    }

    public static void setScope(APPLICATION_SCOPE scope) {
        Core.scope = scope;
    }


    public static void tryIt(Runnable o) {
        try {
            o.run();
        } catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }
    }

    public static void setGdxBeansProvider(Supplier<GdxBeans> gdxBeansProvider) {
        Core.gdxBeansProvider = gdxBeansProvider;
    }

    public static Supplier<GdxBeans> getGdxBeansProvider() {
        return gdxBeansProvider;
    }

    public enum APPLICATION_SCOPE {
        MENU, BATTLE, MAP
    }
}
