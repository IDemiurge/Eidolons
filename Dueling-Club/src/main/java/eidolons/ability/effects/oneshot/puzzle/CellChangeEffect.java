package eidolons.ability.effects.oneshot.puzzle;

import eidolons.ability.effects.DC_Effect;
import eidolons.entity.obj.DC_Cell;
import main.content.CONTENT_CONSTS;
import main.content.enums.DungeonEnums;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class CellChangeEffect extends DC_Effect {
    private DungeonEnums.CELL_IMAGE type;
    private Integer variant;
    private CONTENT_CONSTS.COLOR_THEME color;

    public CellChangeEffect(DungeonEnums.CELL_IMAGE type, Integer variant, CONTENT_CONSTS.COLOR_THEME color) {
        this.type = type;
        this.variant = variant;
        this.color = color;
    }
    public CellChangeEffect(DungeonEnums.CELL_IMAGE type ) {
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
        if (getRef().getTargetObj() instanceof DC_Cell) {
            if (type != null)
                ((DC_Cell) getRef().getTargetObj()).setCellType(type);
            if (variant != null)
                ((DC_Cell) getRef().getTargetObj()).setCellVariant(variant);
            if (color != null)
                ((DC_Cell) getRef().getTargetObj()).setColorTheme(color);
            GuiEventManager.trigger(GuiEventType.CELL_RESET, getRef().getTargetObj());
            return true;
        }
        return false;
    }
}
