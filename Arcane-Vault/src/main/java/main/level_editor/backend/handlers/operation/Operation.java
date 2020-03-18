package main.level_editor.backend.handlers.operation;

public class Operation {

    protected LE_OPERATION operation;
    protected Object[] args;

    public Operation(LE_OPERATION operation, Object[] args) {
        this.operation = operation;
        this.args = args;
    }

    public enum LE_OPERATION {
        SELECTION,
        MODEL_CHANGE,
        VOID,
        RESTORE_VOID,
        ADD_OBJ,
        REMOVE_OBJ,
        MODIFY_STRUCTURE_START(true, false),
        MODIFY_STRUCTURE_END(false, true),

        MOVE_OBJ;

        LE_OPERATION() {
        }

        LE_OPERATION(boolean bulkStart, boolean bulkEnd) {
            this.bulkStart = bulkStart;
            this.bulkEnd = bulkEnd;
        }

        boolean bulkStart;
        boolean bulkEnd;

    }
}
