package main.level_editor.gui.dialog.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.ModuleData;

public class ModuleDialog extends DataEditDialog<ModuleData> {
    public ModuleDialog() {
        super(1);
    }

    @Override
    protected void apply(ModuleData data) {
        data.apply();
    }

    @Override
    protected ModuleData createDataCopy(ModuleData userObject) {
        return new ModuleData(userObject.getStructure());
    }

}
