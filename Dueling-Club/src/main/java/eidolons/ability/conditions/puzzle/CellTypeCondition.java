package eidolons.ability.conditions.puzzle;

import eidolons.ability.conditions.DC_Condition;
import eidolons.entity.obj.DC_Cell;
import main.content.enums.DungeonEnums;
import main.entity.Ref;

public class CellTypeCondition extends DC_Condition {
    private DungeonEnums.CELL_IMAGE type;

    public CellTypeCondition(DungeonEnums.CELL_IMAGE type) {
        this.type = type;
    }

    @Override
    public boolean check(Ref ref) {
        if (ref.getMatchObj() instanceof DC_Cell) {
        DC_Cell cell = (DC_Cell) ref.getMatchObj();
            return cell.getCellType() == type;
        }

        return false;
    }
}
