package main.elements.conditions.standard;

import main.elements.conditions.ConditionImpl;
import main.entity.Ref;
import main.game.bf.Coordinates;

public class OccupiedCondition extends ConditionImpl {
    protected String obj_ref;

    public OccupiedCondition(String obj_ref) {
        this.obj_ref = obj_ref;
    }

    @Override
    public boolean check(Ref ref) {
        Coordinates c = getCoordinates(ref);
        if (c == null) {
            return true;
        }
        boolean result = !game.getMovementManager().canMove(
         ref.getSourceObj(), c);
        return  result;
//        if (result) {
//            for (Obj obj : game.getObjectsOnCoordinate(c)) {
//                if (game.getVisionMaster().checkInvisible(obj)) {
//                    result = false;
//                    continue;
//                }
//                    if (ref.getSourceObj().checkProperty(G_PROPS.STANDARD_PASSIVES,
//                     "" + UnitEnums.STANDARD_PASSIVES.FLYING)) {
//                        if (obj.getOBJ_TYPE_ENUM() == DC_TYPE.BF_OBJ) {
//                            if (obj.checkProperty(G_PROPS.STANDARD_PASSIVES, ""
//                             + UnitEnums.STANDARD_PASSIVES.TALL)) {
//                                result = false;
//                            }
//                            continue;
//                        }
//                    }
//
//                return true;
//
//            }
//        }
//        return result;
    }

    protected Coordinates getCoordinates(Ref ref) {
        return game.getObjectById(ref.getId(obj_ref)).getCoordinates();
    }

}
