package eidolons.game.core.master;

import eidolons.game.core.game.DC_Game;
import eidolons.game.core.game.DC_GameManager;
import eidolons.game.core.game.DC_GameObjMaster;
import eidolons.game.core.state.DC_GameState;
import eidolons.game.core.state.DC_StateManager;

/**
 * Created by JustMe on 2/16/2017.
 */
public abstract class Master {

    protected DC_Game game;
    protected DC_GameManager gameManager;
    protected DC_GameObjMaster gameMaster;
    protected DC_GameState state;
    protected DC_StateManager stateManager;

    public Master(DC_Game game) {
        this.game = game;
        this.gameManager = game.getManager();
        this.state = game.getState();
        this.gameMaster = game.getMaster();
        this.stateManager = game.getStateManager();
    }

    public DC_Game getGame() {
        return game;
    }

    public DC_GameManager getGameManager() {
        return gameManager;
    }

    public DC_GameObjMaster getGameMaster() {
        return gameMaster;
    }

    public DC_GameState getState() {
        return state;
    }

    public DC_StateManager getStateManager() {
        return stateManager;
    }
}
