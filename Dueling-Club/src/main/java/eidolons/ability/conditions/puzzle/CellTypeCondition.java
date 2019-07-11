package eidolons.ability.conditions.puzzle;

import eidolons.ability.conditions.DC_Condition;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import main.entity.Ref;

public class CellTypeCondition extends DC_Condition {
    private DungeonLevel.CELL_IMAGE type;

    public CellTypeCondition(DungeonLevel.CELL_IMAGE type) {
        this.type = type;
    }

    @Override
    public boolean check(Ref ref) {
        if (ref.getMatchObj() instanceof DC_Cell) {
        DC_Cell cell = (DC_Cell) ref.getMatchObj();
        if (cell.getCellType()==type) {
            return true;
        }
        }

        return false;
    }
}
