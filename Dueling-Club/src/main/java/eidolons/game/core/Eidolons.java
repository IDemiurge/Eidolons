package eidolons.game.core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.igg.event.TipMessageMaster;
import eidolons.game.battlecraft.logic.meta.igg.event.TipMessageSource;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.DC_GameManager;
import eidolons.game.core.game.DC_GameObjMaster;
import eidolons.game.core.state.DC_StateManager;
import eidolons.game.module.herocreator.CharacterCreator;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.menu.GameMenu;
import eidolons.libgdx.gui.panels.headquarters.town.TownPanel;
import eidolons.libgdx.launch.GenericLauncher;
import eidolons.libgdx.launch.ScenarioLauncher;
import eidolons.libgdx.screens.GameScreen;
import eidolons.libgdx.screens.SCREEN_TYPE;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.menu.MainMenu;
import eidolons.macro.MacroGame;
import eidolons.macro.entity.town.Town;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.audio.MusicMaster;
import eidolons.system.audio.MusicMaster.MUSIC_SCOPE;
import eidolons.system.graphics.RESOLUTION;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import eidolons.system.test.Debugger;
import main.game.core.game.Game;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.SpecialLogger;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster.STD_SOUNDS;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by JustMe on 2/15/2017.
 */
public class Eidolons {
    public static final boolean DEV_MODE = true;
    private static final Integer WIDTH_WINDOWED = 95;
    private static final Integer HEIGHT_WINDOWED = 90;
    public static final String NAME = "eidolons";
    public static final String EXTENSION = "nethergate";
    public static final String SUFFIX = "demo";
    public static DC_Game game;
    public static DC_GameManager gameManager;
    public static DC_GameObjMaster gameMaster;
    public static DC_StateManager stateManager;

    public static EidolonsGame mainGame;
    public static Application gdxApplication;
    public static boolean BOSS_FIGHT;
    public static boolean TUTORIAL_MISSION;
    public static boolean TUTORIAL_PATH;
    private static LwjglApplication application;
    private static String selectedMainHero;
    private static Unit mainHero;
    public static Unit MAIN_HERO;
    private static boolean fullscreen;
    private static RESOLUTION resolution;
    private static ScreenViewport mainViewport;
    private static GenericLauncher launcher;
    private static Party party;
    private static boolean battleRunning;
    private static GameScreen screen;
    private static SCOPE scope = SCOPE.MENU;
    private static SCREEN_TYPE screenType;
    private static SCREEN_TYPE previousScreenType;
    private static boolean logicThreadBusy;
    private static int customThreadsUsed = 0;

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

    //    public static void init(){
    //
    //    }
    public static DC_Game getGame() {
        return game;
    }

    public static void initDemoMeta() {
        initScenario(new ScenarioMetaMaster(ScenarioLauncher.DEFAULT));
        CharacterCreator.setGame(getGame());
        CharacterCreator.init();
    }

    public static LwjglApplication getApplication() {
        return application;
    }

    public static void setApplication(LwjglApplication application) {
        Eidolons.application = application;
    }

    public static String getSelectedMainHero() {
        return selectedMainHero;
    }

    public static void setSelectedMainHero(String selectedMainHero) {
        Eidolons.selectedMainHero = selectedMainHero;
    }


    public static void setMainHero(Unit mainHero) {
        Eidolons.mainHero = mainHero;
        MAIN_HERO = mainHero;
    }

    public static Unit getMainHero() {
        if (mainHero == null) {
            if (game == null)
                return null;
            try {
                mainHero = game.getMetaMaster().getPartyManager().getParty().getLeader();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mainHero == null) {
                try {
                    mainHero = (Unit) game.getPlayer(true).getHeroObj();
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
            MAIN_HERO = mainHero;
        }
        return mainHero;
    }

    public static boolean isFullscreen() {
        return fullscreen;
    }

    public static void setFullscreen(boolean b) {
        if (getApplication() == null)
            return;
        if (resolution != null)
            if (fullscreen == b)
                return;
        fullscreen = b;
        Eidolons.getApplication().getGraphics().setResizable(true);
        if (fullscreen) {
            int width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
            int height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
            if (width>1920)
            {
                cannotFullscreen();
                return;
            }
            if (width>1080)
            {
                cannotFullscreen();
                return;
            }

            GdxMaster.setWidth(width);
            GdxMaster.setHeight(LwjglApplicationConfiguration.getDesktopDisplayMode().height);
            getApplication().getGraphics().setUndecorated(true);
            //            getApplication().getGraphics().setFullscreenMode(
            //             new LwjglDisplayMode(width, height,
            //              60, 256, null));

            Gdx.graphics.setWindowedMode(width,
                    LwjglApplicationConfiguration.getDesktopDisplayMode().height);
            getApplication().getApplicationListener().resize(width, height);
            if (getMainViewport() != null)
                getMainViewport().setScreenSize(width, height);

        } else {
            System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
            setResolution(OptionsMaster.getGraphicsOptions().getValue(GRAPHIC_OPTION.RESOLUTION));
            getApplication().getGraphics().setUndecorated(false);
        }
        Eidolons.getApplication().getGraphics().setResizable(false);
    }

    private static void cannotFullscreen() {
        EUtils.onConfirm(
                "Sorry, cannot go fullscreen on your monitor, only 1920x1080 supported yet. ",
                false, ()->{});

//        TipMessageMaster.tip(new TipMessageSource("", "", "Carry On", false, ()->{}));

    }

    public static void setResolution(String value) {
        RESOLUTION resolution =
                new EnumMaster<RESOLUTION>().retrieveEnumConst(RESOLUTION.class, value);
        setResolution(resolution);
    }

    public static Dimension getResolutionDimensions() {
        return getResolutionDimensions(getResolution(), isFullscreen());
    }

    public static Dimension getResolutionDimensions(RESOLUTION resolution, boolean fullscreen) {
        String[] parts = resolution.toString().substring(1).
                split("x");
        Integer w =
                NumberUtils.getInteger(
                        parts[0]);
        Integer h =
                NumberUtils.getInteger(parts[1]);
//        if (Gdx.graphics.getDisplayMode())
        if (!fullscreen) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (screenSize.width <= w)
                w = w * WIDTH_WINDOWED / 100;
            if (screenSize.height <= h)
                h = h * HEIGHT_WINDOWED / 100;
        }
        return new Dimension(w, h);
    }

    public static ScreenViewport getMainViewport() {
        return mainViewport;
    }

    public static void setMainViewport(ScreenViewport mainViewport) {
        Eidolons.mainViewport = mainViewport;
    }

    public static void gameExited() {
        //        DC_Game toFinilize = game;
        GenericLauncher.setFirstInitDone(false);
        game.getMetaMaster().gameExited();

        SpecialLogger.getInstance().appendSpecialLog(SPECIAL_LOG.MAIN,
                "GAME EXIT TO MAIN MENU");
        game.exit(true);
        game = null;
        mainHero = null;
        DC_Game.game = null;
        Game.game = null;

        //        try{toFinilize.finilize();}catch(Exception e){main.system.ExceptionMaster.printStackTrace( e);}
    }

    public static GenericLauncher getLauncher() {
        return launcher;
    }

    public static void setLauncher(GenericLauncher launcher) {
        Eidolons.launcher = launcher;
    }

    public static Party getParty() {
        if (party == null)
            if (CoreEngine.isMacro()) {
                if (MacroGame.game == null) {
                    return null;
                }
                return MacroGame.game.getPlayerParty().getParty();
            } else {
                return getGame().getMetaMaster().getPartyManager().getParty();
            }
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

    public static GameScreen getScreen() {
        return launcher.gameScreen;
    }

    public static MacroGame getMacroGame() {
        return MacroGame.game;
    }

    public static void restart() {
        new Thread(() -> {
            getGame().getMetaMaster().getBattleMaster().
                    getOutcomeManager().restart();
        }, "restart thread").start();
    }

    public static void exitFromGame() {
        exitToMenu();
        GuiEventManager.trigger(GuiEventType.DISPOSE_TEXTURES);
    }

    public static void exitToMenu() {
        CoreEngine.setIggDemoRunning(false);
        DC_SoundMaster.playStandardSound(STD_SOUNDS.NEW__ENTER);
        try {
            DC_Game.game.getMetaMaster().gameExited();
            if (MacroGame.game != null) {
                if (MacroGame.game.getLoop() != null) {
                    MacroGame.game.getLoop().setExited(true);
                }

            }

            getScreen().reset();
            gameExited();
            MainMenu.getInstance().setCurrentItem(null);
            GameMenu.menuOpen = false;
            TownPanel.setActiveInstance(null);
            GdxMaster.setInputProcessor(new InputAdapter());
            showMainMenu();
            MusicMaster.getInstance().scopeChanged(MUSIC_SCOPE.MENU);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            //            DialogMaster.confirm("Game exit failed!");
            //            exitGame();
        }
    }

    public static void showMainMenu() {
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN,
                new ScreenData(SCREEN_TYPE.MAIN_MENU, "Loading..."));

        ScenarioLauncher.missionIndex = 0;
    }

    public static void activateMainHeroAction(String action) {
        activateMainHeroAction(Eidolons.getMainHero().getAction(action));
    }

    public static void activateMainHeroAction(DC_ActiveObj action) {
        Eidolons.getGame().getLoop().actionInput(
                new ActionInput((action), Eidolons.getMainHero()));
    }

    public static void exitGame() {
        DC_SoundMaster.playStandardSound(STD_SOUNDS.NEW__ENTER);
        SpecialLogger.getInstance().writeLogs();
        Debugger.writeLog();
        try {
            LogMaster.writeAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gdx.app.exit();
        System.exit(0);
    }

    public static void onThisOrNonGdxThread(Runnable o) {
        if (GdxMaster.isLwjglThread()) {
            onNonGdxThread(o);
        } else o.run();
    }

    public static void onNonGdxThread(Runnable o) {
//        if (!logicThreadBusy) {
//            SwingUtilities.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    logicThreadBusy = true;
//                    o.run();
//                    logicThreadBusy = false;
//                }
//            });
//        } else
            new Thread(o, "single task thread " + customThreadsUsed++).start();


    }

    public static RESOLUTION getResolution() {
        if (resolution == null) {
            resolution = GDX.getCurrentResolution();
        }
        return resolution;
    }

    public static void setResolution(RESOLUTION resolution) {
        if (resolution != null) {
            if (resolution != Eidolons.getResolution()) {
                if (Eidolons.getScope() != null)
                    if (Eidolons.getScope() != SCOPE.MENU) {
                        EUtils.onConfirm(
                                "New resolution will be applied on restart... Ok?", true, () ->
                                        OptionsMaster.saveOptions());
                        return;
                    }
                Eidolons.getApplication().getGraphics().setResizable(true);
                Eidolons.resolution = resolution;
                Dimension dimension = Eidolons.getResolutionDimensions(resolution, fullscreen);
                Integer w = (int)
                        dimension.getWidth();
                Integer h = (int)
                        dimension.getHeight();
                GdxMaster.setWidth(w);
                GdxMaster.setHeight(h);
                Gdx.graphics.setWindowedMode(w,
                        h);
                getApplication().getApplicationListener().resize(w, h);
                getApplication().getGraphics().setResizable(false);
                //            getMainViewport().setScreenSize(w, h);
                GdxMaster.resized();
            }
        }
    }

    public static SCOPE getScope() {
        return scope;
    }

    public static void setScope(SCOPE scope) {
        Eidolons.scope = scope;
    }

    public static void screenSet(SCREEN_TYPE type) {
        if (screenType != null)
            previousScreenType = screenType;
        screenType = type;
    }

    public static SCREEN_TYPE getScreenType() {
        return screenType;
    }

    public static SCREEN_TYPE getPreviousScreenType() {
        return previousScreenType;
    }

    public static Town getTown() {
        return getGame().getMetaMaster().getTownMaster().getTown();
    }

    public static void tryIt(Runnable o) {
        try {
            o.run();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }


    public enum SCOPE {
        MENU, BATTLE, MAP
    }
}
