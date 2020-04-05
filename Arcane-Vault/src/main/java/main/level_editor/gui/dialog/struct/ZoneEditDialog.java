package main.level_editor.gui.dialog.struct;

import eidolons.game.battlecraft.logic.dungeon.location.struct.ZoneData;

public class ZoneEditDialog extends DataEditDialog<ZoneData> {
    public ZoneEditDialog() {
        super(1);
    }

    @Override
    protected void apply(ZoneData data) {
        data.apply();
    }

    @Override
    protected ZoneData createDataCopy(ZoneData data) {
        return (ZoneData) new ZoneData(data.getStructure()).setData(data.getData());
    }
}
