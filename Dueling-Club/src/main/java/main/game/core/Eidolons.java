package main.game.core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import main.client.cc.CharacterCreator;
import main.entity.obj.unit.Unit;
import main.game.EidolonsGame;
import main.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import main.game.core.game.DC_Game;
import main.game.core.game.DC_GameManager;
import main.game.core.game.DC_GameMaster;
import main.game.core.state.DC_StateManager;
import main.test.frontend.ScenarioLauncher;

/**
 * Created by JustMe on 2/15/2017.
 */
public class Eidolons {
    public static final boolean DEV_MODE =true ;
    public static DC_Game game;
    public static DC_GameManager gameManager;
    public static DC_GameMaster gameMaster;
    public static DC_StateManager stateManager;

    public static EidolonsGame mainGame;
    public static Application gdxApplication;
    private static LwjglApplication application;
    private static String selectedMainHero;
    private static Unit mainHero;

    public static void initScenario(ScenarioMetaMaster master){
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
        initScenario(new ScenarioMetaMaster( ScenarioLauncher.DEFAULT));
        CharacterCreator.setGame(getGame());
        CharacterCreator.init();
    }

    public static void setApplication(LwjglApplication application) {
        Eidolons.application = application;
    }

    public static LwjglApplication getApplication() {
        return application;
    }

    public static String getSelectedMainHero() {
        return selectedMainHero;
    }

    public static void setSelectedMainHero(String selectedMainHero) {
        Eidolons.selectedMainHero = selectedMainHero;
    }

    public static void setMainHero(Unit mainHero) {
        Eidolons.mainHero = mainHero;
    }

    public static Unit getMainHero() {
        return mainHero;
    }
}
