package main.level_editor.gui.dialog;

import main.level_editor.gui.components.DataTable;
import main.system.data.DataUnit;

public abstract class DataEditDialog<T extends DataUnit> extends EditDialog<DataTable.DataPair> {

    T data;

    public DataEditDialog(int size) {
        super(size);
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        data = (T) userObject;
}
}
