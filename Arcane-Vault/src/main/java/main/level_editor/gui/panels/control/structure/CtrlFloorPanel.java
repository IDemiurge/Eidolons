package main.level_editor.gui.panels.control.structure;

import main.level_editor.backend.functions.structure.IFloorHandler;
import main.level_editor.gui.panels.control.LE_ControlPanel;

public class CtrlFloorPanel extends LE_ControlPanel<IFloorHandler> {
    @Override
    protected IFloorHandler getHandler() {
        return null;
    }

    @Override
    protected Class<IFloorHandler> getClazz() {
        return null;
    }

    @Override
    protected int getWrap() {
        return 0;
    }

    @Override
    protected float getSpace() {
        return 0;
    }
}
