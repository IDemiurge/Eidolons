package main.level_editor.gui.dialog.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.libgdx.bf.decor.DecorData;
import main.system.threading.WaitMaster;

public class DecorEditor extends DataEditDialog< DecorData.DECOR_LEVEL , DecorData> {
    public static final WaitMaster.WAIT_OPERATIONS OPERATION = WaitMaster.WAIT_OPERATIONS.DATA_EDIT;

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
        WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.DATA_EDIT, selected);
    }

    protected boolean isSaveForUndo() {
        return false;
    }
    @Override
    protected Object getArg(DecorData.DECOR_LEVEL enumConst) {
        return null;
    }

    @Override
    protected DecorData createDataCopy(DecorData userObject) {
        return new DecorData(userObject.getData());
    }
}
