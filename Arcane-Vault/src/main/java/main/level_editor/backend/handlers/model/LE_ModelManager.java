package main.level_editor.backend.handlers.model;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.selection.PaletteSelection;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.Stack;

public class LE_ModelManager extends LE_Handler {

    private static final String DEFAULT_TYPE = "Bone Wall";
    LE_DataModel model;
    Stack<LE_DataModel> modelStack = new Stack<>();

    public LE_ModelManager(LE_Manager manager) {
        super(manager);
        model = createDefault();
    }

    private LE_DataModel createDefault() {
        LE_DataModel model = new LE_DataModel();
//model.setCoordinateSelection(CoordinatesMaster.getCenterCoordinate(getModule().getCoordinates()));
        model.setPaletteSelection(new PaletteSelection(DataManager.getType(DEFAULT_TYPE, DC_TYPE.BF_OBJ)));
        return model;
    }


    public void back() {
        model = modelStack.pop();
        GuiEventManager.trigger(GuiEventType.LE_GUI_RESET);
    }

    public void toDefault() {
        modelStack.push(model);
        model = new LE_DataModel();
    }

    public LE_DataModel getModel() {
        return model;
    }

    public void paletteSelection(Entity entity) {
        PaletteSelection selection = new PaletteSelection((ObjType) entity, false);
        getModel().setPaletteSelection(selection);
    }

    public ObjType getDefaultWallType() {
        return getModel().getDefaultWallType();
    }


}
