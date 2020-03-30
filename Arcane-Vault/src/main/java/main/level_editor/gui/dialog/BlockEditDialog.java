package main.level_editor.gui.dialog;

import eidolons.game.battlecraft.logic.dungeon.location.struct.BlockData;

public class BlockEditDialog extends DataEditDialog<BlockData> {
    public BlockEditDialog() {
        super(1);
    }

    @Override
    protected BlockData createDataCopy() {
        return  new BlockData(data.getStructure());
    }
}
