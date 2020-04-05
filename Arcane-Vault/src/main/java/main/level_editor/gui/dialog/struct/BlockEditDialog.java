package main.level_editor.gui.dialog.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.BlockData;

public class BlockEditDialog extends DataEditDialog<BlockData> {
    public BlockEditDialog() {
        super(1);
    }


    @Override
    protected void apply(BlockData data) {
        data.apply();
    }

    @Override
    protected BlockData createDataCopy(BlockData data) {
        return new BlockData(data.getStructure()).setData(data.getData());
    }
}
