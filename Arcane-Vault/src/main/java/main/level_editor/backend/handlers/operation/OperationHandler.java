package main.level_editor.backend.handlers.operation;

import eidolons.content.data.EntityData;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.GridCell;
import eidolons.game.battlecraft.logic.dungeon.location.struct.StructureData;
import eidolons.game.battlecraft.logic.meta.scenario.script.CellScriptData;
import eidolons.game.exploration.dungeon.struct.LevelBlock;
import eidolons.game.exploration.dungeon.struct.LevelStruct;
import eidolons.content.consts.CellData;
import eidolons.content.consts.DecorData;
import libgdx.bf.grid.handlers.GridManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.system.EventType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.data.ListMaster;
import main.system.data.DataUnit;

import java.util.Collection;
import java.util.Stack;

public class OperationHandler extends LE_Handler {
    public Operation lastOperation;
    Stack<Operation> operations = new Stack<>();
    Stack<Operation> undone = new Stack<>();

    public OperationHandler(LE_Manager manager) {
        super(manager);
    }

    public Object[] execute(Operation.LE_OPERATION operation, Object... args) {
        Coordinates c;
        ObjType type;
        boolean set = false;
        BattleFieldObject obj = null;
        DIRECTION d = null;
        main.system.auxiliary.log.LogMaster.log(1, "operation: " + operation + " args:" + ListMaster.toStringList(args));

        EventType event = null;
        switch (operation) {
            case CELL_DATA_CHANGE:
                event = GuiEventType.CELL_RESET;
                c = (Coordinates) args[0];
                CellData cdata = (CellData) args[1];
                if (cdata == null) {
                    cdata = new CellData("");
                }
                if (cdata.getData().isEmpty()) {
                    getFloorWrapper().getCellMap().remove(c);
                } else {
                    getFloorWrapper().getCellMap().put(c, cdata);
                }
                GridCell cell = getGame().getCell(c);
                cdata.apply(cell);
                GuiEventManager.trigger(event, cell);
                break;
            case CELL_DECOR_CHANGE:
                event = GuiEventType.CELL_DECOR_RESET;
                c = (Coordinates) args[0];
                DecorData data = (DecorData) args[1];
                if (data == null) {
                    data = new DecorData("");
                }
                if (data.getData().isEmpty()) {
                    getFloorWrapper().getDecorMap().remove(c);
                } else {
                    getFloorWrapper().getDecorMap().put(c, data);
                }
                GuiEventManager.triggerWithParams(
                        event, c, data);
                break;
            case CELL_SCRIPT_CHANGE:
                event = GuiEventType.LE_CELL_SCRIPTS_LABEL_UPDATE;
                c = (Coordinates) args[0];
                CellScriptData scriptData = (CellScriptData) args[1];
                if (scriptData == null || scriptData.getData().isEmpty()) {
                    getFloorWrapper().getTextDataMap().remove(c);
                } else {
                    getFloorWrapper().getTextDataMap().put(c, scriptData);
                }
                GuiEventManager.triggerWithParams(
                        event, c, scriptData.getData());
                break;
            case SELECTION:
                break;
            case MODEL_CHANGE:
                break;
            case ADD_BLOCK:
                LevelBlock block = (LevelBlock) args[0];
                block.getZone().addBlock(block);
                break;
            case REMOVE_BLOCK:
                block = (LevelBlock) args[0];
                block.getZone().getSubParts().remove(block);
                break;
            case VOID_SET:
                set = true;
            case VOID_TOGGLE:
                c = (Coordinates) args[0];
                cell = manager.getGame().getCell(c);
                boolean isVoid = cell.isVOID();
                if (!isVoid)
                    for (BattleFieldObject bfObj : manager.getGame().getObjectsOnCoordinateAll(c)) {
                        operation(Operation.LE_OPERATION.REMOVE_OBJ, bfObj);
                    }
                if (set)
                    if (isVoid)
                        return null;
                GuiEventManager.trigger(
                        !isVoid || set ? GuiEventType.CELL_SET_VOID
                                : GuiEventType.CELL_RESET_VOID, c);

                break;
            case MASS_RESET_VOID:
            case MASS_SET_VOID:
                Collection<Coordinates> collection = (Collection<Coordinates>) args[0];
                for (Coordinates coordinates : collection) {
                    cell = manager.getGame().getCell(coordinates);
                    isVoid = !cell.isVOID();
                    if (isVoid)
                        for (BattleFieldObject bfObj : manager.getGame().getObjectsNoOverlaying(coordinates)) {
                            operation(Operation.LE_OPERATION.REMOVE_OBJ, bfObj);
                        }
                }
                if (operation == Operation.LE_OPERATION.MASS_RESET_VOID) {
                    GuiEventManager.trigger(GuiEventType.CELLS_MASS_RESET_VOID, collection);
                } else
                    GuiEventManager.trigger(GuiEventType.CELLS_MASS_SET_VOID, collection);

                GridManager.reset();
                break;
            case MOVE_OBJ:
                obj = (BattleFieldObject) args[0];
                args = new Object[]{obj.getCoordinates()};
                //TODO between blocks?
                break;
            case ADD_OBJ:
                type = (ObjType) args[0];
                c = (Coordinates) args[1];
                if (type == null) {
                    return null;
                }
                if (!getGame().getObjectsOnCoordinateAll(c).isEmpty()) {
                    Boolean overwrite = null;
                    if (args.length > 2) {
                        overwrite = (Boolean) args[2];
                    }
                    if (overwrite != null) {
                        if (overwrite) {
                            for (BattleFieldObject object : getGame().getObjectsOnCoordinateAll(c)) {
                                operation(Operation.LE_OPERATION.REMOVE_OBJ, object);
                            }
                        }
                    }
                    return null;
                }
                BattleFieldObject unit = getObjHandler().addObj(type, c.x, c.y);
                if (args[args.length - 1] == new Boolean(false)) {
                    args = new BattleFieldObject[]{unit};
                } else {
                    args = new BattleFieldObject[]{unit};
                    //                    getStructureHandler().updateTree(); //too much hassle, leave it
                }

                break;
            case REMOVE_OVERLAY:
                obj = (BattleFieldObject) args[0];
                d = obj.getDirection();
                //continues
            case REMOVE_OBJ:
                obj = (BattleFieldObject) args[0];
                getObjHandler().remove(obj);
                if (!obj.isOverlaying()) {
                    for (BattleFieldObject overlayingObject : obj.getGame().getOverlayingObjects(obj.getCoordinates())) {
                        operation(Operation.LE_OPERATION.REMOVE_OVERLAY, overlayingObject);
                    }
                }
                type = obj.getType();
                c = obj.getCoordinates();
                args = new Object[]{type, c, d};
                break;
            case ADD_OVERLAY:
                type = (ObjType) args[0];
                c = (Coordinates) args[1];
                d = (DIRECTION) args[2];
                args = new BattleFieldObject[]{getObjHandler().addOverlay(d, type, c.x, c.y)};
                break;
            case MODIFY_STRUCTURE:
                StructureData sdata = (StructureData) args[0];
                sdata.apply();
                Object levelLayer = sdata.getStructure().getLevelLayer();
                if (levelLayer instanceof LevelStruct) {
                    getGame().getDungeonMaster().resetColorMap(((LevelStruct) levelLayer).getCoordinatesSet());

                }

                getStructureHandler().reset(sdata.getLevelStruct());
                getStructureHandler().updateTree();
                //                if (data instanceof BlockData) {
                //                    getStructureManager().blockReset(((BlockData) data).getBlock());
                //                    getStructureManager().updateTree();
                //                }
                break;
            case MODIFY_DATA:
                DataUnit dataUnit = (DataUnit) args[0];
                DataUnit dataUnitPrev = (DataUnit) args[1];
                dataUnitPrev.setData(dataUnit.getData());
                break;

            case MODIFY_ENTITY:
                getEntityHandler().modified((EntityData) args[0]);
                break;
        }
        getDataHandler().setDirty(true);
        return args;
    }

    public void operation(Operation.LE_OPERATION operation, Object... args) {
        operation(false, operation, args);
    }

    public void operation(boolean redo, Operation.LE_OPERATION operation, Object... args) {
        Object[] newArgs = execute(operation, args);
        if (newArgs == null) {
            if (args.length > 0)
                main.system.auxiliary.log.LogMaster.log(1, "operation failed: "
                        + operation + " newArgs = " + ListMaster.toStringList(newArgs));
            return;
        }

        operations.add(this.lastOperation = new Operation(operation, newArgs));
        main.system.auxiliary.log.LogMaster.log(1, "operation done: " + operation +
                " args = " + ListMaster.toStringList(args));
        if (!redo)
            undone.clear();
    }

    public void undo() {
        while (!operations.empty() &&
                revert(operations.pop(), false)) ;
    }

    public void redo() {
        if (undone.empty()) {
            return;
        }
        Operation op =
                undone.pop();
        operation(true, op.operation, op.args);
    }

    private boolean revert(Operation op, boolean redo) {
        main.system.auxiliary.log.LogMaster.log(1, "Reverting " + op);
        if (op.operation.bulkEnd) {
            Operation rev = operations.pop();
            while (!operations.empty()) {
                revert(rev, redo);
                if (operations.empty()) {
                    continue;
                }
                rev = operations.pop();
                if (rev.operation.bulkStart) {
                    break;
                }
            }

        }
        switch (op.operation) {
            case MASS_SET_VOID:
                execute(Operation.LE_OPERATION.MASS_RESET_VOID, op.args[0]);
                break;
            case MASS_RESET_VOID:
                execute(Operation.LE_OPERATION.MASS_SET_VOID, op.args[0]);
                break;
            case CELL_DECOR_CHANGE:
                execute(Operation.LE_OPERATION.CELL_DECOR_CHANGE, op.args[0],
                        op.args[2],
                        op.args[1]);
                break;
            case CELL_SCRIPT_CHANGE:
                execute(Operation.LE_OPERATION.CELL_SCRIPT_CHANGE, op.args[0],
                        op.args[2],
                        op.args[1]);
                break;
            case SAVE_DATA:
                execute(Operation.LE_OPERATION.MODIFY_DATA, op.args);
                break;
            case SAVE_STRUCTURE:
                execute(Operation.LE_OPERATION.MODIFY_DATA, op.args);
                execute(Operation.LE_OPERATION.MODIFY_STRUCTURE, op.args);
                break;
            case SAVE_ENTITY_DATA:
                execute(Operation.LE_OPERATION.MODIFY_ENTITY, op.args);
                break;
            case ADD_BLOCK:
                execute(Operation.LE_OPERATION.REMOVE_BLOCK, op.args);
                break;
            case REMOVE_BLOCK:
                execute(Operation.LE_OPERATION.ADD_BLOCK, op.args);
                break;
            case MODIFY_STRUCTURE:
                return true;
            case MODEL_CHANGE:
                getModelManager().back();
                break;
            //all kindsd of meta info

            case VOID_SET:
                if (op.args == null) {
                    break;
                }
            case VOID_TOGGLE:
                execute(Operation.LE_OPERATION.VOID_TOGGLE, op.args);
                break;
            case ADD_OBJ:
                execute(Operation.LE_OPERATION.REMOVE_OBJ, op.args);
                getAiHandler().undone();
                break;
            case REMOVE_OBJ:
                execute(Operation.LE_OPERATION.ADD_OBJ, op.args);
                getAiHandler().undone();
                break;
            case ADD_OVERLAY:
                execute(Operation.LE_OPERATION.REMOVE_OVERLAY, op.args);
                break;
            case REMOVE_OVERLAY:
                execute(Operation.LE_OPERATION.ADD_OVERLAY, op.args);
                break;
        }
        if (!redo)
            undone.push(op);

        return false;
    }

    public void move(DIRECTION d) {
        switch (manager.getLayer()) {
            case obj:
                getDecorHandler().move(d);
                break;
            case decor:
                getDecorHandler().move(d);
                break;
            case script:
                getDecorHandler().move(d);
                break;
        }

    }
}
