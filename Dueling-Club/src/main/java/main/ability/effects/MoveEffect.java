package main.ability.effects;

import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.MicroObj;
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
        DC_HeroObj obj = (DC_HeroObj) ref.getObj(obj_to_move);
        if (direction != null) {
            Coordinates c = obj.getCoordinates().getAdjacentCoordinate(
                    DirectionMaster.getDirectionByFacing(obj.getFacing(),
                            direction));
            game.getMovementManager().move(obj, c, free,
                    MOVE_MODIFIER.DISPLACEMENT, ref);
            return true;
        }
        Coordinates c = null;
        if (movement) {
            c = ((MicroObj) ref.getObj(targetKey)).getCoordinates();
        } else {
            Integer x = x_displacement.getInt(ref);
            Integer y = y_displacement.getInt(ref);
            // TODO direction?!
            c = new Coordinates(obj.getCoordinates().getX() + x, obj
                    .getCoordinates().getY() + y);
        }
        game.getMovementManager().move(obj, c, free,
                MOVE_MODIFIER.DISPLACEMENT, ref);

        //
        // FacingManager.

         Coordinates.FACING_DIRECTION facing = obj.getFacing();
//         direction = DirectionMaster.getDirectionByFacing(facing, d);

        return true;
    }
}
