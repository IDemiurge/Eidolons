package main.game.battlecraft.logic.battlefield.vision;

import main.ability.conditions.special.ClearShotCondition;
import main.content.C_OBJ_TYPE;
import main.content.PARAMS;
import main.content.enums.entity.BfObjEnums;
import main.content.enums.entity.UnitEnums;
import main.content.enums.rules.VisionEnums;
import main.content.enums.rules.VisionEnums.UNIT_TO_UNIT_VISION;
import main.content.enums.rules.VisionEnums.VISION_MODE;
import main.content.values.properties.G_PROPS;
import main.entity.Ref;
import main.entity.obj.BfObj;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.bf.DirectionMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.math.MathMaster;
import main.system.math.PositionMaster;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by JustMe on 2/22/2017.
 */
public class SightMaster {
    private VisionMaster master;
    private ClearShotCondition clearShotCondition;
    private Map<DC_Obj, DequeImpl<Coordinates>> cache = new HashMap<>();
    private Map<DC_Obj, DequeImpl<Coordinates>> cacheSecondary = new HashMap<>();

    public SightMaster(VisionMaster visionManager) {
        master = visionManager;
    }

    public DequeImpl<Coordinates> getSpectrumCoordinates(Integer range,
                                                         Integer side_penalty, Integer back_bonus, DC_Obj source, boolean vision,
                                                         FACING_DIRECTION facing
    ) {
        return getSpectrumCoordinates(range,
                side_penalty, back_bonus, source, vision,
                facing, false);
    }

    public DequeImpl<Coordinates> getSpectrumCoordinates(Integer range,
                                                         Integer side_penalty, Integer back_bonus, DC_Obj source, boolean vision,
                                                         FACING_DIRECTION facing,
                                                         boolean extended) {
        DequeImpl<Coordinates> list = new DequeImpl<>();
        Unit unit = null;
        Coordinates orig = source.getCoordinates();
        if (source instanceof Unit) {
            unit = (Unit) source;
        }
        if (facing == null) {
            if (unit != null) {
                facing = unit.getFacing();
            }
        }
        DIRECTION direction;
        if (facing == null) {
            facing = FacingMaster.getRandomFacing();
        }
        direction = facing.getDirection();

        if (range == null) {
            range = source.getIntParam(PARAMS.SIGHT_RANGE);
            if (extended) {
                range = MathMaster.applyModIfNotZero(range, source
                        .getIntParam(PARAMS.SIGHT_RANGE_EXPANSION));
            }
        }
        if (side_penalty == null) {
            side_penalty = source.getIntParam(PARAMS.SIDE_SIGHT_PENALTY);
            if (extended) {
                side_penalty = MathMaster.applyModIfNotZero(side_penalty, source
                        .getIntParam(PARAMS.SIGHT_RANGE_EXPANSION_SIDES));
            }
        }
        if (back_bonus == null) {
            back_bonus = source.getIntParam(PARAMS.BEHIND_SIGHT_BONUS);
            if (extended) {
                back_bonus = MathMaster.applyModIfNotZero(back_bonus, source
                        .getIntParam(PARAMS.SIGHT_RANGE_EXPANSION_BACKWARD));
            }
        }

        addLine(orig.getAdjacentCoordinate(direction), range, list, direction, true);
        addSides(list, orig, direction, range - side_penalty, false);
        DIRECTION backDirection = DirectionMaster.flip(direction);
        Coordinates backCoordinate = orig.getAdjacentCoordinate(backDirection);
        if (back_bonus > 0) {
            if (backCoordinate != null) {
                addLine(backCoordinate, back_bonus, list, backDirection, true);
                // if (back_bonus > side_penalty)
                // addSides(list, backCoordinate, backDirection, back_bonus -
                // side_penalty, false);
            }
        }

        Chronos.mark("Clear shot preCheck for " + source.getNameAndCoordinate());
        removeShadowed(list, source, facing);
        Chronos.logTimeElapsedForMark("Clear shot preCheck for " + source.getNameAndCoordinate());
        if (vision) {
            // removeConcealed(list, unit, facing);
            // addIlluminated(list, unit, facing);
            addSpecial(list, unit, facing);
            unit.setSightSpectrumCoordinates(list, extended);
        }
        list.add(source.getCoordinates());
        return list;
    }

    private void addSpecial(DequeImpl<Coordinates> list, Unit source,
                            FACING_DIRECTION facing) {
        for (Obj obj : source.getGame().getObjects(C_OBJ_TYPE.BF)) {
            if (FacingMaster.getSingleFacing(source, (BfObj) obj) == UnitEnums.FACING_SINGLE.IN_FRONT) {
                if (obj.getProperty(G_PROPS.BF_OBJECT_GROUP).equals(BfObjEnums.BF_OBJECT_GROUP.WALL)) {
                    list.add(obj.getCoordinates());
                }
                // if (((DC_HeroObj ) obj).isHuge()

            }
        }
    }

    // TODO
    private void removeShadowed(DequeImpl<Coordinates> list, DC_Obj source,
                                FACING_DIRECTION facing) {
        // if (source.isFlying())
        // return;
        Collection<Coordinates> removeList = new LinkedList<>();
        for (Coordinates c : list) {
            Obj obj = source.getGame().getBattleField().getGrid().getObjOrCell(c);
            if (obj != null) {
                Ref ref = new Ref(source);
                ref.setMatch(obj.getId());
                boolean clearShot = getClearShotCondition().preCheck(ref);
                if (!clearShot) {
                    removeList.add(c);
                }
            }
        }
        list.removeAll(removeList);
    }

    private ClearShotCondition getClearShotCondition() {
        if (clearShotCondition == null) {
            clearShotCondition = new ClearShotCondition();
            clearShotCondition.setVision(true);
        }
        return clearShotCondition;
    }

    /**
     * @param c     starting coordinate of the line
     * @param sides whether to add sides (pyramid shape)
     */
    private void addLine(Coordinates c, int range, DequeImpl<Coordinates> list,
                         DIRECTION facing, boolean sides) {
        addLine(c, range, list, facing, sides, false);
    }


    private void addLine(Coordinates c, int range, DequeImpl<Coordinates> list,
                         DIRECTION direction, boolean sides, boolean remove) {
        for (int i = 0; i < range; i++) {
            if (c != null) {
                if (remove) {
                    list.remove(c);
                    LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Coordinate shadowed from vision: "
                            + c);
                } else {
                    list.add(c);
                }
                if (sides) {
                    int side_range = range - 1 - i;
                    if (remove) {
                        side_range = i;
                    }
                    addSides(list, c, direction, side_range, remove);
                }
            } else {
                break;
            }
            c = c.getAdjacentCoordinate(direction);

        }
    }

    private void addSides(DequeImpl<Coordinates> list, Coordinates orig, DIRECTION facing,
                          int range, boolean remove) {
        DIRECTION side = DirectionMaster.rotate90(facing, true);
        addLine(orig.getAdjacentCoordinate(side), range, list, side, false, remove);

        side = DirectionMaster.rotate90(facing, false);
        addLine(orig.getAdjacentCoordinate(side), range, list, side, false, remove);

    }

    public void clearCacheForUnit(DC_Obj obj) {
        cacheSecondary.remove(obj);
        cache.remove(obj);
    }

    private DequeImpl<Coordinates> getVisibleCoordinatesSecondary(Unit source) {
        return getVisibleCoordinates(source, true);
    }

    private DequeImpl<Coordinates> getVisibleCoordinates(Unit source) {
        return getVisibleCoordinates(source, false);
    }

    private DequeImpl<Coordinates> getVisibleCoordinates(Unit source, boolean extended) {
        VISION_MODE mode = source.getVisionMode();
        switch (mode) {
            case INFRARED_VISION:
                break; // see the living
            case NORMAL_VISION:
                return getVisibleCoordinatesNormalSight(source, extended);
            case TRUE_SIGHT:
                break; // see all
            case WARP_SIGHT:
                break; // see magic
            case X_RAY_VISION:
                break; // see right thru
        }
        return null;
    }

    public DequeImpl<Coordinates> getVisibleCoordinatesNormalSight(Unit source,
                                                                   boolean extended) {
        DequeImpl<Coordinates> coordinates = source.getSightSpectrumCoordinates(extended);
        if (!coordinates.isEmpty()) {
            return coordinates;
        }
        return getSpectrumCoordinates(null, null, null, source, true, null, extended);
    }

    // returns direction of the shadowing
    private DIRECTION getShadowingDirection(Unit source, DC_Obj obj) {
        if (obj.isTransparent()) {
            return null;
        }
        // if (objComp.getObj().isTransparent()) return false;
        DIRECTION direction;
        FACING_DIRECTION facing = source.getFacing();
        Coordinates orig = source.getCoordinates();
        Coordinates c = obj.getCoordinates();

        if (checkDirectVerticalShadowing(orig, c)) {

            direction = (!PositionMaster.isAbove(orig, c)) ? DIRECTION.UP : DIRECTION.DOWN;

            return direction;
        }
        if (checkDirectHorizontalShadowing(orig, c)) {
            direction = (!PositionMaster.isToTheLeft(orig, c)) ? DIRECTION.LEFT : DIRECTION.RIGHT;
            return direction;
        }

        // if (facing.isVertical()) {

        // if (facing == FACING_DIRECTION.NORTH)

        if (PositionMaster.isAbove(orig, c)) {
            if (PositionMaster.isToTheLeft(orig, c)) {
                direction = DIRECTION.DOWN_RIGHT;
            } else {
                direction = DIRECTION.DOWN_LEFT;
            }
        } else {

            if (PositionMaster.isToTheLeft(orig, c)) {
                direction = DIRECTION.UP_RIGHT;
            } else {
                direction = DIRECTION.UP_LEFT;
            }
        }

        return direction;

    }

    private boolean checkMinorDirectShadowing(FACING_DIRECTION facing, Coordinates orig,
                                              Coordinates c) {
        int horizontalDistance;
        int verticalDistance;
        if (facing.isVertical()) {
            horizontalDistance = Math.abs(orig.x - c.x);
            verticalDistance = Math.abs(orig.y - c.y);
        } else {
            horizontalDistance = Math.abs(orig.y - c.y);
            verticalDistance = Math.abs(orig.x - c.x);
        }
        return horizontalDistance <= 1 && verticalDistance > 1;
    }

    private boolean checkDirectHorizontalShadowing(Coordinates orig, Coordinates c) {
        int horizontalDistance = Math.abs(orig.y - c.y);
        return (horizontalDistance == 0);
    }

    private boolean checkDirectVerticalShadowing(Coordinates orig, Coordinates c) {

        int verticalDistance = Math.abs(orig.x - c.x);

        return (verticalDistance == 0);
    }

    private void removeLine(Coordinates c, int range, DequeImpl<Coordinates> list,
                            DIRECTION direction, boolean sides) {
        addLine(c, range, list, direction, sides, true);
    }

    public UNIT_TO_UNIT_VISION getUnitVisibilityStatus(DC_Obj unit, Unit activeUnit) {
        clearCacheForUnit(activeUnit);
        return getUnitVisionStatusPrivate(unit, activeUnit);
    }


    protected UNIT_TO_UNIT_VISION getUnitVisionStatusPrivate(DC_Obj unit, Unit activeUnit) {
        UNIT_TO_UNIT_VISION status;

        Boolean result = checkInSightSector(activeUnit, unit);
        if (result == null) {
            status = VisionEnums.UNIT_TO_UNIT_VISION.BEYOND_SIGHT;
        } else {
            status = (result) ? VisionEnums.UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT : VisionEnums.UNIT_TO_UNIT_VISION.IN_SIGHT;
        }

        // if (status == UNIT_TO_UNIT_VISION.IN_SIGHT)
        // if (checkConcealed(activeUnit, unit)) // TODO preCheck visibility
        // obstacles
        // status = UNIT_TO_UNIT_VISION.CONCEALED;

        return status;

    }

    protected Boolean checkInSightSector(Unit source, DC_Obj target) {
        if (VisionManager.isVisionHacked()) {
            return true;
        }
        DequeImpl<Coordinates> coordinates = cache.get(source);
        if (coordinates == null || coordinates.isEmpty()) {
            coordinates = getVisibleCoordinates(source);
            cache.put(source, coordinates);
        }
        Boolean result = coordinates.contains(target.getCoordinates());
        if (result) {
            LogMaster.log(0, target + " is visible: " + coordinates);
        } else {
            coordinates = cacheSecondary.get(source);
            if (coordinates == null || coordinates.isEmpty()) {
                coordinates = getVisibleCoordinatesSecondary(source);
                cacheSecondary.put(source, coordinates);
            }
            result = coordinates.contains(target.getCoordinates());
            if (result) {
                LogMaster.log(0, target + " is half-visible: " + coordinates);
                return false;
            } else {
                return null;
            }
        }
        return result;
    }

    public void clearCaches() {
        cache.clear();
        cacheSecondary.clear();
    }
}
