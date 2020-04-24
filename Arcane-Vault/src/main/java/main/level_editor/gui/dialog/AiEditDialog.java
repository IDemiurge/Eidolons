package main.level_editor.gui.dialog;

import eidolons.game.battlecraft.ai.elements.generic.AiData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import main.level_editor.LevelEditor;
import main.level_editor.backend.handlers.operation.Operation;
import main.level_editor.gui.dialog.struct.DataEditDialog;

public class AiEditDialog extends DataEditDialog<AiData.AI_VALUE, AiData> {
    public AiEditDialog() {
        super(AiData.AI_VALUE.values().length);
    }

    @Override
    public void ok() {
        LevelEditor.getManager().getOperationHandler().operation(Operation.LE_OPERATION.SAVE_DATA,
                cached, data);
        super.ok();
    }

    @Override
    protected LevelStructure.EDIT_VALUE_TYPE getEditor(AiData.AI_VALUE enumConst) {
        return null;
    }

    @Override
    protected Object getArg(AiData.AI_VALUE enumConst) {
        return null;
    }

    @Override
    protected AiData createDataCopy(AiData userObject) {
        return new AiData(userObject.getData());
    }
}
