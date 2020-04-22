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
        VOID_TOGGLE,
        ADD_OBJ,
        REMOVE_OBJ,
        MODIFY_STRUCTURE_START(true, false),
        MODIFY_STRUCTURE_END(false, true),

        MOVE_OBJ,

        CLEAR_START(true, false),
        CLEAR_END(false, true),

        FILL_START(true, false),
        FILL_END(false, true),

        PASTE_START(true, false),
        PASTE_END(false, true),

        INSERT_START(true, false),
        INSERT_END(false, true),


        EDIT,
        ADD_BLOCK,
        REMOVE_BLOCK,
        REMOVE_OVERLAY,
        ADD_OVERLAY, CELL_SCRIPT_CHANGE, MODIFY_STRUCTURE, SAVE_STRUCTURE,
        MASS_SET_VOID, MASS_RESET_VOID, MODIFY_ENTITY, SAVE_ENTITY_DATA, MODIFY_DATA, SAVE_DATA;

        LE_OPERATION() {
        }

        LE_OPERATION(boolean bulkStart, boolean bulkEnd) {
            this.bulkStart = bulkStart;
            this.bulkEnd = bulkEnd;
        }

        boolean bulkStart;
        boolean bulkEnd;

    }

    public LE_OPERATION getOperation() {
        return operation;
    }

    public Object[] getArgs() {
        return args;
    }
}
