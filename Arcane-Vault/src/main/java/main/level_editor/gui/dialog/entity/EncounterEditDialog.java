package main.level_editor.gui.dialog.entity;

import eidolons.game.battlecraft.logic.battle.encounter.EncounterData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;

public class EncounterEditDialog extends EntityEditDiag<EncounterData.ENCOUNTER_VALUE, EncounterData> {
    public EncounterEditDialog(int size) {
        super(size);
    }

    @Override
    protected LevelStructure.EDIT_VALUE_TYPE getEditor(EncounterData.ENCOUNTER_VALUE enumConst) {
        return null;
    }

    @Override
    protected Object getArg(EncounterData.ENCOUNTER_VALUE enumConst) {
        return null;
    }
}
