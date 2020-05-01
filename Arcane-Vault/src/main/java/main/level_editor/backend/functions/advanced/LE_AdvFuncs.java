package main.level_editor.backend.functions.advanced;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.module.dungeoncrawl.dungeon.LevelStruct;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.operation.Operation;
import main.level_editor.backend.handlers.selection.LE_Selection;
import main.system.auxiliary.data.ListMaster;

import java.util.LinkedHashSet;
import java.util.Set;

public class LE_AdvFuncs extends LE_Handler implements IAdvFuncs {

    public LE_AdvFuncs(LE_Manager manager) {
        super(manager);
    }

    @Override
    public void fill() {
        Set<Coordinates> area = getSelectionHandler().getSelection().getCoordinates();
        if (area.isEmpty()) {
            area = getSelectionHandler().selectArea();
        }
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
        Set<Coordinates> area = getSelectionHandler().getSelection().getCoordinates();

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
    public void toggleVoid() {

        operation(Operation.LE_OPERATION.CLEAR_START);
        for (Coordinates c : getSelectionHandler().getSelection().getCoordinates()) {
            operation(Operation.LE_OPERATION.VOID_TOGGLE, c);
        }
        operation(Operation.LE_OPERATION.CLEAR_END);
    }

    public void mirror() {
//TODO
        LE_Selection selection = getModel().getSelection();
        Set<Coordinates> coordinates = selection.getCoordinates();
        boolean fromSelection = false;
        if (!ListMaster.isNotEmpty(coordinates)) {
            LevelStruct struct = getModel().getLastSelectedStruct();
            coordinates= struct.getCoordinatesSet();
        }
        //including void,
        int w = CoordinatesMaster.getWidth(coordinates);
        int h = CoordinatesMaster.getHeight(coordinates);
        DIRECTION d = getDialogHandler().chooseEnum(DIRECTION.class);
        if (fromSelection) {
//            axis = left ? :;
        }

        Coordinates origin = CoordinatesMaster.getUpperLeftCornerCoordinates(coordinates);
        operation(Operation.LE_OPERATION.FILL_START);
        Set<Coordinates> toVoid = new LinkedHashSet<>();
        {
            for (Coordinates c : coordinates) {
                boolean VOID = getGame().getCellByCoordinate(c).isVOID();
                if (VOID) {
                    c = getMirroredCoordinate(c, d, w, h);
                    toVoid.add(c);
                    continue;
                }
                Set<BattleFieldObject> objects = getGame().getObjectsOnCoordinateAll(c);
                c =c.getOffset( getMirroredCoordinate(c.getOffset(-origin.x, -origin.y), d, w, h));
//                c = c.getOffset(origin);
                for (BattleFieldObject object : objects) {
                    getObjHandler().copyTo(object, c);
                }
            }
        }
        operation(Operation.LE_OPERATION.MASS_SET_VOID, toVoid);

        operation(Operation.LE_OPERATION.FILL_END);
    }

    private Coordinates getMirroredCoordinate(Coordinates c, DIRECTION d, int w, int h) {
        int offsetX = 0;
        int offsetY = 0;
        if (d.growX != null) {
            offsetX = d.growX ? w - c.x : c.x - w;
        }
        if (d.growY != null) {
            offsetY = d.growY ? h - c.y : c.y - h;
        }
        return Coordinates.get(true ,  offsetX,  offsetY);
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
