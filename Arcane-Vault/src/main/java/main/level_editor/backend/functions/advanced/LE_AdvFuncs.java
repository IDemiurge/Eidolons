package main.level_editor.backend.functions.advanced;

import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.operation.Operation;
import main.level_editor.backend.handlers.selection.LE_Selection;
import main.system.auxiliary.data.ListMaster;

import java.util.Set;

public class LE_AdvFuncs extends LE_Handler implements IAdvFuncs {

    public LE_AdvFuncs(LE_Manager manager) {
        super(manager);
    }

    @Override
    public void fill() {
        Set<Coordinates> area = getSelectionHandler().selectArea();
        if (area.isEmpty()) {
            return;
        }
        operation(Operation.LE_OPERATION.FILL_START);
        ObjType type = getModel().getPaletteSelection().getObjType();

        for (Coordinates coordinates : area) {
            operation(Operation.LE_OPERATION.ADD_OBJ, type, coordinates);
        }
        operation(Operation.LE_OPERATION.FILL_END);

    }

    @Override
    public void clear() {
        Set<Coordinates> area = getSelectionHandler().selectArea();
        if (area.isEmpty()) {
            return;
        }
        operation(Operation.LE_OPERATION.CLEAR_START);
        for (Coordinates coordinates : area) {
            getObjHandler().clear(coordinates);
        }
        operation(Operation.LE_OPERATION.CLEAR_END);
    }

    @Override
    public void setVoid() {
        Coordinates c = getSelectionHandler().selectCoordinate();
        if (c != null) {
            getGame().toggleVoid(c);
        }
    }

    public void mirror() {
//TODO
        LE_Selection selection = getModel().getSelection();
        if (!ListMaster.isNotEmpty(selection.getCoordinates())) {
            getSelectionHandler().selectCoordinate();
            getSelectionHandler().selectArea();
        }
    }

    @Override
    public void rotate() {
//TODO

    }

    @Override
    public void repeat() {
        switch (getOperationHandler().lastOperation.getOperation()) {
            case FILL_END:
                fill();
                break;
            case CLEAR_END:
                clear();
                break;
            case INSERT_END:
//                fill();
                break;
            case PASTE_END:
//                fill();
                break;

        }
    }
}
