package eidolons.ability.conditions.puzzle;

import eidolons.ability.conditions.DC_Condition;
import eidolons.entity.obj.GridCell;
import main.entity.Ref;

public abstract class CellComparison extends DC_Condition {

    @Override
    public boolean check(Ref ref) {
        if (!(ref.getMatchObj() instanceof GridCell)) {
            return false;
        }
        GridCell cell = (GridCell) ref.getMatchObj();
        return checkCell(cell);
    }

    protected abstract boolean checkCell(GridCell cell);
}
