package main.level_editor.backend.functions.advanced;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.core.EUtils;
import eidolons.game.exploration.dungeon.struct.LevelStruct;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.level_editor.backend.handlers.selection.LE_Selection;
import main.system.auxiliary.data.ListMaster;

import java.util.LinkedHashSet;
import java.util.Set;

import static main.level_editor.backend.handlers.operation.Operation.LE_OPERATION.*;

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
        operation(FILL_START);
        ObjType type = getModel().getPaletteSelection().getObjType();

        for (Coordinates coordinates : area) {
            operation(ADD_OBJ, type, coordinates);
        }
        operation(FILL_END);

    }

    @Override
    public void clear() {
        Set<Coordinates> area = getSelectionHandler().getSelection().getCoordinates();

        if (area.isEmpty()) {
            return;
        }
        operation(CLEAR_START);
        for (Coordinates coordinates : area) {
            getObjHandler().clear(coordinates);
        }
        operation(CLEAR_END);
    }

    @Override
    public void toggleVoid() {

        operation(CLEAR_START);
        Set<Coordinates> coordinates = getSelectionHandler().getSelection().getCoordinates();
        if (coordinates. size()>10) {
            if (getGame().getCell(coordinates.iterator().next()).isVOID()) {
                operation(MASS_RESET_VOID, coordinates);
            }else
                operation(MASS_SET_VOID, coordinates);
            return;
        }
        for (Coordinates c : coordinates) {
            operation(VOID_TOGGLE, c);
        }
        operation(CLEAR_END);
    }

    @Override
    public void setVoid() {

        operation(CLEAR_START);
        for (Coordinates c : getSelectionHandler().getSelection().getCoordinates()) {
            operation(VOID_SET, c);
        }
        operation(CLEAR_END);
    }
    public void mirror() {
        //TODO
        LE_Selection selection = getModel().getSelection();
        Set<Coordinates> coordinates = selection.getCoordinates();
        boolean fromSelection = false;
        if (!ListMaster.isNotEmpty(coordinates)) {
            LevelStruct struct = getModel().getLastSelectedStruct();
            coordinates = struct.getCoordinatesSet();
        }
        //including void,
        int w = CoordinatesMaster.getWidth(coordinates);
        int h = CoordinatesMaster.getHeight(coordinates);
        DIRECTION d = getDialogHandler().chooseEnum(DIRECTION.class);
        if (fromSelection) {
            //            axis = left ? :;
        }

        Coordinates origin = CoordinatesMaster.getUpperLeftCornerCoordinates(coordinates);
        operation(FILL_START);
        Set<Coordinates> toVoid = new LinkedHashSet<>();
        {
            for (Coordinates c : coordinates) {
                Coordinates c1 = c.getOffset(getMirroredCoordinate(c.getOffset(-origin.x, -origin.y), d, w, h));
                boolean VOID = getGame().getCell(c).isVOID();
                if (VOID) {
                    toVoid.add(c1);
                    continue;
                }
                Set<BattleFieldObject> objects = getGame().getObjectsOnCoordinateAll(c);
                //                c = c.getOffset(origin);
                for (BattleFieldObject object : objects) {
                    getObjHandler().copyTo(object, c1);
                }
            }
        }
        operation(MASS_SET_VOID, toVoid);

        operation(FILL_END);
    }

    private Coordinates getMirroredCoordinate(Coordinates c, DIRECTION d, int w, int h) {
        int offsetX = 0;
        int offsetY = 0;
        if (d.growX != null) {
            offsetX = d.growX ? w - c.x : -c.x;
        }
        if (d.growY != null) {
            offsetY = d.growY ? h - c.y : -c.y;
        }
        return Coordinates.get(true, offsetX, offsetY);
    }

    @Override
    public void rotate() {

    }

    @Override
    public void replace() {
        Set<BattleFieldObject> toReplace = new LinkedHashSet<>();
        boolean overlaying = getSelectionHandler().getObject().isOverlaying();
        String name = getSelectionHandler().getObject().getName();
        LevelStruct struct = getModel().getLastSelectedStruct();
        for (Object o : struct.getCoordinatesSet()) {
            for (BattleFieldObject object : getGame().getObjectsOnCoordinate((Coordinates) o, true)) {
                if (object.getName().equalsIgnoreCase(name)) {
                    toReplace.add(object);
                }
            }
        }
        ObjType replacing = overlaying ? getModel().getPaletteSelection().getObjType() : getModel().getPaletteSelection().getObjType();

        if (EUtils.waitConfirm("Replace " +
                toReplace.size() +
                " objects with " + replacing.getName())) {
            replace(toReplace, replacing, overlaying);
        }
    }

    private void replace(Set<BattleFieldObject> toReplace, ObjType replacing, boolean overlaying) {
        operation(FILL_START);
        for (BattleFieldObject object : toReplace) {
            if (overlaying) {
                operation(REMOVE_OVERLAY, object);
                operation(ADD_OVERLAY, replacing, object.getCoordinates(), object.getDirection());
            } else {
                operation(REMOVE_OBJ, object);
                operation(ADD_OBJ, replacing, object.getCoordinates());
            }
        }
        operation(FILL_END);
    }
    @Override
    public void platform() {
        getPlatformHandler().platform();
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
