package main.level_editor.gui.panels.control.impl;

import main.level_editor.backend.functions.rng.IRngHandler;
import main.level_editor.gui.panels.control.LE_ControlPanel;

public class CtrlRngPanel extends LE_ControlPanel<IRngHandler> {
    @Override
    protected IRngHandler getHandler() {
//        return getManager().gr;
        return null;
    }

    @Override
    protected Class<IRngHandler> getClazz() {
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
