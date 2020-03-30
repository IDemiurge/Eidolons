package main.level_editor.gui.dialog;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ModuleData;
import main.level_editor.gui.components.DataTable;

public class ModuleDialog extends DataEditDialog<ModuleData> {
    public ModuleDialog( ) {
        super(1);
    }

    @Override
    protected LevelStructure.EDIT_VALUE_TYPE getType(DataTable.DataPair datum) {
        return null;
    }

    @Override
    protected DataTable.DataPair[] initDataArray() {
        for (String s : data.getValues().keySet()) {
            DataTable.DataPair pair = new DataTable.DataPair(s, data.getValue(s));

        }
        return new DataTable.DataPair[0];
    }
    @Override
    protected Object getArg(DataTable.DataPair datum) {
        return null;
    }

    @Override
    protected String getVal(DataTable.DataPair datum) {
        return null;
    }

    @Override
    protected String getName(DataTable.DataPair datum) {
        return null;
    }

    @Override
    protected ModuleData createDataCopy() {
        return null;
    }

}
