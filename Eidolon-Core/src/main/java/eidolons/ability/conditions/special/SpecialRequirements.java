package eidolons.ability.conditions.special;

import eidolons.entity.unit.UnitModel;
import main.content.CONTENT_CONSTS.SPECIAL_REQUIREMENTS;
import main.elements.conditions.ConditionImpl;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;

public class SpecialRequirements extends ConditionImpl {
    private SPECIAL_REQUIREMENTS template;

    public SpecialRequirements(SPECIAL_REQUIREMENTS template) {
        this.template = template;
    }

    public static boolean check(String req, Ref ref) {
        SPECIAL_REQUIREMENTS template = new EnumMaster<SPECIAL_REQUIREMENTS>()
         .retrieveEnumConst(SPECIAL_REQUIREMENTS.class, req);
        return new SpecialRequirements(template).preCheck(ref);
    }

    @Override
    public boolean check(Ref ref) {
        Coordinates c;
        UnitModel unit;
        switch (template) {
            default:
                break;

        }
        return false;
    }

//    private boolean checkCellPassible(int rotation, int length) {
//        DC_UnitModel unit = (DC_UnitModel) ref.getSourceObj();
//        DIRECTION direction = unit.getDirection();
//        if (rotation != 0) {
//            direction = DirectionMaster.getDirectionByDegree(direction
//                    .getDegrees() + rotation);
//        }
//        Coordinates c = unit.getCoordinates().getAdjacentCoordinate(direction);
//        return game.getMovementManager().getPathingManager()
//                .isGroundPassable(unit, c);
//    }

}
