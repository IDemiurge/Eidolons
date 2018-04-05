package eidolons.game.core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import eidolons.client.cc.CharacterCreator;
import eidolons.client.cc.logic.party.Party;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import eidolons.game.battlecraft.logic.meta.universal.PartyManager;
import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.DC_GameManager;
import eidolons.game.core.game.DC_GameMaster;
import eidolons.game.core.state.DC_StateManager;
import eidolons.game.module.adventure.MacroGame;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.launch.GenericLauncher;
import eidolons.libgdx.launch.ScenarioLauncher;
import eidolons.libgdx.screens.GameScreen;
import eidolons.libgdx.screens.ScreenData;
import eidolons.libgdx.screens.ScreenType;
import eidolons.system.options.GraphicsOptions.GRAPHIC_OPTION;
import eidolons.system.options.OptionsMaster;
import eidolons.test.frontend.RESOLUTION;
import main.game.core.game.Game;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.awt.*;

/**
 * Created by JustMe on 2/15/2017.
 */
public class Eidolons {
    public static final boolean DEV_MODE = true;
    public static DC_Game game;
    public static DC_GameManager gameManager;
    public static DC_GameMaster gameMaster;
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

    public static boolean initScenario(ScenarioMetaMaster master) {
        mainGame = new EidolonsGame();
        mainGame.setMetaMaster(master);
        mainGame.init();
        if (mainGame.isAborted()) {
            master.gameExited();
            toMainMenu();
            return false;
        }

        return true;
    }

    private static void toMainMenu() {
        GuiEventManager.trigger(GuiEventType.SWITCH_SCREEN,
         new ScreenData(ScreenType.MAIN_MENU, "Back to the Void..."));

        ScenarioLauncher.missionIndex = 0;
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
        PartyManager.setSelectedHero(null);
        game.getMetaMaster().gameExited();
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
}
