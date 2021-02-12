package main.level_editor.gui.dialog.struct;

import eidolons.content.consts.libgdx.GdxStringUtils;
import eidolons.game.battlecraft.logic.dungeon.location.struct.FloorData;
import eidolons.game.battlecraft.logic.dungeon.location.struct.LevelStructure;
import eidolons.system.audio.MusicEnums;
import main.content.DC_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.system.MetaEnums;
import main.level_editor.LevelEditor;
import main.level_editor.backend.functions.io.LE_DataHandler;
import main.level_editor.gui.components.DataTable;
import main.level_editor.gui.components.EditValueContainer;
import main.system.auxiliary.data.FileManager;

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
    protected void editItem(EditValueContainer actor, DataTable.DataPair item) {
        String name = data.getValue("name");
        super.editItem(actor, item);
        if (!name.equalsIgnoreCase(data.getValue("name"))) {

            if (isAppendStatus()) {
                String readiness = data.getValue(LevelStructure.FLOOR_VALUES.readiness);
                if ((readiness.isEmpty())) {
                    readiness = MetaEnums.READINESS.NEW.name();
                }
                data.setValue(LevelStructure.FLOOR_VALUES.filepath,
                        LE_DataHandler.PREFIX_CRAWL + data.getValue("name") + "[" +
                                readiness + "]" + ".xml");
                //TODO delete previous versions?
            } else {
                data.setValue(LevelStructure.FLOOR_VALUES.filepath, LE_DataHandler.PREFIX_CRAWL + data.getValue("name") + ".xml");
            }
        }
    }

    private boolean isAppendStatus() {
        return true;
    }

    @Override
    public void ok() {
        super.ok();
        LevelEditor.getManager().getStructureHandler().updateTree();
    }

    protected Object formatFilePath(DataTable.DataPair item, Object value) {
        String path = FileManager.formatPath(value.toString(), true, true);
        return GdxStringUtils.cropImagePath(path);
    }

    @Override
    protected Object getArg(LevelStructure.FLOOR_VALUES enumConst) {
        switch (enumConst) {
            case background:
                return "resources/img/main/background";
            case floor_type:
                return DC_TYPE.FLOORS;
            case vfx_template:
                return GenericEnums.VFX.class;
            case ambience:
                return MusicEnums.AMBIENCE.class;

        }
        return null;
    }

    @Override
    protected FloorData createDataCopy(FloorData data) {
        return (FloorData) new FloorData(data.getStructure()).clear().setData(data.getData());
    }
}
