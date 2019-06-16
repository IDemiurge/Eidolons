package eidolons.ability.targeting;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import main.elements.targeting.TargetingImpl;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.group.GroupImpl;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.bf.directions.UNIT_DIRECTION;

import java.util.ArrayList;
import java.util.Set;

public class CoordinateTargeting extends TargetingImpl {
    private static final KEYS DEFAULT_KEY = KEYS.SOURCE;
    private UNIT_DIRECTION unitDirection;
    private DIRECTION direction;
    private String key;

    public CoordinateTargeting(DIRECTION d) {
        this.direction = (d);
    }

    public CoordinateTargeting() {
        this(KEYS.SOURCE.toString(), null);
    }

    public CoordinateTargeting(String key, UNIT_DIRECTION d) {
        this.unitDirection = (d);
        this.key = (key);
    }

    public CoordinateTargeting(UNIT_DIRECTION d) {
        this(DEFAULT_KEY.toString(), d);
    }

    public boolean select(Ref ref) {
        DC_Obj obj = (DC_Obj) ref.getObj(key);
        Coordinates coordinate = obj.getCoordinates();
        DIRECTION used_direction = direction;
        if (unitDirection != null) {
            Unit unit = (Unit) obj;
            used_direction = DirectionMaster.getDirectionByFacing(unit.getFacing(), unitDirection);
        }
        if (used_direction != null)
            coordinate = coordinate.getAdjacentCoordinate(used_direction);

        Set<BattleFieldObject> objects = obj.getGame().getMaster().
                getObjectsOnCoordinate(coordinate, false);
        if (objects.size() == 0) {
            ref.setTarget(obj.getGame().getCellByCoordinate(coordinate).getId());
        } else if (objects.size() == 1) {
            ref.setTarget(objects.iterator().next().getId());
        } else {
            ref.setGroup(new GroupImpl(new ArrayList<>(objects)));
        }
        return true;
    }
}
