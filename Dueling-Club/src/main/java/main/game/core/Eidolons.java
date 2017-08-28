package main.game.core;

import com.badlogic.gdx.Application;
import main.client.cc.CharacterCreator;
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

    public static void initScenario(String data){
        mainGame = new EidolonsGame();
        mainGame.setMetaMaster(new ScenarioMetaMaster(data));
        mainGame.init();
}

//    public static void init(){
//
//    }
    public static DC_Game getGame() {
        return game;
    }

    public static void initDemoMeta() {
        initScenario(ScenarioLauncher.DEFAULT);
        CharacterCreator.setGame(getGame());
        CharacterCreator.init();
    }
}
