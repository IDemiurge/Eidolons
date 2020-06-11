package main.level_editor.gui.panels.control.impl;

import main.level_editor.backend.handlers.ai.IAiHandler;
import main.level_editor.gui.panels.control.LE_ControlPanel;

public class CtrlAiPanel extends LE_ControlPanel<IAiHandler> {
    @Override
    protected IAiHandler getHandler() {
        return getManager().getAiHandler();
    }

    @Override
    protected Class<IAiHandler> getClazz() {
        return IAiHandler.class;
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
