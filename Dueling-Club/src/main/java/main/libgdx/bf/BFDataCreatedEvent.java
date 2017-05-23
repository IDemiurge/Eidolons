package main.libgdx.bf;

import main.entity.obj.BattleFieldObject;
import main.system.datatypes.DequeImpl;

public class BFDataCreatedEvent {
    private final int gridW;
    private final int gridH;
    private final DequeImpl<BattleFieldObject> objects;

    public BFDataCreatedEvent(int gridW, int gridH, DequeImpl<BattleFieldObject> objects) {
        this.gridW = gridW;
        this.gridH = gridH;
        this.objects = objects;
    }

    public int getGridW() {
        return gridW;
    }

    public int getGridH() {
        return gridH;
    }

    public DequeImpl<BattleFieldObject> getObjects() {
        return objects;
    }
}
