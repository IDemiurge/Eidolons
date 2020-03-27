package main.level_editor.gui.dialog;

import main.level_editor.backend.handlers.dialog.ModuleData;
import main.level_editor.gui.components.DataTable;
import main.level_editor.gui.components.EditValueContainer;

public class ModuleDialog extends DataEditDialog<ModuleData> {
    public ModuleDialog(int size) {
        super(size);
    }

    @Override
    protected EditValueContainer.EDIT_VALUE_TYPE getType(DataTable.DataPair datum) {
        return null;
    }

    @Override
    protected DataTable.DataPair[] initDataArray() {
        for (String s : data.getValues().keySet()) {
            DataTable.DataPair pair = new DataTable.DataPair(s, data.getValue(s));

        }
        return new DataTable.DataPair[0];
    }
    @Override
    protected Object getArg(DataTable.DataPair datum) {
        return null;
    }

    @Override
    protected String getVal(DataTable.DataPair datum) {
        return null;
    }

    @Override
    protected String getName(DataTable.DataPair datum) {
        return null;
    }

}
