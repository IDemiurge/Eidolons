package main.level_editor.backend.handlers.operation;

import eidolons.entity.obj.BattleFieldObject;
import main.game.bf.Coordinates;
import main.level_editor.backend.LE_Handler;
import main.level_editor.backend.LE_Manager;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.Stack;

public class OperationHandler extends LE_Handler {
    Stack<Operation> operations=new Stack<>();

    public OperationHandler(LE_Manager manager) {
        super(manager);
    }

    public void execute(  Operation.LE_OPERATION operation, Object... args) {
        execute(false, operation, args);
    }
    public void execute(boolean undo, Operation.LE_OPERATION operation, Object... args) {
        Coordinates c;
        switch (operation) {

            case VOID:
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
            case RESTORE_VOID:
                break;
            case ADD_OBJ:
                break;
            case REMOVE_OBJ:
                break;
        }
        if (!undo)
            manager.getModelManager().operation(operation, args);
    }

    public void operation(Operation.LE_OPERATION operation, Object... args) {
        operations.add(new Operation(operation, args));
    }

    public void undo() {
        if (operations.empty()) {
            return; //TODO result
        }
        Operation op =
                operations.pop();

        revert(op);
    }

    private void revert(Operation op) {
        if (op.operation.bulkEnd){
            Operation rev = operations.pop() ;
            while (!operations.empty() && (!rev.operation.bulkStart)){
                revert(rev);
                rev = operations.pop() ;
            }

        }
        switch (op.operation) {
            case MODEL_CHANGE:
                getModelManager().back();
                //all kindsd of meta info
            case VOID:
                manager.getOperationHandler().execute(true, Operation.LE_OPERATION.VOID, op.args);
                break;
            case ADD_OBJ:
                manager.getOperationHandler().execute(true, Operation.LE_OPERATION.REMOVE_OBJ, op.args);
                break;
            case REMOVE_OBJ:
                manager.getOperationHandler().execute(true, Operation.LE_OPERATION.ADD_OBJ, op.args);
                break;
        }
    }

}
