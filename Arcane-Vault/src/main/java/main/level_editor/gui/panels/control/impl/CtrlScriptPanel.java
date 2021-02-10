package main.level_editor.gui.panels.control.impl;

import main.level_editor.LevelEditor;
import main.level_editor.backend.metadata.script.IScriptHandler;
import main.level_editor.gui.panels.control.LE_ControlPanel;

public class CtrlScriptPanel extends LE_ControlPanel<IScriptHandler> {
    @Override
    protected IScriptHandler getHandler() {
        return LevelEditor.getManager().getScriptHandler();
    }

    @Override
    protected Class<IScriptHandler> getClazz() {
        return IScriptHandler.class;
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
