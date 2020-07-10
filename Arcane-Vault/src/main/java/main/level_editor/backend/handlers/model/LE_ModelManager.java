package main.level_editor.backend.handlers.model;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.bf.datasource.GraphicData;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
    private static Map<Coordinates, String> copiedScripts;
    private static Map<Coordinates, Boolean> copiedVoid;

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
        switch (getLayer()) {
            case decor:
                getDecorHandler().cut();
                return;
            case script:
                getScriptHandler().cut();
                return;
            case obj:
                break;
        }
        copy();
        getObjHandler().removeSelected();

    }

    public void clear() {
        switch (getLayer()) {
            case decor:
                getDecorHandler().clear();
                return;
            case script:
                getScriptHandler().clear();
                return;
            case obj:
                getObjHandler().removeSelected();
                return;
        }
    }

    public void paste() {
        Coordinates origin = getSelectionHandler().getSelection().getFirstCoordinate();
        switch (getLayer()) {
            case decor:
                getDecorHandler().paste();
                return;
            case script:
                getScriptHandler().paste();
                return;
            case obj:
                copyTo(copied, origin);
                return;
        }
    }

    public void copy() {
        switch (getLayer()) {
            case decor:
                getDecorHandler().copy();
                return;
            case script:
                getScriptHandler().copy();
                return;
            case obj:
                break;
        }
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
        setPaletteType(entity);
    }

    public void modelChanged() {
        modelStack.push(model);
        model = new EditorModel(model);
        GuiEventManager.trigger(GuiEventType.LE_MODEL_CHANGED, getModel());
    }

    public void setPaletteType(ObjType type) {
        getModel().getPaletteSelection().setType(type);
        getModel().setBrushMode(false);
        manager.setLayer(LE_Manager.LE_LAYER.obj);
        modelChanged();
    }

    public void paletteDecorSelection(String data) {
        getModel().getPaletteSelection().setDecorData(new GraphicData(data));
        getModel().setBrushMode(false);
        manager.setLayer(LE_Manager.LE_LAYER.decor);
        modelChanged();

    }

}
