package eidolons.ability.conditions;

import eidolons.entity.obj.GridCell;
import eidolons.entity.unit.UnitModel;
import main.elements.conditions.ConditionImpl;
import main.entity.Ref;
import main.entity.Ref.KEYS;

public class MoveCondition extends ConditionImpl {

    @Override
    public boolean check(Ref ref) {
        UnitModel obj = (UnitModel) ref.getSourceObj();
        GridCell cell = (GridCell) ref.getObj(KEYS.MATCH);
        return (game.getMovementManager().canMove(obj, cell.getCoordinates()));
    }

}
