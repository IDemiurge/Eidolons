package main.game.bf;

import main.game.core.game.GameManager;
import main.game.core.game.MicroGame;
import main.game.core.state.MicroGameState;

public abstract class BattleFieldManager {

    protected MicroGame game;
    protected GameManager mngr;
    protected MicroGameState state;

    public BattleFieldManager(MicroGame game ) {
        this.game = game;
        mngr = game.getManager();
        this.state = game.getState();

    }

    public abstract Coordinates pickCoordinate();



    public abstract boolean isCellVisiblyFree(Coordinates c);


}
