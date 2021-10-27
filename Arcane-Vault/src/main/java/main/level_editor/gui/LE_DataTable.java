package main.level_editor.gui;

import main.entity.Entity;
import main.level_editor.backend.handlers.LE_EditHandler;
import libgdx.gui.editor.components.DataTable;

public class LE_DataTable extends DataTable {
    public LE_DataTable(int wrap, int size) {
        super(wrap, size);
    }

    @Override
    protected DataPair[] getDataPairs(Entity editEntity) {
        return LE_EditHandler.getDataPairs(editEntity);
    }
}
