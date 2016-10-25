package main.ability.conditions.special;

import main.content.CONTENT_CONSTS.SPECIAL_REQUIREMENTS;
import main.elements.conditions.ConditionImpl;
import main.entity.Ref;
import main.entity.obj.DC_UnitObj;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.DirectionMaster;
import main.system.auxiliary.EnumMaster;

public class SpecialRequirements extends ConditionImpl {
    private SPECIAL_REQUIREMENTS template;

    public SpecialRequirements(SPECIAL_REQUIREMENTS template) {
        this.template = template;
    }

    public static boolean check(String req, Ref ref) {
        SPECIAL_REQUIREMENTS template = new EnumMaster<SPECIAL_REQUIREMENTS>()
                .retrieveEnumConst(SPECIAL_REQUIREMENTS.class, req);
        return new SpecialRequirements(template).check(ref);
    }

    @Override
    public boolean check() {
        Coordinates c;
        DC_UnitObj unit;
        switch (template) {
            case FREE_CELL:
                break;
            case FREE_CELL_RANGE:
                break;
            case HAS_ITEM:
                break;
            case ITEM:
                break;
            case NOT_FREE_CELL:
                break;
            case NOT_ITEM:
                break;
            default:
                break;

        }
        return false;
    }

    private boolean checkCellPassible(int rotation, int length) {
        DC_UnitObj unit = (DC_UnitObj) ref.getSourceObj();
        DIRECTION direction = unit.getDirection();
        if (rotation != 0) {
            direction = DirectionMaster.getDirectionByDegree(direction
                    .getDegrees() + rotation);
        }
        Coordinates c = unit.getCoordinates().getAdjacentCoordinate(direction);
        return game.getMovementManager().getPathingManager()
                .isGroundPassable(unit, c);
    }

}
