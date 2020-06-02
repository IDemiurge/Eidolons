package main.level_editor.gui.dialog.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import main.content.CONTENT_CONSTS;
import main.game.bf.directions.FACING_DIRECTION;
import main.level_editor.gui.components.DataTable;
import main.level_editor.gui.components.EditValueContainer;
import main.system.threading.WaitMaster;

import static eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure.EDIT_VALUE_TYPE.*;

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
    protected void editItem(EditValueContainer actor, DataTable.DataPair item) {
        // item.value
        super.editItem(actor, item);
    }

    @Override
    protected LevelStructure.EDIT_VALUE_TYPE getEditor(CellScriptData.CELL_SCRIPT_VALUE enumConst) {
        switch (enumConst) {
            case flip:
            case facing:
                return enum_const;
            case portals:
                return coordinates;
            //     return new MultiEditor(coordinates, );
        }
        return text;
    }

    @Override
    protected EditValueContainer createElement_(DataTable.DataPair datum) {
        return super.createElement_(datum);
    }

    @Override
    protected Object getArg(CellScriptData.CELL_SCRIPT_VALUE enumConst) {
        switch (enumConst) {
            case flip:
                return CONTENT_CONSTS.FLIP.class;
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
