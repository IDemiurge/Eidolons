package main.level_editor.gui.dialog.struct;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.location.struct.BlockData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import main.level_editor.gui.dialog.struct.DataEditDialog;

public class BlockEditDialog extends DataEditDialog<LevelStructure.BLOCK_VALUE, BlockData> {
    public BlockEditDialog() {
        super(1);
    }


    @Override
    protected LevelStructure.EDIT_VALUE_TYPE getEditor(LevelStructure.BLOCK_VALUE enumConst) {
        switch (enumConst) {
            case room_type:
                return LevelStructure.EDIT_VALUE_TYPE.enum_const;
        }
        if (enumConst.getEditValueType() != null) {
            return enumConst.getEditValueType();
        }
        return null;
    }

    @Override
    protected Object getArg(LevelStructure.BLOCK_VALUE enumConst) {
        switch (enumConst) {
            case room_type:
                return LocationBuilder.ROOM_TYPE.class;
        }
        if (enumConst.getArg() != null) {
            return enumConst.getArg();
        }
        return null;
    }

    @Override
    protected BlockData createDataCopy(BlockData data) {
        return (BlockData) new BlockData(data.getStructure()).clear().setData(data.getData());
    }

}
