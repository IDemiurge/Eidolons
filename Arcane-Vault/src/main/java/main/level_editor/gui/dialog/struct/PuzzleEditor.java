package main.level_editor.gui.dialog.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.puzzle.sub.PuzzleData;
import main.level_editor.gui.dialog.struct.DataEditDialog;
import main.system.threading.WaitMaster;

public class PuzzleEditor extends DataEditDialog<PuzzleData.PUZZLE_VALUE , PuzzleData> {

    @Override
    protected LevelStructure.EDIT_VALUE_TYPE getEditor(PuzzleData.PUZZLE_VALUE enumConst) {
        return null;
    }

    @Override
    protected Object getArg(PuzzleData.PUZZLE_VALUE enumConst) {
        return null;
    }

    @Override
    protected WaitMaster.WAIT_OPERATIONS getWaitOperation() {
        return WaitMaster.WAIT_OPERATIONS.PUZZLE_DATA_EDIT;
    }

    protected boolean isSaveForUndo() {
        return false;
    }
    @Override
    protected PuzzleData createDataCopy(PuzzleData userObject) {
        return new PuzzleData();
    }
}
