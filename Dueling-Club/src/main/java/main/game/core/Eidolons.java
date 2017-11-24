package main.game.core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import main.client.cc.CharacterCreator;
import main.entity.obj.unit.Unit;
import main.game.EidolonsGame;
import main.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import main.game.core.game.DC_Game;
import main.game.core.game.DC_GameManager;
import main.game.core.game.DC_GameMaster;
import main.game.core.state.DC_StateManager;
import main.libgdx.GdxMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.options.GraphicsOptions.GRAPHIC_OPTION;
import main.system.options.OptionsMaster;
import main.test.frontend.RESOLUTION;
import main.test.frontend.ScenarioLauncher;

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

    public static void initScenario(ScenarioMetaMaster master) {
        mainGame = new EidolonsGame();
        mainGame.setMetaMaster(master);
        mainGame.init();
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
        if (fullscreen) {
            GdxMaster.setWidth(LwjglApplicationConfiguration.getDesktopDisplayMode().width);
            GdxMaster.setHeight(LwjglApplicationConfiguration.getDesktopDisplayMode().height);
        } else {
            setResolution(OptionsMaster.getGraphicsOptions().getValue(GRAPHIC_OPTION.RESOLUTION));
        }

        getApplication().getGraphics().setUndecorated(fullscreen);

    }

    public static void setResolution(String value) {
        RESOLUTION resolution =
         new EnumMaster<RESOLUTION>().retrieveEnumConst(RESOLUTION.class, value);
        setResolution(resolution);
    }

    public static void setResolution(RESOLUTION resolution) {
        if (resolution != null) {
            Eidolons.resolution = resolution;
            Dimension dimension = Eidolons.getResolutionDimensions(resolution, fullscreen);
            Integer w = (int)
             dimension.getWidth();
            Integer h = (int)
             dimension.getHeight();
            GdxMaster.setWidth(w);
            GdxMaster.setHeight(h);
        }

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
}
