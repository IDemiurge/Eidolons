package main.level_editor.gui.panels.control.structure;

import main.level_editor.backend.handlers.selection.ISelectionHandler;
import main.level_editor.gui.panels.control.LE_ControlPanel;

public class CtrlSelectionPanel extends LE_ControlPanel<ISelectionHandler> {
    @Override
    protected ISelectionHandler getHandler() {
        return getManager().getSelectionHandler();
    }

    @Override
    protected Class<ISelectionHandler> getClazz() {
        return ISelectionHandler.class;
    }

    @Override
    protected float getSpace() {
        return 0;
    }
}
