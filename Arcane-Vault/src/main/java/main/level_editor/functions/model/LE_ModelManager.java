package main.level_editor.functions.model;

import main.entity.Entity;
import main.entity.type.ObjType;
import main.level_editor.functions.LE_Handler;
import main.level_editor.functions.LE_Manager;
import main.level_editor.functions.selection.PaletteSelection;

public class LE_ModelManager extends LE_Handler {

    LE_DataModel model;

    public LE_ModelManager(LE_Manager manager) {
        super(manager);
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
