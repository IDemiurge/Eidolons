package main.level_editor.gui.panels.control;

import main.level_editor.backend.functions.advanced.IAdvFuncs;

public class CtrlFuncsPanel extends LE_ControlPanel<IAdvFuncs> {
    @Override
    protected IAdvFuncs getHandler() {
        return null;
    }

    @Override
    protected Class<IAdvFuncs> getClazz() {
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
