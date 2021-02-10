package main.level_editor.gui.panels.control.impl;

import main.level_editor.LevelEditor;
import main.level_editor.backend.handlers.operation.IHandlerDelegate;
import main.level_editor.gui.panels.control.LE_ControlPanel;

public class CtrlCustomPanel extends LE_ControlPanel<IHandlerDelegate> {
    @Override
    protected IHandlerDelegate getHandler() {
        return LevelEditor.getManager().getDelegate();
    }

    @Override
    protected Class<IHandlerDelegate> getClazz() {
        return IHandlerDelegate.class;
    }

    @Override
    protected float getSpace() {
        return 0;
    }

    @Override
    protected int getWrap() {
        return 3;
    }
}
