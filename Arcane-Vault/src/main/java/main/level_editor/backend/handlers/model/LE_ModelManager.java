package main.level_editor.backend.handlers.model;

import eidolons.entity.obj.BattleFieldObject;
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

    EditorModel model;
    Stack<EditorModel> modelStack = new Stack<>();
    private LE_Selection copied;

    public LE_ModelManager(LE_Manager manager) {
        super(manager);
    }

    @Override
    public void load() {
        model = createDefault();
    }

    private EditorModel createDefault() {
        EditorModel model = new EditorModel();
//model.setCoordinateSelection(CoordinatesMaster.getCenterCoordinate(getModule().getCoordinates()));
        PaletteSelection.getInstance().setType(getObjHandler().getDefaultPaletteType());
        PaletteSelection.getInstance().setOverlayingType(null);

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
        //TODO all data - ai, script, layer props...
        List<BattleFieldObject> sorted = new LinkedList<>();
        for (Integer id : copied.getIds()) {
            sorted.add(getGame().getSimIdManager().getObjectById(id));
        }
        sorted.sort(SortMaster.getSorterByExpression(obj -> -(((Obj) obj).getX() + ((Obj) obj).getY())));

        copyTo(sorted, origin);
    }

    public void copyTo(List<BattleFieldObject> sorted, Coordinates origin) {
        Coordinates offset = null;
        operation(PASTE_START);
        for (BattleFieldObject obj : sorted) {
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
            if (obj.isOverlaying()) {
                operation(Operation.LE_OPERATION.ADD_OVERLAY, type, c, obj.getDirection());
            } else
                operation(Operation.LE_OPERATION.ADD_OBJ, type, c);

        }
        operation(PASTE_END);
    }

    public void copy() {
        copied = new LE_Selection(model.getSelection());

    }

    public LE_Selection getCopied() {
        return copied;
    }

    public void cut() {
        copied = new LE_Selection(model.getSelection());
        getObjHandler().removeSelected();

    }

    public void back() {
        model = modelStack.pop();
    }

    public void toDefault() {
        modelStack.push(model);
        model = createDefault();
    }

    public EditorModel getModel() {
        return model;
    }

    public void paletteSelection(ObjType entity) {
        getModel().getPaletteSelection().setType(entity);

        GuiEventManager.trigger(GuiEventType.LE_GUI_RESET, getModel());
    }

    public void modelChanged() {
        modelStack.push(model);
        model = new EditorModel(model);
    }
}
