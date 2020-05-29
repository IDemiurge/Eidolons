package main.level_editor.backend.handlers.model;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.operation.Operation;
import main.level_editor.backend.handlers.selection.PaletteSelection;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.SortMaster;

import java.util.*;

import static main.level_editor.backend.handlers.operation.Operation.LE_OPERATION.PASTE_END;
import static main.level_editor.backend.handlers.operation.Operation.LE_OPERATION.PASTE_START;

public class LE_ModelManager extends LE_Handler {

    private static final int OPTION_NO_META = 0;
    private static final int OPTION_ALL = 1;
    private static final int OPTION_NO_OVERLAY = 2;
    private static final int OPTION_OVERLAYS = 4;
    private static final int OPTION_STRUCTURE = 8;

    EditorModel model;
    Stack<EditorModel> modelStack = new Stack<>();
    private static List<BattleFieldObject> copied;
    private static Map<Coordinates, CellScriptData> copiedData;

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

    public void pasteData(Coordinates origin) {
        operation(PASTE_START);
        Coordinates offset = null;
        for (Coordinates c : copiedData.keySet()) {
            // if (offset == null) {
            //     offset = c;
            // }
            // offset = Coordinates.get(offset.x - c.x, offset.y - c.y);
            if (copiedData.size() > 1 && offset == null) {
                offset = c; //the first - top left object will be our offset
            } else {
                if (offset != null) {
                    c = c.getOffset(c.getX() - offset.x,
                            c.getY() - offset.y);
                }
            }

            CellScriptData data = copiedData.get(c);
            Coordinates coordinates = offset == null ? origin : origin.getOffset(offset);
            CellScriptData prev = getScriptHandler().getScriptData(coordinates);
            operation(Operation.LE_OPERATION.CELL_SCRIPT_CHANGE, coordinates, data,
                    prev);
        }
        operation(PASTE_END);
    }

    public void pasteData() {
        pasteData(getSelectionHandler().getSelection().getFirstCoordinate());
    }

    public void paste() {
        Coordinates origin = null;
        if (getSelectionHandler().getSelection().getCoordinates().size() == 1) {
            origin = getSelectionHandler().getSelection().getFirstCoordinate();
            //            origin =getSelectionHandler().getSelectedCoordinate();
        } else {
            origin = getSelectionHandler().selectCoordinate();
        }
        //TODO all data - ai, script, layer props...

        copyTo(copied, origin);
    }

    public void copyTo(List<BattleFieldObject> copied, Coordinates origin) {
        copyTo(copied, origin, 0);
    }

    public void copyTo(List<BattleFieldObject> copied, Coordinates origin, int option) {
        Coordinates offset = null;
        operation(PASTE_START);
        for (BattleFieldObject obj : copied) {
            ObjType type = obj.getType();
            Coordinates c = origin;
            if (copied.size() > 1 && offset == null) {
                offset = obj.getCoordinates(); //the first - top left object will be our offset
            } else {
                if (offset != null) {
                    c = c.getOffset(obj.getX() - offset.x,
                            obj.getY() - offset.y);
                }
            }
            if (!checkOption(obj, option))
                continue;
            if (obj.isOverlaying()) {
                operation(Operation.LE_OPERATION.ADD_OVERLAY, type, c, obj.getDirection());
            } else
                operation(Operation.LE_OPERATION.ADD_OBJ, type, c);

        }
        operation(PASTE_END);
    }

    private boolean checkOption(BattleFieldObject obj, int option) {
        switch (option) {
            case OPTION_NO_OVERLAY:
            case OPTION_OVERLAYS:
            case OPTION_STRUCTURE:
            case OPTION_NO_META:
            case OPTION_ALL:
        }
        return true;
    }

    public void cut() {
        copy();
        getObjHandler().removeSelected();

    }

    public void copyScriptData(boolean cut) {
        copiedData = new TreeMap() {
            @Override
            public Comparator comparator() {
                return SortMaster.getSorterByExpression(obj -> -(((Coordinates) obj).getX() * 10
                        + ((Coordinates) obj).getY()));
            }
        };
        for (Coordinates c : model.getSelection().getCoordinates()) {
            copiedData.put(c, new CellScriptData(
                    getScriptHandler().getScriptData(c).getData()));
            if (cut)
                getScriptHandler().clear(c);
        }

    }

    public void copy() {

        copied = new ArrayList<>(model.getSelection().getIds().size());
        for (Integer id : model.getSelection().getIds()) {
            copied.add(getIdManager().getObjectById(id));
        }
        copied.sort(SortMaster.getSorterByExpression(obj -> -(((Obj) obj).getX() * 10 + ((Obj) obj).getY())));
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
