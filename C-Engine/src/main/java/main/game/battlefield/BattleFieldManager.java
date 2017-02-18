package main.game.battlefield;

import main.entity.obj.MicroObj;
import main.game.core.game.GameManager;
import main.game.core.game.MicroGame;
import main.game.core.state.MicroGameState;

public abstract class BattleFieldManager {

    protected MicroGame game;
    protected GameManager mngr;
    protected MicroGameState state;
    private SwingBattleField battlefield;

    public BattleFieldManager(MicroGame game, SwingBattleField battlefield) {
        this.game = game;
        this.setBattlefield(battlefield);
        mngr = game.getManager();
        this.state = game.getState();

    }

    public abstract Coordinates pickCoordinate();

    public boolean placeUnit(MicroObj unit, int x, int y) {
        getBattlefield().createObj(unit);
        return true;

    }

    public abstract boolean isCellVisiblyFree(Coordinates c);

    public SwingBattleField getBattlefield() {
        return battlefield;
    }

    public void setBattlefield(SwingBattleField battlefield) {
        this.battlefield = battlefield;
    }

}
