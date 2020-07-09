package main.level_editor.gui.dialog.struct;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.libgdx.gui.utils.FileChooserX;
import eidolons.libgdx.utils.GdxDialogMaster;
import main.game.bf.Coordinates;
import main.level_editor.LevelEditor;
import main.level_editor.backend.handlers.LE_EditHandler;
import main.level_editor.backend.handlers.operation.Operation;
import main.level_editor.gui.components.DataTable;
import main.level_editor.gui.components.EditValueContainer;
import main.level_editor.gui.dialog.EditDialog;
import main.level_editor.gui.screen.LE_Screen;
import main.system.PathUtils;
import main.system.auxiliary.data.FileManager;
import main.system.data.DataUnit;
import main.system.threading.WaitMaster;

public abstract class DataEditDialog<S extends Enum<S>, T extends DataUnit<S>> extends EditDialog<DataTable.DataPair> {

    protected T data;
    protected T cached;
    private boolean nested;

    public DataEditDialog(int size) {
        super(size);
    }

    public DataEditDialog() {
    }

    @Override
    protected void editItem(EditValueContainer actor, DataTable.DataPair item) {
        Object value = null;
        String stringValue = null;
        LevelStructure.EDIT_VALUE_TYPE type = actor.getType();
        if (type == null) {
            type = LevelStructure.EDIT_VALUE_TYPE.text;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
            type = LevelStructure.EDIT_VALUE_TYPE.text;
        }
        switch (type) {
            case dataUnit:
                if (actor.getEdit_arg() instanceof DataUnit) {
                    DataUnit edit_arg = (DataUnit) actor.getEdit_arg();
                    if (data.getValue(item.name) != null) {
                        edit_arg.setData(data.getValue(item.name));
                    }
                    LE_EditHandler editHandler = LevelEditor.getManager().getEditHandler();
                    DataEditDialog editDialog = editHandler.getEditDialog(edit_arg);
                    WaitMaster.WAIT_OPERATIONS event = editDialog.getWaitOperation();
                    editDialog.setNested(true);
                    editHandler.editData(edit_arg);
                    value = WaitMaster.waitForInput(event) + "";
                    editDialog.setNested(false);
                    //reset?
                }

                break;
            case script:
                value = inputScript(item);
                break;
            case coordinates:
                value = pickCoordinate();
                if (value != null)
                    if (actor.getEdit_arg() != null) {
                        value = ((Coordinates) value).getOffset(
                                LevelEditor.getManager().getEditHandler().getEditCoordinates().negative());
                    }
                break;
            case enum_const:
                value = enumConst(value, actor.getEdit_arg());
                break;
            case file:
                value = FileChooserX.chooseFile((String) actor.getEdit_arg(), null, getStage());
                value = formatFilePath(item, value);
                break;
            case none:
                return;
            default:
                value = input(item);
                break;
        }
        stringValue = string(value);
        data.setValue(item.name, stringValue);
        setUpdateRequired(true);
    }


    private Coordinates pickCoordinate() {
        return LevelEditor.getManager().getSelectionHandler().selectCoordinate();
    }

    protected Object formatFilePath(DataTable.DataPair item, Object value) {
        String path = FileManager.formatPath(value.toString(), true, true);
        return PathUtils.cropResourcePath(path);
    }

    private Object enumConst(Object value, Object edit_arg) {
        return LE_Screen.getInstance().getGuiStage().getEnumChooser().chooseEnum((Class<S>) edit_arg);
    }

    private String string(Object value) {
        return value.toString();
    }

    private Object input(DataTable.DataPair item) {
        return GdxDialogMaster.inputText("Set value " + item.name, item.stringValue);
    }

    private String inputScript(DataTable.DataPair item) {
        return GdxDialogMaster.inputScript("Set script for " + item.name, item.stringValue);
    }

    @Override
    protected LevelStructure.EDIT_VALUE_TYPE getSpecificType(DataTable.DataPair datum) {
        if (data != null) {
            S enumConst = data.getEnumConst(datum.name);
            if (enumConst instanceof LevelStructure.EditableValue) {
                LevelStructure.EDIT_VALUE_TYPE type = ((LevelStructure.EditableValue) enumConst).getEditValueType();
                if (type != null) {
                    return type;
                }
            }
            return getEditor(enumConst);
        }
        return LevelStructure.EDIT_VALUE_TYPE.text;
    }

    protected abstract LevelStructure.EDIT_VALUE_TYPE getEditor(S enumConst);

    @Override
    protected Object getSpecificArg(DataTable.DataPair datum) {
        if (data != null) {
            S enumConst = data.getEnumConst(datum.name);
            if (enumConst instanceof LevelStructure.EditableValue) {
                Object arg = ((LevelStructure.EditableValue) enumConst).getArg();
                if (arg != null) {
                    return arg;
                }
            }
            return getArg(enumConst);
        }
        return null;
    }

    protected abstract Object getArg(S enumConst);

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
        data = (T) userObject;
        cached = createDataCopy(data);

        show();
    }

    protected WaitMaster.WAIT_OPERATIONS getWaitOperation() {
        return WaitMaster.WAIT_OPERATIONS.DATA_EDIT;
    }

    @Override
    public void ok() {
        if (isSaveForUndo()) {
            LevelEditor.getManager().getOperationHandler().operation(getSaveOperation(),
                    cached, data);
            LevelEditor.getManager().getOperationHandler().execute(getModifyOperation(),
                    data);
        }
        super.ok();
        WaitMaster.receiveInput(getWaitOperation(), getUserObject());
    }

    protected Operation.LE_OPERATION getModifyOperation() {
        return Operation.LE_OPERATION.MODIFY_STRUCTURE;
    }
    protected Operation.LE_OPERATION getSaveOperation() {
        return Operation.LE_OPERATION.SAVE_STRUCTURE;
    }

    protected boolean isSaveForUndo() {
        return true;
    }

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

    public void edit(T data) {
        setUserObject(data);
    }

    public void setNested(boolean nested) {
        this.nested = nested;
    }

    public boolean isNested() {
        return nested;
    }
}
