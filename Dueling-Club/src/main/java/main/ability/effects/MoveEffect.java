package main.ability.effects;

import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.unit.Unit;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.UNIT_DIRECTION;
import main.game.battlefield.DirectionMaster;
import main.game.battlefield.MovementManager.MOVE_MODIFIER;
import main.system.math.Formula;

public class MoveEffect extends DC_Effect {

    protected boolean movement;
    protected boolean free;
    protected String obj_to_move;
    protected Formula x_displacement;
    protected Formula y_displacement;
    protected Coordinates origin;
    protected Coordinates destination;
    private UNIT_DIRECTION direction;
    private String relativeDirection;
    private String targetKey = KEYS.TARGET.toString();

    public MoveEffect() {
        movement = true;
        free = false;
        this.obj_to_move = Ref.KEYS.SOURCE.name();
    }

    // public MoveEffect(String obj_to_move, String target, Boolean
    // relativeDirection) {
    //
    // }

    public MoveEffect(KEYS obj_to_move, UNIT_DIRECTION d) {
        this(obj_to_move);
        this.direction = d;

    }

    public MoveEffect(KEYS obj_to_move) {
        this(obj_to_move.toString());
    }

    public MoveEffect(KEYS obj_to_move, KEYS target) {
        this(obj_to_move.toString(), target.toString());
    }

    public MoveEffect(String obj_to_move) {
        this(obj_to_move, KEYS.TARGET.toString());
    }

    public MoveEffect(String obj_to_move, String targetKey) {
        movement = true;
        free = true;
        this.obj_to_move = obj_to_move;
        this.targetKey = targetKey;
    }

    public MoveEffect(String obj_to_move, Formula x_displacement,
                      Formula y_displacement) {
        this.x_displacement = x_displacement;
        this.y_displacement = y_displacement;
        this.obj_to_move = obj_to_move;
        movement = false;
        free = true;
    }

    @Override
    public boolean applyThis() {
        Unit obj = (Unit) ref.getObj(obj_to_move);
        origin = obj.getCoordinates();
        if (direction != null) {
            destination = obj.getCoordinates().getAdjacentCoordinate(
                    DirectionMaster.getDirectionByFacing(obj.getFacing(),
                            direction));
            game.getMovementManager().move(obj, destination, free,
                    MOVE_MODIFIER.DISPLACEMENT, ref);
            return true;
        }
        destination = getCoordinates();

        game.getMovementManager().move(obj, destination, free,
                MOVE_MODIFIER.DISPLACEMENT, ref);

        //
        // FacingManager.

        Coordinates.FACING_DIRECTION facing = obj.getFacing();
//         direction = DirectionMaster.getDirectionByFacing(facing, d);

        return true;
    }

    public Coordinates getCoordinates() {
        Coordinates c;
        if (movement) {
            c = ref.getObj(targetKey).getCoordinates();
        } else {
            Integer x = x_displacement.getInt(ref);
            Integer y = y_displacement.getInt(ref);
            // TODO direction?!
            c = new Coordinates(ref.getObj(obj_to_move).getCoordinates().getX() + x, ref.getObj(obj_to_move)
                    .getCoordinates().getY() + y);
        }
        return c;
    }

    public Coordinates getDestination() {
        if (destination == null) {
            return getCoordinates();
        }
        return destination;
    }

    public Coordinates getOrigin() {
        return origin;
    }
}
