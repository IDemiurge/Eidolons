package main.level_editor.backend.handlers.operation;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.dungeon.location.layer.Layer;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.data.ListMaster;

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
                   layer.getScripts().put( c , text);
                    //color, hidden, ...
                }

                break;
            case SELECTION:
                break;
            case MODEL_CHANGE:
                break;
            case VOID_TOGGLE:
                c = (Coordinates) args[0];
                boolean isVoid = manager.getGame().toggleVoid(c);
                if (isVoid)
                    for (BattleFieldObject bfObj : manager.getGame().getObjectsAt(c)) {
                        getObjHandler().remove(bfObj);
                    }
                GuiEventManager.trigger(
                        isVoid ? GuiEventType.CELL_SET_VOID
                                : GuiEventType.CELL_RESET_VOID, c);
                //TODO no guarantee of success!!!
                break;
            case MODIFY_STRUCTURE_START:
                break;
            case MODIFY_STRUCTURE_END:
                break;
            case MOVE_OBJ:
                obj = (BattleFieldObject) args[0];
                args = new Object[]{obj.getCoordinates()};
                break;
            case ADD_OBJ:
                type = (ObjType) args[0];
                c = (Coordinates) args[1];
                BattleFieldObject unit = getObjHandler().addObj(type, c.x, c.y);
                args = new BattleFieldObject[]{unit};
                break;
            case REMOVE_OVERLAY:
                obj = (BattleFieldObject) args[0];
                d = obj.getDirection();
                //continues
            case REMOVE_OBJ:
                obj = (BattleFieldObject) args[0];
                getObjHandler().remove(obj);
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
        }
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
        if (operations.empty()) {
            return; //TODO result
        }
        Operation op =
                operations.pop();
        revert(op, false);
    }

    public void redo() {
        if (undone.empty()) {
            return;
        }
        Operation op =
                undone.pop();
        operation(true, op.operation, op.args);
    }

    private void revert(Operation op, boolean redo) {
        if (op.operation.bulkEnd) {
            Operation rev = operations.pop();
            while (!operations.empty()  ) {
                revert(rev, redo);
                rev = operations.pop();
                if (rev.operation.bulkStart) {
                    break;
                }
            }

        }
        switch (op.operation) {
            case MODEL_CHANGE:
                getModelManager().back();
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
    }

}
