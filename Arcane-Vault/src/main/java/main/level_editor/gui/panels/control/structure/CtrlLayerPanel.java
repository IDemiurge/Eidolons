package main.level_editor.gui.panels.control.structure;

import main.level_editor.backend.functions.handlers.ILayerHandler;
import main.level_editor.gui.panels.control.LE_ControlPanel;

public class CtrlLayerPanel extends LE_ControlPanel<ILayerHandler> {

    @Override
    protected ILayerHandler getHandler() {
        return null;
    }

    @Override
    protected Class<ILayerHandler> getClazz() {
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
