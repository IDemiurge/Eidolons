package eidolons.ability.conditions.special;

import eidolons.entity.obj.unit.Unit;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.Ref.KEYS;

public class FleeCondition extends MicroCondition {

    KEYS key = KEYS.SOURCE;

    public FleeCondition(KEYS key) {
        this.key = key;
    }

    public FleeCondition() {

    }

    @Override
    public boolean check(Ref ref) {
        if (!(ref.getObj(key) instanceof Unit)) {
            return false;
        }
        Unit obj = (Unit) ref.getObj(key);

        if (!(obj.getX() == obj.getGame() .getGrid().getWidth() - 1 || obj
         .getX() == 0)) {
            return false;
        }
        if (!(obj.getY() == obj.getGame() .getGrid()
         .getHeight() - 1 || obj.getY() == 0)) {
            return false;
        }

        return obj.getCoordinates().getAdjacentCoordinate(
         obj.getFacing().getDirection()) == null;
    }
}
