package main.level_editor.gui.panels.control.impl;

import main.level_editor.backend.metadata.decor.IDecorHandler;
import main.level_editor.gui.panels.control.LE_ControlPanel;

public class CtrlDecorPanel extends LE_ControlPanel<IDecorHandler> {
    @Override
    protected IDecorHandler getHandler() {
        return getManager().getDecorHandler();
    }

    @Override
    protected Class<IDecorHandler> getClazz() {
        return IDecorHandler.class;
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
