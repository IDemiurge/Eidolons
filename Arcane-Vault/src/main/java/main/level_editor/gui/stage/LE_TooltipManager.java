package main.level_editor.gui.stage;

import libgdx.GdxMaster;
import libgdx.gui.dungeon.tooltips.ToolTipManager;

public class LE_TooltipManager extends ToolTipManager {
    public LE_TooltipManager(LE_GuiStage guiStage) {
        super(guiStage);
    }

    @Override
    protected boolean isHidden() {
        if (GdxMaster.isVisibleEffectively(getGuiStage().getDialog())) {
            return true;
        }
        return super.isHidden();
    }

    @Override
    public LE_GuiStage getGuiStage() {
        return (LE_GuiStage) super.getGuiStage();
    }
}
