package main.game.core;

import main.game.EidolonsGame;
import main.game.battlecraft.logic.dungeon.DungeonData.DUNGEON_VALUE;
import main.game.battlecraft.logic.meta.scenario.ScenarioMetaMaster;
import main.game.core.game.DC_Game;
import main.game.core.game.DC_GameManager;
import main.game.core.game.DC_GameMaster;
import main.game.core.state.DC_StateManager;

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

    public static void initScenario(String data){
        mainGame.setMetaMaster(new ScenarioMetaMaster(data));
        mainGame.init();
}

}
