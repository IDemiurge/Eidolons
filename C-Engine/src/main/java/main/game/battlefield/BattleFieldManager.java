package main.game.battlefield;

import main.entity.obj.MicroObj;
import main.entity.type.ObjType;
import main.game.GameManager;
import main.game.MicroGame;
import main.game.MicroGameState;

public abstract class BattleFieldManager {

    protected MicroGame game;
    protected GameManager mngr;
    protected MicroGameState state;
    private BattleField battlefield;

    public BattleFieldManager(MicroGame game, BattleField battlefield) {
        this.game = game;
        this.setBattlefield(battlefield);
        mngr = game.getManager();
        this.state = game.getState();

    }

    public abstract Coordinates pickCoordinate();

    public boolean placeUnit(MicroObj unit) {
        return placeUnit(unit, unit.getX(), unit.getY());
    }

    public abstract MicroObj createMapObject(int x, int y, ObjType type);

    public boolean placeUnit(MicroObj unit, int x, int y) {
        getBattlefield().createObj(unit);
        return true;

    }

    public abstract boolean isCellVisiblyFree(Coordinates c);

    public BattleField getBattlefield() {
        return battlefield;
    }

    public void setBattlefield(BattleField battlefield) {
        this.battlefield = battlefield;
    }

}
