package main.level_editor.backend.handlers.operation;

import eidolons.content.data.EntityData;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.location.layer.Layer;
import eidolons.game.battlecraft.logic.dungeon.location.struct.StructureData;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
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
        BattleFieldObject obj = null;
        DIRECTION d = null;
        main.system.auxiliary.log.LogMaster.log(1, "operation: " + operation + " args:" + ListMaster.toStringList(args));
        switch (operation) {
            case CELL_SCRIPT_CHANGE:
                c = (Coordinates) args[0];
                String text = (String) args[1];
                String layerName = (String) args[2];
                if (layerName != null) {
                    Layer layer = getLayerHandler().getLayer(layerName);
                    layer.getScripts().put(c, text);
                    //color, hidden, ...
                }

                GuiEventManager.triggerWithParams(
                        GuiEventType.LE_CELL_SCRIPTS_LABEL_UPDATE, c, text);
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
            case VOID_TOGGLE:
                c = (Coordinates) args[0];
                boolean isVoid = manager.getGame().toggleVoid(c);
                if (isVoid)
                    for (BattleFieldObject bfObj : manager.getGame().getObjectsAt(c)) {
                        operation(Operation.LE_OPERATION.REMOVE_OBJ, bfObj);
                    }
                GuiEventManager.trigger(
                        isVoid ? GuiEventType.CELL_SET_VOID
                                : GuiEventType.CELL_RESET_VOID, c);
                break;
            case MASS_RESET_VOID:
            case MASS_SET_VOID:
                Collection<Coordinates> collection = (Collection<Coordinates>) args[0];
                for (Coordinates coordinates : collection) {
                    isVoid = manager.getGame().toggleVoid(coordinates);
                    if (isVoid)
                        for (BattleFieldObject bfObj : manager.getGame().getObjectsAt(coordinates)) {
                            operation(Operation.LE_OPERATION.REMOVE_OBJ, bfObj);
                        }
                }
                if (operation == Operation.LE_OPERATION.MASS_RESET_VOID) {
                    GuiEventManager.trigger(GuiEventType.CELLS_MASS_RESET_VOID, collection);
                } else
                    GuiEventManager.trigger(GuiEventType.CELLS_MASS_SET_VOID, collection);
                break;
            case MODIFY_STRUCTURE_START:
                break;
            case MODIFY_STRUCTURE_END:
                break;
            case MOVE_OBJ:
                obj = (BattleFieldObject) args[0];
                args = new Object[]{obj.getCoordinates()};
                //TODO between blocks?
                break;
            case ADD_OBJ:
                type = (ObjType) args[0];
                c = (Coordinates) args[1];
                BattleFieldObject unit = getObjHandler().addObj(type, c.x, c.y);
                if (args[args.length-1]==new Boolean(false)) {
                    args = new BattleFieldObject[]{unit};
                } else {
                    args = new BattleFieldObject[]{unit};
                    getStructureHandler().updateTree();
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
                getStructureHandler().updateTree();
                break;
            case ADD_OVERLAY:
                type = (ObjType) args[0];
                c = (Coordinates) args[1];
                d = (DIRECTION) args[2];
                args = new BattleFieldObject[]{getObjHandler().addOverlay(d, type, c.x, c.y)};
                break;
            case MODIFY_STRUCTURE:
                StructureData data = (StructureData) args[0];
                data.apply();

                getStructureHandler().reset(data.getLevelStruct());
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
        args = execute(operation, args);
        operations.add(this.lastOperation = new Operation(operation, args));
        main.system.auxiliary.log.LogMaster.log(1, "operation: " + operation + " args = " + ListMaster.toStringList(args));
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
        if (op.operation.bulkEnd) {
            Operation rev = operations.pop();
            while (!operations.empty()) {
                revert(rev, redo);
                rev = operations.pop();
                if (rev.operation.bulkStart) {
                    break;
                }
            }

        }
        switch (op.operation) {
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
                return false;
            case MODEL_CHANGE:
                getModelManager().back();
                break;
            //all kindsd of meta info
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

        return true;
    }

}
