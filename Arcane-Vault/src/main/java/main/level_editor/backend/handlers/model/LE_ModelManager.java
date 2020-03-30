package main.level_editor.backend.handlers.model;

import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.operation.Operation;
import main.level_editor.backend.handlers.selection.LE_Selection;
import main.level_editor.backend.handlers.selection.PaletteSelection;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.SortMaster;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import static main.level_editor.backend.handlers.operation.Operation.LE_OPERATION.PASTE_END;
import static main.level_editor.backend.handlers.operation.Operation.LE_OPERATION.PASTE_START;

public class LE_ModelManager extends LE_Handler {

    EditData model;
    Stack<EditData> modelStack = new Stack<>();
    private LE_Selection copied;

    public LE_ModelManager(LE_Manager manager) {
        super(manager);
    }

    @Override
    public void load() {
        model = createDefault();
    }

    private EditData createDefault() {
        EditData model = new EditData();
//model.setCoordinateSelection(CoordinatesMaster.getCenterCoordinate(getModule().getCoordinates()));
        model.setPaletteSelection(new PaletteSelection());
        return model;
    }

    public void paste() {
        Coordinates origin = null;
        if (getSelectionHandler().getSelection().getCoordinates().size() == 1) {
            origin = getSelectionHandler().getSelection().getCoordinates().iterator().next();
//            origin =getSelectionHandler().getSelectedCoordinate();
        } else {
            origin = getSelectionHandler().selectCoordinate();
        }
        operation(PASTE_START);
        //TODO all data - ai, script, layer props...
        Coordinates offset = null;
        List<Obj> sorted = new LinkedList<>();
        for (Integer id : copied.getIds()) {
            sorted.add(getGame().getSimIdManager().getObjectById(id));
        }
        sorted.sort(SortMaster.getSorterByExpression(obj -> -(((Obj) obj).getX() + ((Obj) obj).getY())));

        for (Obj obj : sorted) {
            ObjType type = obj.getType();
            Coordinates c = origin;
            if (copied.getIds().size() > 1 && offset == null) {
                offset = obj.getCoordinates();
            } else {
                if (offset != null) {
                    c = c.getOffset(obj.getX() - offset.x,
                            obj.getY() - offset.y);
                }
            }

            operation(Operation.LE_OPERATION.ADD_OBJ, type, c);

        }
        operation(PASTE_END);
    }

    public void copy() {
        copied = new LE_Selection(model.getSelection());

    }

    public void back() {
        model = modelStack.pop();
        GuiEventManager.trigger(GuiEventType.LE_GUI_RESET);
    }

    public void toDefault() {
        modelStack.push(model);
        model = createDefault();
    }

    public EditData getModel() {
        return model;
    }

    public void paletteSelection(ObjType entity) {
        getModel().getPaletteSelection().setType(entity);
    }

    public ObjType getDefaultWallType() {
        return getModel().getDefaultWallType();
    }


    public void modelChanged() {
        modelStack.push(model);
        model = new EditData(model);
    }
}
