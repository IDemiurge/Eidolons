package main.game.bf;

import main.game.core.game.GameManager;
import main.game.core.game.GenericGame;
import main.game.core.state.MicroGameState;

public abstract class BattleFieldManager {

    protected GenericGame game;
    protected GameManager mngr;
    protected MicroGameState state;

    public BattleFieldManager(GenericGame game) {
        this.game = game;
        mngr = game.getManager();
        this.state = game.getState();

    }

    public abstract boolean isCellVisiblyFree(Coordinates c);


}
