package main.level_editor.gui.dialog.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ModuleData;
import eidolons.system.audio.MusicMaster;
import main.content.enums.GenericEnums;

public class ModuleDialog extends DataEditDialog<LevelStructure.MODULE_VALUE, ModuleData> {
    public ModuleDialog() {
        super(1);
    }

    @Override
    protected LevelStructure.EDIT_VALUE_TYPE getEditor(LevelStructure.MODULE_VALUE enumConst) {
        switch (enumConst) {
            case vfx_template:
            case ambience:
                return LevelStructure.EDIT_VALUE_TYPE.enum_const;

        }
        return null;
    }

    @Override
    protected Object getArg(LevelStructure.MODULE_VALUE enumConst) {
        switch (enumConst) {
            case vfx_template:
                return GenericEnums.VFX.class;
            case ambience:
                return MusicMaster.AMBIENCE.class;

        }
        return null;
    }

    @Override
    protected ModuleData createDataCopy(ModuleData userObject) {
        return (ModuleData) new ModuleData(userObject.getStructure()).clear().setData(userObject.getData());
    }

}
