package eidolons.libgdx.bf;

import eidolons.entity.obj.BattleFieldObject;
import main.system.datatypes.DequeImpl;

public class GridCreateData {
    private final int cols;
    private final int rows;
    private final DequeImpl<BattleFieldObject> objects;
    Integer moduleWidth , moduleHeight;
    String name;

    public GridCreateData(String name, int cols, int rows, DequeImpl<BattleFieldObject> objects, Integer moduleWidth, Integer moduleHeight) {
        this.name = name;
        this.cols = cols;
        this.rows = rows;
        this.objects = objects;
        this.moduleWidth = moduleWidth;
        this.moduleHeight = moduleHeight;
    }

    public Integer getModuleWidth() {
        return moduleWidth;
    }

    public Integer getModuleHeight() {
        return moduleHeight;
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

    public String getName() {
        return name;
    }
}
