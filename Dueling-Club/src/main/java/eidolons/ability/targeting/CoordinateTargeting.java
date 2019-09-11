package eidolons.ability.targeting;

import eidolons.ability.effects.oneshot.unit.SummonEffect;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import main.ability.effects.continuous.CustomTargetEffect;
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
    private String facingKey;
    private String coordinateKey;
    boolean flip; //TODO igg demo fix
    boolean useActivesRange;

    public CoordinateTargeting(UNIT_DIRECTION unitDirection, String facingKey, String coordinateKey) {
        this.unitDirection = unitDirection;
        this.facingKey = facingKey;
        this.coordinateKey = coordinateKey;
    }

    public CoordinateTargeting(DIRECTION d) {
        this(DEFAULT_KEY.toString(), null);
        this.direction = d;
    }

    public CoordinateTargeting(DIRECTION d, String key) {
        this(key, null);
        this.direction = d;
    }

    public CoordinateTargeting() {
        this(DEFAULT_KEY.toString(), null);
    }

    public CoordinateTargeting(String key, UNIT_DIRECTION d) {
        this(d, key, key);
    }

    public CoordinateTargeting(UNIT_DIRECTION unitDirection, boolean useActivesRange) {
        this(unitDirection);
        this.useActivesRange = useActivesRange;
    }

    public CoordinateTargeting(UNIT_DIRECTION d) {
        this(DEFAULT_KEY.toString(), d);
    }

    public boolean select(Ref ref) {
        DC_Obj obj = (DC_Obj) ref.getObj(facingKey);
        if (obj == null) {
            obj = (DC_Obj) ref.getSourceObj();
        }
        Coordinates coordinate = (ref.getObj(coordinateKey) == null)
                ? ref.getSourceObj().getCoordinates()
                : ref.getObj(coordinateKey).getCoordinates();

        DIRECTION used_direction = direction;
        if (unitDirection != null) {
            BattleFieldObject unit = (BattleFieldObject) obj;
            used_direction = DirectionMaster.getDirectionByFacing(unit.getFacing(), unitDirection);
            if (flip) {
                used_direction = used_direction.flip();
            }
        }
        if (used_direction != null) {
            if (useActivesRange) {
                if (ref.getActive() instanceof DC_ActiveObj) {
                    int r = ((DC_ActiveObj) ref.getActive()).getRange();
                    for (int i = 0; i < r; i++) {
                        if (coordinate.getAdjacentCoordinate(used_direction) == null) {
                            break;
                        }
                        coordinate = coordinate.getAdjacentCoordinate(used_direction);
                    }
                }
            } else {
                coordinate = coordinate.getAdjacentCoordinate(used_direction);
            }
        }

        if (ref.getEffect() instanceof CustomTargetEffect) {//TODO EA hack - overlaying!
            if (((CustomTargetEffect) ref.getEffect()).getEffect() instanceof SummonEffect) {
                try {
                    ref.setTarget(obj.getGame().getObjectByCoordinate(null, coordinate,
                            false, false, true).getId());
                    return true;
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
        }
        Set<BattleFieldObject> objects = obj.getGame().getMaster().
                getObjectsOnCoordinate(coordinate, true); //TODO EA hack - overlaying!
        if (objects.size() == 0) {
            DC_Cell cell = obj.getGame().getCellByCoordinate(coordinate);
            if (cell == null) {
                return false;
            }
            ref.setTarget(cell.getId());
        } else if (objects.size() == 1) {
            ref.setTarget(objects.iterator().next().getId());
        } else {
            ref.setGroup(new GroupImpl(new ArrayList<>(objects)));
        }
        return true;
    }
}
