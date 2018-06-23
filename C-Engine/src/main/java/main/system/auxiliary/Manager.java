package main.system.auxiliary;

import main.game.core.game.GameManager;
import main.game.core.game.GenericGame;
import main.game.core.state.MicroGameState;

public abstract class Manager {
    protected static GenericGame game;
    protected static MicroGameState state;
    protected static GameManager mngr;

    public static void init(GenericGame _game, MicroGameState _state, GameManager _mngr) {
        game = _game;
        state = _state;
        mngr = _mngr;
    }

    public static GenericGame getGame() {
        return game;
    }

    public static void setGame(GenericGame game) {
        Manager.game = game;
    }

    public static MicroGameState getState() {
        return state;
    }

    public static void setState(MicroGameState state) {
        Manager.state = state;
    }

}
