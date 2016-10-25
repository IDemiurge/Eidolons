package main.system.auxiliary;

import main.game.GameManager;
import main.game.MicroGame;
import main.game.MicroGameState;

public abstract class Manager {
    protected static MicroGame game;
    protected static MicroGameState state;
    protected static GameManager mngr;

    public static void init(MicroGame _game, MicroGameState _state, GameManager _mngr) {
        game = _game;
        state = _state;
        mngr = _mngr;
    }

    public static MicroGame getGame() {
        return game;
    }

    public static void setGame(MicroGame game) {
        Manager.game = game;
    }

    public static MicroGameState getState() {
        return state;
    }

    public static void setState(MicroGameState state) {
        Manager.state = state;
    }

}
