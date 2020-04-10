package main.level_editor.gui.dialog.entity;

import main.level_editor.LevelEditor;
import main.level_editor.backend.handlers.operation.Operation;
import main.level_editor.gui.dialog.struct.DataEditDialog;
import main.system.data.DataUnit;

public abstract class EntityEditDiag<S extends Enum<S>, T extends DataUnit<S>> extends DataEditDialog<S, T> {


    public EntityEditDiag(int size) {
        super(size);
    }

    @Override
    public void ok() {
        LevelEditor.getManager().getOperationHandler().execute(Operation.LE_OPERATION.SAVE_ENTITY_DATA,
                cached);
        LevelEditor.getManager().getOperationHandler().execute(Operation.LE_OPERATION.MODIFY_ENTITY,
                data);
        super.ok();
    }

    @Override
    protected T createDataCopy(T userObject) {
        return null;
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
    }

}
