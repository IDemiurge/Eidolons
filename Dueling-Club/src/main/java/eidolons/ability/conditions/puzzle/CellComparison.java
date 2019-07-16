package eidolons.ability.conditions.puzzle;

import eidolons.ability.conditions.DC_Condition;
import eidolons.entity.obj.DC_Cell;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import main.entity.Ref;

public abstract class CellComparison extends DC_Condition {

    @Override
    public boolean check(Ref ref) {
        if (!(ref.getMatchObj() instanceof DC_Cell)) {
            return false;
        }
        DC_Cell cell = (DC_Cell) ref.getMatchObj();
        return checkCell(cell);
    }

    protected abstract boolean checkCell(DC_Cell cell);
}
