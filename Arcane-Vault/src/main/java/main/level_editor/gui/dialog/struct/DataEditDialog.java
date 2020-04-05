package main.level_editor.gui.dialog.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.libgdx.utils.GdxDialogMaster;
import main.level_editor.LevelEditor;
import main.level_editor.backend.handlers.operation.Operation;
import main.level_editor.gui.components.DataTable;
import main.level_editor.gui.dialog.EditDialog;
import main.system.data.DataUnit;

public abstract class DataEditDialog<T extends DataUnit> extends EditDialog<DataTable.DataPair> {

    protected T data;
    protected T cached;

    public DataEditDialog(int size) {
        super(size);
    }

    @Override
    protected void edit(DataTable.DataPair item) {
        Object value = input(item);
        String stringValue = null;
        switch (getType(item)) {
            case text:
                stringValue = string(value);
                break;
        }
        data.setValue(item.name, stringValue);
        setUpdateRequired(true);
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
        super.setUserObject(userObject);
        cached =(T) userObject;
        data =createDataCopy(cached);

        show();
    }

    @Override
    public void ok() {
        LevelEditor.getManager().getOperationHandler().execute(Operation.LE_OPERATION.MODIFY_STRUCTURE,
                cached);
        apply(data);
        super.ok();
    }

    protected abstract void apply(T data);

    protected abstract T createDataCopy(T userObject);


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
