package main.level_editor.gui.dialog.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.threading.WaitMaster;

public class CellDataEditor extends DataEditDialog<CellScriptData.CELL_SCRIPT_VALUE, CellScriptData> {

    public static final WaitMaster.WAIT_OPERATIONS OPERATION = WaitMaster.WAIT_OPERATIONS.DIALOG_SELECTION_CELL_SCRIPT_VALUE;

    @Override
    protected WaitMaster.WAIT_OPERATIONS getSelectionOperation() {
        return OPERATION;
    }

    public CellDataEditor( ) {
        super(CellScriptData.CELL_SCRIPT_VALUE.values().length);
    }

    @Override
    public void ok() {
        chosen(selected);
        close();
    }

    @Override
    protected LevelStructure.EDIT_VALUE_TYPE getEditor(CellScriptData.CELL_SCRIPT_VALUE enumConst) {
        switch (enumConst) {
            case facing:
                return LevelStructure.EDIT_VALUE_TYPE.enum_const;
        }
        return LevelStructure.EDIT_VALUE_TYPE.text;
    }

    @Override
    protected Object getArg(CellScriptData.CELL_SCRIPT_VALUE enumConst) {
        switch (enumConst) {
            case facing:
                return FACING_DIRECTION.class;
        }
        return null;
    }

    @Override
    protected CellScriptData createDataCopy(CellScriptData userObject) {
        return new CellScriptData(userObject.getData());
    }
}
