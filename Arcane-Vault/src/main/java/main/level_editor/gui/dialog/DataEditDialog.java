package main.level_editor.gui.dialog;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.libgdx.utils.GdxDialogMaster;
import main.level_editor.LevelEditor;
import main.level_editor.backend.handlers.operation.Operation;
import main.level_editor.gui.components.DataTable;
import main.system.data.DataUnit;

public abstract class DataEditDialog<T extends DataUnit> extends EditDialog<DataTable.DataPair> {

    T data;

    public DataEditDialog(int size) {
        super(size);
    }

    @Override
    protected void edit(DataTable.DataPair item) {
        Object value = input(item);
        String stringValue=null ;
        switch (getType(item)) {
            case text:
                 stringValue = string(value);
                break;
        }
        data.setValue(item.name, stringValue);
    }

    private String string(Object value) {
        return value.toString();
    }

    private Object input(DataTable.DataPair item) {
        return GdxDialogMaster.inputText("Set value " + item.name, item.stringValue);
    }

    @Override
    protected LevelStructure.EDIT_VALUE_TYPE getType(DataTable.DataPair datum) {
        return LevelStructure.EDIT_VALUE_TYPE.text;
    }

    @Override
    protected Object getArg(DataTable.DataPair datum) {
        return null;
    }

    @Override
    protected Object getVal(DataTable.DataPair datum) {
        return datum.value;
    }

    @Override
    protected String getName(DataTable.DataPair datum) {
        return datum.name;
    }

    @Override
    public void setUserObject(Object userObject) {
        LevelEditor.getManager().operation(Operation.LE_OPERATION.SAVE_STRUCTURE,
               createDataCopy());
        super.setUserObject(userObject);
        data = (T) userObject;
        show();
    }

    @Override
    public void ok() {
        LevelEditor.getManager().getOperationHandler().execute(Operation.LE_OPERATION.SAVE_STRUCTURE,
                createDataCopy());
        super.ok();
    }

    protected abstract T createDataCopy();


    @Override
    protected DataTable.DataPair[] initDataArray() {
        DataTable.DataPair[] array =
                new DataTable.DataPair[data.getRelevantValues().length];
        int i = 0;
        for (String relevantValue : data.getRelevantValues()) {
            array[i++] = new DataTable.DataPair(relevantValue, data.getValue(relevantValue));
        }
        return array;
    }
}
