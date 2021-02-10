package main.level_editor.gui.panels.control.impl;

import main.level_editor.backend.handlers.structure.IStructureHandler;
import main.level_editor.gui.panels.control.LE_ControlPanel;

public class CtrlStructurePanel extends LE_ControlPanel<IStructureHandler> {


    public CtrlStructurePanel() {
        super();
    }

    @Override
    protected IStructureHandler getHandler() {
        return getManager().getStructureHandler();
    }

    @Override
    protected Class<IStructureHandler> getClazz() {
        return IStructureHandler.class;
    }

    @Override
    protected int getWrap() {
        return 4;
    }

    @Override
    protected float getSpace() {
        return 0;
    }
}
