package main.level_editor.gui.dialog.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import libgdx.bf.grid.moving.PlatformData;
import main.system.threading.WaitMaster;

import static main.system.threading.WaitMaster.WAIT_OPERATIONS.PLATFORM_EDIT_DONE;

public class PlatformEditDialog extends DataEditDialog<PlatformData.PLATFORM_VALUE, PlatformData> {
    public static final WaitMaster.WAIT_OPERATIONS EDIT_DONE = PLATFORM_EDIT_DONE;

    public PlatformEditDialog() {
        super(1);
    }

    protected boolean isSaveForUndo() {
        return false;
    }

    @Override
    public void cancel() {
        super.cancel();
        WaitMaster.receiveInput(getWaitOperation(), false);
    }

    @Override
    protected WaitMaster.WAIT_OPERATIONS getWaitOperation() {
        return EDIT_DONE;
    }

    @Override
    public void ok() {
        super.ok();
        WaitMaster.receiveInput(getWaitOperation(), true);
    }

    @Override
    protected LevelStructure.EDIT_VALUE_TYPE getEditor(PlatformData.PLATFORM_VALUE enumConst) {
        return null;
    }

    @Override
    protected Object getArg(PlatformData.PLATFORM_VALUE enumConst) {
        return null;
    }

    @Override
    protected PlatformData createDataCopy(PlatformData userObject) {
        return new PlatformData(userObject.getData());
    }
}
