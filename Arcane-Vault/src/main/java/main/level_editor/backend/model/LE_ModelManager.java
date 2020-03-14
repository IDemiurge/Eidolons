package main.level_editor.backend.model;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.selection.PaletteSelection;

public class LE_ModelManager extends LE_Handler {

    private static final String DEFAULT_TYPE = "Bone Wall";
    LE_DataModel model;

    public LE_ModelManager(LE_Manager manager) {
        super(manager);
        model = new LE_DataModel();

        model.setPaletteSelection(new PaletteSelection(DataManager.getType(DEFAULT_TYPE, DC_TYPE.BF_OBJ)));
    }

    public void undo() {
    }

    public void toDefault() {

    }

    public LE_DataModel getModel() {
        return model;
    }

    public void paletteSelection(Entity entity) {
        PaletteSelection selection=new PaletteSelection((ObjType) entity, false);
        getModel().setPaletteSelection(selection);
    }
}
