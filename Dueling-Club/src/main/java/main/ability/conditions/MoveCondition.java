package main.ability.conditions;

import main.elements.conditions.ConditionImpl;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_Cell;
import main.entity.obj.unit.DC_UnitModel;

public class MoveCondition extends ConditionImpl {

    @Override
    public boolean check(Ref ref) {
        DC_UnitModel obj = (DC_UnitModel) ref.getSourceObj();
        DC_Cell cell = (DC_Cell) ref.getObj(KEYS.MATCH);
        return (game.getMovementManager().canMove(obj, cell));
    }

}
