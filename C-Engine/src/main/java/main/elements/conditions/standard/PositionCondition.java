package main.elements.conditions.standard;

import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;

public class PositionCondition extends MicroCondition {

    private Coordinates c;
    private String key;

    public PositionCondition(String key) {
        this.key = key;
    }

    public PositionCondition(String key, Coordinates c) {
        this.key = key;
        this.c = c;
    }

    public PositionCondition(Coordinates c) {
        this(Ref.KEYS.MATCH.toString(), c);
    }

    @Override
    public boolean check(Ref ref) {
        Obj obj = ref.getObj(key);
        if (obj == null) {
            obj = ref.getSourceObj();
        }
        if (c == null) {
            Obj cell = ref.getMatchObj();
            if (cell == null)
                return false;
            return obj.getCoordinates().equals(cell.getCoordinates());
        } else {
            return obj.getCoordinates().equals(c);
        }

    }

    public enum POSITION_CONDITIONS {
        DIAGONAL,
        ;
    }
}
