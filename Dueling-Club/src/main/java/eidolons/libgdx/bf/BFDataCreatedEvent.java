package eidolons.libgdx.bf;

import eidolons.entity.obj.BattleFieldObject;
import main.system.datatypes.DequeImpl;

public class BFDataCreatedEvent {
    private final int cols;
    private final int rows;
    private final DequeImpl<BattleFieldObject> objects;

    public BFDataCreatedEvent(int cols, int rows, DequeImpl<BattleFieldObject> objects) {
        this.cols = cols;
        this.rows = rows;
        this.objects = objects;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public DequeImpl<BattleFieldObject> getObjects() {
        return objects;
    }
}
