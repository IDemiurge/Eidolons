package main.elements.conditions.standard;

import main.content.DC_TYPE;
import main.content.enums.entity.UnitEnums;
import main.content.values.properties.G_PROPS;
import main.elements.conditions.ConditionImpl;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;

public class OccupiedCondition extends ConditionImpl {
    protected String obj_ref;
    boolean permitInvisCollision = true;
    boolean permitCollision = true;

    public OccupiedCondition(String obj_ref) {
        this.obj_ref = obj_ref;
    }

    @Override
    public boolean check(Ref ref) {
        Coordinates c = getCoordinates(ref);
        if (c == null) {
            return true;
        }
        boolean result;
        if (permitCollision) {
            result =
            !game.getMovementManager().getPathingManager().isGroundPassable(
                    ref.getSourceObj(), c);
        } else {
            result = game.getMovementManager().getPathingManager().isOccupied(c);
        }
        // ObjComponent objComponent =
        // game.getMovementManager().getPathingManager().getGrid()
        // .getObjCompMap().get(c);
        if (result) {
            for (Obj obj : game.getObjectsOnCoordinate(c)) {
                if (game.getVisionMaster().checkInvisible(obj)) {
                    if (permitInvisCollision) {
                        result = false;
                        continue;
                    }
                }
                try {
                    if (ref.getSourceObj().checkProperty(G_PROPS.STANDARD_PASSIVES,
                            "" + UnitEnums.STANDARD_PASSIVES.FLYING)) {
                        if (obj.getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ) {
                            if (obj.checkProperty(G_PROPS.STANDARD_PASSIVES, ""
                                    + UnitEnums.STANDARD_PASSIVES.TALL)) {
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

    protected Coordinates getCoordinates(Ref ref) {
        return game.getObjectById(ref.getId(obj_ref)).getCoordinates();
    }

}
