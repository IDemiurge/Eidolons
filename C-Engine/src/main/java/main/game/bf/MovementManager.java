package main.game.bf;

import main.data.ability.construct.VarHolder;
import main.data.ability.construct.VariableManager;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.bf.Coordinates.UNIT_DIRECTION;
import main.game.bf.pathing.Path;
import main.game.bf.pathing.PathingManager;

import java.util.List;
import java.util.Set;

public interface MovementManager {

    boolean move(Obj obj, Coordinates c);

    boolean canMove(Obj obj, Obj cell);

    boolean noObstacles(Coordinates objCoordinates, Coordinates cellCoordinates);

    BattleFieldGrid getGrid();

    void setGrid(BattleFieldGrid grid);

    boolean isAdjacent(Obj obj1, Obj obj2);

    int getDistance(Obj obj1, Obj obj2);

    SwingBattleField getBf();

    Obj getCell(Coordinates c1);

    List<Obj> getAdjacentEnemies(Obj unit);

    List<Obj> getAdjacentObjs(Obj unit, boolean cell);

    List<Obj> getAdjacentObjs(Coordinates coordinates, boolean cell);

    boolean isAdjacent(Coordinates c1, Coordinates c2);

    boolean move(Obj obj, Coordinates c, boolean free, Path path);

    int getIntegerCost(double cost);

    Set<Obj> getCellsInRadius(Obj targetUnit, int i);

    PathingManager getPathingManager();

    Path getPath(Obj unit, Obj cell);

    boolean move(Obj obj, Coordinates c, boolean free, MOVE_MODIFIER mod, Ref ref);

    Coordinates getTemplateMoveCoordinate(MOVE_TEMPLATES template, FACING_DIRECTION facing,
                                          Obj obj, Ref ref);

    void moveTo(Obj objClicked);

    void promptContinuePath(Obj activeUnit);

    enum MOVE_MODIFIER {
        NONE, FLYING, AGILE, TELEPORT, DISPLACEMENT,
        // trample, jump over

    }

    // VAR TYPE!
    enum MOVE_TEMPLATES implements VarHolder {
        ANY(true, "Range", VariableManager.NUMBER_VAR_CLASS),
        FORWARD_1(false, UNIT_DIRECTION.AHEAD),
        BACKWARD_1(false, UNIT_DIRECTION.BACKWARDS),
        FORWARD(true, "Range", UNIT_DIRECTION.AHEAD, VariableManager.NUMBER_VAR_CLASS),
        // SIDEWAYS_1(true ),

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
        private Boolean selectiveTargeting; // null
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

        public void setVarClasses(Object[] varClasses) {
            this.varClasses = varClasses;
        }

        @Override
        public String getVariableNames() {
            return varNames;
        }

        public String getVarNames() {
            return varNames;
        }

        public void setVarNames(String varNames) {
            this.varNames = varNames;
        }

        public Boolean isSelectiveTargeting() {
            return selectiveTargeting;
        }

        public UNIT_DIRECTION getDirection() {
            return direction;
        }

        public void setDirection(UNIT_DIRECTION direction) {
            this.direction = direction;
        }

        public String getRange() {
            return range;
        }

        public void setRange(String range) {
            this.range = range;
        }

        public void setSelectiveTargeting(Boolean selectiveTargeting) {
            this.selectiveTargeting = selectiveTargeting;
        }

    }

}
