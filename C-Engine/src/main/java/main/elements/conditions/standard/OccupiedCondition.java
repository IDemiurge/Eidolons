package main.elements.conditions.standard;

import main.content.CONTENT_CONSTS.STANDARD_PASSIVES;
import main.content.OBJ_TYPES;
import main.content.properties.G_PROPS;
import main.elements.conditions.ConditionImpl;
import main.entity.obj.Obj;
import main.game.battlefield.Coordinates;

public class OccupiedCondition extends ConditionImpl {
    protected String obj_ref;
    boolean permitInvisCollision = true;
    boolean permitCollision = true;

    public OccupiedCondition(String obj_ref) {
        this.obj_ref = obj_ref;
    }

    @Override
    public boolean check() {
        Coordinates c = getCoordinates();
        if (c == null) {
            return true;
        }
        boolean result = false;
        if (permitCollision) {
            result = !game.getMovementManager().getPathingManager().isGroundPassable(
                    ref.getSourceObj(), c);
        } else {
            result = game.getMovementManager().getPathingManager().isOccupied(c);
        }
        // ObjComponent objComponent =
        // game.getMovementManager().getPathingManager().getGrid()
        // .getObjCompMap().get(c);
        if (result) {
            for (Obj obj : game.getObjectsOnCoordinate(c)) {
                if (game.getVisionManager().checkInvisible(obj)) {
                    if (permitInvisCollision) {
                        result = false;
                        continue;
                    }
                }
                try {
                    if (ref.getSourceObj().checkProperty(G_PROPS.STANDARD_PASSIVES,
                            "" + STANDARD_PASSIVES.FLYING)) {
                        if (obj.getOBJ_TYPE_ENUM() == OBJ_TYPES.BF_OBJ) {
                            if (obj.checkProperty(G_PROPS.STANDARD_PASSIVES, ""
                                    + STANDARD_PASSIVES.TALL)) {
                                result = false;
                            }
                            continue;
                        }
                    }
                } catch (Exception e) {

                }

                return true;

            }
        }
        return result;
    }

    protected Coordinates getCoordinates() {
        return game.getObjectById(ref.getId(obj_ref)).getCoordinates();
    }

}
