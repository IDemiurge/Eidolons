package main.elements.conditions.standard;

import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.obj.Obj;

public class PositionCondition extends MicroCondition {

    private String key;
    private Obj cell;

    public PositionCondition(String key ) {
        this.key = key;
    }
    public PositionCondition(String key, Obj cell) {
        this.key = key;
        this.cell = cell;
    }

    @Override
    public boolean check(Ref ref) {
        Obj obj = ref.getObj(key);
        if (cell == null){
            cell = ref.getMatchObj();
        }
            if (cell == null)
                return false;

                return obj.getCoordinates().equals(cell.getCoordinates());


    }

    public enum POSITION_CONDITIONS {
        DIAGONAL,;
    }
}
