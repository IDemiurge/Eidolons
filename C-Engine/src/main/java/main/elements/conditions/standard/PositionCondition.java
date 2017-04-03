package main.elements.conditions.standard;

import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.obj.Obj;

public class PositionCondition extends MicroCondition {

    private String key;
    private Obj cell;

    public PositionCondition(String key, Obj cell) {
        this.key = key;
        this.cell = cell;
    }

    @Override
    public boolean check(Ref ref) {
        Obj obj = ref.getObj(key);
        if (cell != null) {
            return obj.getCoordinates().equals(cell.getCoordinates());
        }

        return false;
    }

    public enum POSITION_CONDITIONS {
        DIAGONAL,;
    }
}
