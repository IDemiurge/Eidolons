package main.level_editor.gui.panels.control.structure;

import main.level_editor.backend.handlers.structure.IFloorHandler;
import main.level_editor.gui.panels.control.LE_ControlPanel;

public class CtrlFloorPanel extends LE_ControlPanel<IFloorHandler> {
    @Override
    protected IFloorHandler getHandler() {
//        return getManager().getAdvFuncs();
        return null;
    }

    @Override
    protected Class<IFloorHandler> getClazz() {
        return IFloorHandler.class;
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
