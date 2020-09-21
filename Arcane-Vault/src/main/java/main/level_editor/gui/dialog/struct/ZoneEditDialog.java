package main.level_editor.gui.dialog.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.game.battlecraft.logic.dungeon.location.struct.ZoneData;
import eidolons.system.audio.MusicEnums;
import main.content.enums.GenericEnums;

import static eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure.EDIT_VALUE_TYPE;
import static eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure.ZONE_VALUE;

public class ZoneEditDialog extends DataEditDialog<ZONE_VALUE, ZoneData> {
    public ZoneEditDialog() {
        super(1);
    }

    @Override
    protected EDIT_VALUE_TYPE getEditor(ZONE_VALUE enumConst) {
        switch (enumConst) {
            case ambience:
            case style:
            case vfx_template:
                return LevelStructure.EDIT_VALUE_TYPE.enum_const;

        }
        return null;
    }

    @Override
    protected Object getArg(ZONE_VALUE enumConst) {
        switch (enumConst) {
            case vfx_template:
                return GenericEnums.VFX.class;
            case ambience:
                return MusicEnums.AMBIENCE.class;

        }
        return null;
    }

    @Override
    protected ZoneData createDataCopy(ZoneData data) {
        return (ZoneData) new ZoneData(data.getStructure()).clear(). setData(data.getData());
    }
}
