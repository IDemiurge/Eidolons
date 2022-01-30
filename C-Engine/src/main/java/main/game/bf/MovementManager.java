package main.game.bf;

import main.data.ability.construct.VarHolder;
import main.data.ability.construct.VariableManager;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.bf.directions.UNIT_DIRECTION;

public interface MovementManager {

    boolean move(Obj obj, Coordinates c);

    boolean canMove(Entity obj, Coordinates c);

    BattleFieldGrid getGrid();

    int getDistance(Obj obj1, Obj obj2);

    boolean move(Obj obj, Coordinates c, boolean free, MOVE_MODIFIER mod, Ref ref);

    void moveTo(Obj objClicked);

    void promptContinuePath(Obj activeUnit);

    enum MOVE_MODIFIER {
        NONE, FLYING, AGILE, TELEPORT, DISPLACEMENT,

    }

    enum MOVE_TEMPLATES implements VarHolder {

        ANY(true, "Range", VariableManager.NUMBER_VAR_CLASS),
        FORWARD_1(false, UNIT_DIRECTION.AHEAD),
        BACKWARD_1(false, UNIT_DIRECTION.BACKWARDS),
        FORWARD(true, "Range", UNIT_DIRECTION.AHEAD, VariableManager.NUMBER_VAR_CLASS),
        CUSTOM(false, "Selective;Range;Direction", VariableManager.NUMBER_VAR_CLASS, UNIT_DIRECTION.class, VariableManager.BOOLEAN_VAR_CLASS),
        SIDEWAYS_2(true, "2"),
        SIDEWAYS_RIGHT_1(false, UNIT_DIRECTION.RIGHT),
        SIDEWAYS_LEFT_1(false, UNIT_DIRECTION.LEFT),
        SIDEWAYS_LEFT(true, "Range", UNIT_DIRECTION.LEFT, VariableManager.NUMBER_VAR_CLASS),
        SIDEWAYS_RIGHT(true, "Range", VariableManager.NUMBER_VAR_CLASS),
        DIAGONAL_ANY(true, "Range", VariableManager.NUMBER_VAR_CLASS),
        DIAGONAL_FORWARD(true, "Range", VariableManager.NUMBER_VAR_CLASS),
        DIAGONAL_BACKWARD(true, "Range", VariableManager.NUMBER_VAR_CLASS),
        DIAGONAL_BACKWARD_LEFT(false, "Range", UNIT_DIRECTION.BACKWARDS_LEFT, VariableManager.NUMBER_VAR_CLASS),
        DIAGONAL_BACKWARD_RIGHT(false, "Range", UNIT_DIRECTION.BACKWARDS_RIGHT, VariableManager.NUMBER_VAR_CLASS),
        DIAGONAL_FORWARD_LEFT(false, "Range", UNIT_DIRECTION.AHEAD_LEFT, VariableManager.NUMBER_VAR_CLASS),
        DIAGONAL_FORWARD_RIGHT(false, "Range", UNIT_DIRECTION.AHEAD_RIGHT, VariableManager.NUMBER_VAR_CLASS),
        KNIGHT(true),;

        String range;
        private String varNames;
        private Object[] varClasses;
        private Boolean selectiveTargeting;
        private UNIT_DIRECTION direction;

        // point to spec req?
        MOVE_TEMPLATES(boolean selectiveTargeting, String varNames, UNIT_DIRECTION direction,
                       Object... varClasses) {
            this(selectiveTargeting, varNames, varClasses);
            this.direction = direction;
        }

        MOVE_TEMPLATES(boolean selectiveTargeting, String varNames, Object... varClasses) {
            this.selectiveTargeting = selectiveTargeting;
            this.varNames = varNames;
            this.varClasses = varClasses;
        }

        MOVE_TEMPLATES(boolean selectiveTargeting) {
            this.range = "1";
        }

        MOVE_TEMPLATES(boolean selectiveTargeting, UNIT_DIRECTION direction) {
            this.direction = direction;
            this.range = "1";
        }

        MOVE_TEMPLATES(boolean selectiveTargeting, String range) {
            this.range = range;
        }

        @Override
        public Object[] getVarClasses() {
            return varClasses;
        }

        @Override
        public String getVariableNames() {
            return varNames;
        }

        public Boolean isSelectiveTargeting() {
            return selectiveTargeting;
        }

        public UNIT_DIRECTION getDirection() {
            return direction;
        }

        public String getRange() {
            return range;
        }




    }

}
