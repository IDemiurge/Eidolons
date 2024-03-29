package eidolons.ability.effects.oneshot.puzzle;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.obj.GridCell;
import main.content.CONTENT_CONSTS;
import main.content.enums.DungeonEnums;

public class CellChangeEffect extends DC_Effect {
    private DungeonEnums.CELL_SET type;
    private Integer variant;
    private CONTENT_CONSTS.COLOR_THEME color;

    public CellChangeEffect(DungeonEnums.CELL_SET type, Integer variant, CONTENT_CONSTS.COLOR_THEME color) {
        this.type = type;
        this.variant = variant;
        this.color = color;
    }
    public CellChangeEffect(DungeonEnums.CELL_SET type ) {
        this.type = type;
    }
    public CellChangeEffect(Integer variant) {
        this.variant = variant;
    }
    public CellChangeEffect(  CONTENT_CONSTS.COLOR_THEME color) {
        this.color = color;
    }

    @Override
    public boolean applyThis() {
        if (getRef().getTargetObj() instanceof GridCell) {
            GridCell cell = (GridCell) getRef().getTargetObj();
            if (type != null)
                cell.setCellSet(type);
            if (variant != null)
                cell.setCellVariant(variant);
            if (color != null)
                cell.setColorTheme(color);

            cell.resetCell(true);
            return true;
        }
        return false;
    }
}
