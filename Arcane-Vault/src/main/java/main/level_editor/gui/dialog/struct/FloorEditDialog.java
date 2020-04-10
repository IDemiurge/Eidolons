package main.level_editor.gui.dialog.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.system.audio.MusicMaster;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;

public class FloorEditDialog extends DataEditDialog<LevelStructure.FLOOR_VALUES, FloorData> {
    public FloorEditDialog() {
        super(1);
    }

    @Override
    protected LevelStructure.EDIT_VALUE_TYPE getEditor(LevelStructure.FLOOR_VALUES enumConst) {
        switch (enumConst) {
            case floor_type:
                return LevelStructure.EDIT_VALUE_TYPE.objType;
            case background:
                return LevelStructure.EDIT_VALUE_TYPE.file;
            case vfx_template:
            case ambience:
                return LevelStructure.EDIT_VALUE_TYPE.enum_const;

        }
        return null;
    }

    @Override
    protected Object getArg(LevelStructure.FLOOR_VALUES enumConst) {
        switch (enumConst) {
            case background:
                return "resources/img/main/background";
            case floor_type:
                return DC_TYPE.DUNGEONS;
            case vfx_template:
                return GenericEnums.VFX.class;
            case ambience:
                return MusicMaster.AMBIENCE.class;

        }
        return null;
    }

    @Override
    protected FloorData createDataCopy(FloorData data) {
        return (FloorData) new FloorData(data.getStructure()).clear().setData(data.getData());
    }
}
