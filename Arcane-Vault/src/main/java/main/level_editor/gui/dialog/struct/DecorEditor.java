package main.level_editor.gui.dialog.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.content.consts.DecorData;
import main.level_editor.gui.dialog.struct.DataEditDialog;
import main.system.threading.WaitMaster;

public class DecorEditor extends DataEditDialog< DecorData.DECOR_LEVEL , DecorData> {
    public static final WaitMaster.WAIT_OPERATIONS OPERATION = WaitMaster.WAIT_OPERATIONS.DECOR_DATA_EDIT;

    public DecorEditor( ) {
        super(2);
    }

    @Override
    protected LevelStructure.EDIT_VALUE_TYPE getEditor(DecorData.DECOR_LEVEL enumConst) {
        return null;
    }

    @Override
    public void ok() {
        super.ok();
        WaitMaster.receiveInput(getWaitOperation(), selected);
    }

    protected boolean isSaveForUndo() {
        return false;
    }
    @Override
    protected Object getArg(DecorData.DECOR_LEVEL enumConst) {
        return null;
    }

    @Override
    protected WaitMaster.WAIT_OPERATIONS getWaitOperation() {
        return OPERATION;
    }

    @Override
    protected DecorData createDataCopy(DecorData userObject) {
        return new DecorData(userObject.getData());
    }
}
