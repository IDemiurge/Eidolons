package main.level_editor.gui.panels.control.impl;

import main.level_editor.backend.display.IDisplayHandler;
import main.level_editor.gui.panels.control.LE_ControlPanel;

public class CtrlDisplayPanel extends LE_ControlPanel<IDisplayHandler> {
    @Override
    protected IDisplayHandler getHandler() {
        return getManager().getDisplayHandler();
    }

    @Override
    protected Class<IDisplayHandler> getClazz() {
        return IDisplayHandler.class;
    }

    @Override
    protected int getWrap() {
        return 3;
    }

    @Override
    protected float getSpace() {
        return 0;
    }
}
