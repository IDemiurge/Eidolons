package main.level_editor.gui.panels.control.impl;

import main.level_editor.backend.functions.palette.IPaletteHandler;
import main.level_editor.gui.panels.control.LE_ControlPanel;

public class CtrlPalettePanel extends LE_ControlPanel<IPaletteHandler> {

    @Override
    protected IPaletteHandler getHandler() {
        return getManager().getPaletteHandler();
    }

    @Override
    protected Class<IPaletteHandler> getClazz() {
        return IPaletteHandler.class;
    }

    @Override
    protected float getSpace() {
        return 0;
    }
}
