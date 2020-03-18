package main.level_editor.gui.panels.control.structure;

import main.level_editor.backend.handlers.structure.layer.ILayerHandler;
import main.level_editor.gui.panels.control.LE_ControlPanel;

public class CtrlLayerPanel extends LE_ControlPanel<ILayerHandler> {

    @Override
    protected ILayerHandler getHandler() {
//        return LevelEditor.getCurrent().getManager().getLayerHandler();
        return null;
    }

    @Override
    protected Class<ILayerHandler> getClazz() {
        return ILayerHandler.class;
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