package main.level_editor.gui.dialog.entity;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.mission.encounter.EncounterData;

public class EncounterEditDialog extends EntityEditDiag<EncounterData.ENCOUNTER_VALUE, EncounterData> {
    public EncounterEditDialog() {
        super(EncounterData.ENCOUNTER_VALUE.values().length);
    }

    @Override
    protected LevelStructure.EDIT_VALUE_TYPE getEditor(EncounterData.ENCOUNTER_VALUE enumConst) {
        return null;
    }

    @Override
    protected Object getArg(EncounterData.ENCOUNTER_VALUE enumConst) {
        return null;
    }

    @Override
    protected EncounterData createDataCopy(EncounterData userObject) {
        return new EncounterData(userObject.getData());
    }
}
