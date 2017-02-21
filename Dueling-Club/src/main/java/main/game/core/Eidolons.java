package main.game.core;

import main.game.core.game.DC_Game;
import main.game.core.game.DC_GameManager;
import main.game.core.game.DC_GameMaster;
import main.game.core.state.DC_StateManager;

/**
 * Created by JustMe on 2/15/2017.
 */
public class Eidolons {
    public static  DC_Game game;
    public static   DC_GameManager gameManager;
    public static DC_GameMaster gameMaster;
    public static  DC_StateManager stateManager;
    private static ActionThread actionThread;


    public static ActionThread getActionThread() {
        if (actionThread==null )
            actionThread = new ActionThread();
        return actionThread;
    }



}
