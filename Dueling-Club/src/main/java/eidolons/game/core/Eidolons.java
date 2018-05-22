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
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.battlecraft.logic.meta.universal.PartyManager;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.DC_GameManager;
import eidolons.game.core.game.DC_GameObjMaster;
import eidolons.game.core.state.DC_StateManager;
import eidolons.game.module.adventure.MacroGame;
import eidolons.game.module.herocreator.CharacterCreator;
import eidolons.game.module.herocreator.logic.party.Party;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.menu.GameMenu;
import eidolons.libgdx.launch.GenericLauncher;
import eidolons.libgdx.launch.ScenarioLauncher;
import eidolons.libgdx.screens.GameScreen;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenType;
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
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.FileLogger.SPECIAL_LOG;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.log.SpecialLogger;

import java.awt.*;

/**
 * Created by JustMe on 2/15/2017.
 */
public class Eidolons {
    public static final boolean DEV_MODE = true;
    public static DC_Game game;
    public static DC_GameManager gameManager;
    public static DC_GameObjMaster gameMaster;
    public static DC_StateManager stateManager;

    public static EidolonsGame mainGame;
    public static Application gdxApplication;
    private static LwjglApplication application;
    private static String selectedMainHero;
    private static Unit mainHero;
    private static boolean fullscreen;
    private static RESOLUTION resolution;
    private static ScreenViewport mainViewport;
    private static GenericLauncher launcher;
    private static Party party;
    private static boolean battleRunning;
    private static GameScreen screen;
    private static SCOPE scope;

    public static boolean initScenario(ScenarioMetaMaster master) {
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

    public static Unit getMainHero() {
        if (mainHero == null) {
            mainHero = (Unit) game.getPlayer(true).getHeroObj();
        }
        return mainHero;
    }

    public static void setMainHero(Unit mainHero) {
        Eidolons.mainHero = mainHero;
    }

    public static boolean isFullscreen() {
        return fullscreen;
    }

    public static void setFullscreen(boolean b) {
        if (getApplication() == null)
            return;
        fullscreen = b;
        Eidolons.getApplication().getGraphics().setResizable(true);
        if (fullscreen) {
            int width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
            int height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
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

    public static void setResolution(String value) {
        RESOLUTION resolution =
         new EnumMaster<RESOLUTION>().retrieveEnumConst(RESOLUTION.class, value);
        setResolution(resolution);
    }

    public static void setResolution(RESOLUTION resolution) {
        if (resolution != null) {
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
            getMainViewport().setScreenSize(w, h);
        }
        GdxMaster.resized();
    }

    public static Dimension getResolutionDimensions(RESOLUTION resolution, boolean fullscreen) {
        String[] parts = resolution.toString().substring(1).
         split("x");
        Integer w =
         StringMaster.getInteger(
          parts[0]);
        Integer h =
         StringMaster.getInteger(parts[1]);
        if (!fullscreen) {
            w = w * 95 / 100;
            h = h * 90 / 100;
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
        PartyManager.setSelectedHero(null);
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

    public static void nextLevel() {
        new Thread(() -> {
            getGame().getMetaMaster().getBattleMaster().
             getOutcomeManager().next();
        }, "next level thread").start();

    }

    public static void exitToMenu() {
        try {
            DC_Game.game.getMetaMaster().gameExited();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
//            DialogMaster.confirm("Game exit failed!");
            exitGame();
        }
        getScreen().reset();
        gameExited();
        GameMenu.menuOpen = false;
        Gdx.input.setInputProcessor(new InputAdapter());
        showMainMenu();
        MusicMaster.getInstance().scopeChanged(MUSIC_SCOPE.MENU);
    }
    public static void showMainMenu() {
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN,
         new ScreenData(ScreenType.MAIN_MENU, "Loading..."));

        ScenarioLauncher.missionIndex = 0;
    }

    public static void nextScenario() {

    }
    public static void activateMainHeroAction(String action) {
        activateMainHeroAction(Eidolons.getMainHero().getAction(action));
    }

    public static void activateMainHeroAction(DC_ActiveObj action) {
        Eidolons.getGame().getLoop().actionInput(
         new ActionInput( (action), Eidolons.getMainHero()));
    }

    public static void exitGame() {
        SpecialLogger.getInstance().writeLogs();
        Debugger.writeLog();
        LogMaster.writeAll();
        Gdx.app.exit();
    }

    public enum SCOPE{
        MENU, BATTLE, MAP
}
    public static SCOPE getScope() {
        return scope;
    }

    public static void setScope(SCOPE scope) {
        Eidolons.scope = scope;
    }
}
