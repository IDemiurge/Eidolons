package main.level_editor.gui.panels.control.impl;

import main.level_editor.backend.functions.advanced.IAdvFuncs;
import main.level_editor.gui.panels.control.LE_ControlPanel;

public class CtrlFuncPanel  extends LE_ControlPanel<IAdvFuncs> {
    @Override
    protected IAdvFuncs getHandler() {
        return getManager().getAdvFuncs();
    }

    @Override
    protected Class<IAdvFuncs> getClazz() {
        return IAdvFuncs.class;
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
