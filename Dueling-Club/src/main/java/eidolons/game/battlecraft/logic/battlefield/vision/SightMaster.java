package eidolons.game.battlecraft.logic.battlefield.vision;

import eidolons.ability.conditions.special.ClearShotCondition;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.game.netherflame.igg.death.ShadowMaster;
import main.content.enums.entity.UnitEnums;
import main.content.enums.rules.VisionEnums.UNIT_VISION;
import main.content.enums.rules.VisionEnums.VISION_MODE;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.DirectionMaster;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.datatypes.DequeImpl;
import main.system.math.MathMaster;
import main.system.math.PositionMaster;

import java.util.*;

/**
 * Created by JustMe on 2/22/2017.
 */
public class SightMaster {
    private VisionMaster master;
    private ClearShotCondition clearShotCondition;
    private Map<DC_Obj, DequeImpl<Coordinates>> cache = new HashMap<>();
    private Map<DC_Obj, DequeImpl<Coordinates>> cacheSecondary = new HashMap<>();

    public DequeImpl<Coordinates> getCachedSpectrumCoordinates(DC_Obj obj) {
        if (!cache.containsKey(obj)) {
            return new DequeImpl<>();
        }
        return cache.get(obj);

    }

    public SightMaster(VisionMaster visionManager) {
        master = visionManager;
    }

    public DequeImpl<Coordinates> getSpectrumCoordinates(Integer range,
                                                         Integer side_penalty,
                                                         Integer back_bonus,
                                                         BattleFieldObject source, boolean vision,
                                                         FACING_DIRECTION facing
    ) {
        return getSpectrumCoordinates(range,
                side_penalty, back_bonus, source, vision,
                facing, false);
    }

    public DequeImpl<Coordinates> getSpectrumCoordinates(Integer range,
                                                         Integer side_penalty,
                                                         Integer back_bonus,
                                                         BattleFieldObject source,
                                                         boolean vision,
                                                         FACING_DIRECTION facing,
                                                         boolean extended) {
        DequeImpl<Coordinates> list = new DequeImpl<>();
        BattleFieldObject unit = source;
        Coordinates orig = source.getCoordinates();
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
            if (!extended)
                back_bonus--;
            //                back_bonus = MathMaster.applyModIfNotZero(back_bonus, source
            //                 .getIntParam(PARAMS.SIGHT_RANGE_EXPANSION_BACKWARD));

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
        if (VisionManager.isCinematicVision()) {
            if (!extended)
                list.addAll(getSpectrumCoordinates(range, 0, back_bonus, source, vision, facing.flip(), true));
        } else {
            Collection<Coordinates> blocked = getBlockedList(list, source, facing);
            list.removeAll(blocked);
        }
        list.add(source.getCoordinates());
        return list;
    }


    // TODO
    private Collection<Coordinates> getBlockedList(DequeImpl<Coordinates> list, BattleFieldObject source,
                                                   FACING_DIRECTION facing) {
        Collection<Coordinates> removeList = new ArrayList<>();
        for (Coordinates c : list) {
            DC_Cell cell = master.getGame().getObjMaster().getCellByCoordinate(c);
            if (cell == null)
                continue;
            Boolean clearShot = !isBlocked(cell, source);
            if (!clearShot) {
                removeList.add(c);
            }
        }
        return removeList;
    }

    private Boolean isBlocked(DC_Obj target, BattleFieldObject source) {
        if (source.getVisionMode() == VISION_MODE.X_RAY_VISION)
        {
            master.getVisionController().getClearshotMapper().set(source, target, false);
            return false;
        }

        Boolean clearShot = master.getVisionController().getClearshotMapper().get(source,
                target);
        if (clearShot != null) {
            return !clearShot;
        }
        Ref ref = new Ref(source);
        ref.setMatch(target.getId());
        clearShot = getClearShotCondition().preCheck(ref);
        master.getVisionController().getClearshotMapper().set(source, target, clearShot);
        return !clearShot;
    }

    public ClearShotCondition getClearShotCondition() {
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

    protected DequeImpl<Coordinates> getVisibleCoordinatesSecondary(BattleFieldObject source) {
        return getVisibleCoordinates(source, true);
    }

    private DequeImpl<Coordinates> getVisibleCoordinates(BattleFieldObject source) {
        return getVisibleCoordinates(source, false);
    }

    private DequeImpl<Coordinates> getVisibleCoordinates(BattleFieldObject source, boolean extended) {
        VISION_MODE mode = source.getVisionMode();
        switch (mode) {
            case INFRARED_VISION:
                break; // see the heat
            case X_RAY_VISION: //will ignore blocks
            case NORMAL_VISION:
                return getVisibleCoordinatesNormalSight(source, extended);
            case TRUE_SIGHT:
                break; // see all
            case WARP_SIGHT:
                break; // see the living
        }
        return null;
    }

    public DequeImpl<Coordinates> getVisibleCoordinatesNormalSight(BattleFieldObject source,
                                                                   boolean extended) {
        return getSpectrumCoordinates(null, null, null, source, true, null, extended);
    }

    // returns direction of the shadowing
    private DIRECTION getShadowingDirection(Unit source, DC_Obj obj) {
        if (obj.isTransparent()) {
            return null;
        }
        // if (objComp.getObj().isTransparent()) return false;
        DIRECTION direction;
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

    private boolean checkDirectHorizontalShadowing(Coordinates orig, Coordinates c) {
        int horizontalDistance = Math.abs(orig.y - c.y);
        return (horizontalDistance == 0);
    }

    private boolean checkDirectVerticalShadowing(Coordinates orig, Coordinates c) {

        int verticalDistance = Math.abs(orig.x - c.x);

        return (verticalDistance == 0);
    }

    public UNIT_VISION getUnitVisibilityStatus(DC_Obj unit, BattleFieldObject activeUnit) {
        clearCacheForUnit(activeUnit);
        return getUnitVisionStatusPrivate(unit, activeUnit);
    }


    protected UNIT_VISION getUnitVisionStatusPrivate(DC_Obj unit, BattleFieldObject activeUnit) {
        if (unit.checkStatus(UnitEnums.STATUS.REVEALED)) {
            unit.removeStatus(UnitEnums.STATUS.REVEALED.toString());
            return UNIT_VISION.IN_PLAIN_SIGHT;
        }

        if (unit.isMine())
            if (activeUnit.isMine())
                return UNIT_VISION.IN_PLAIN_SIGHT;
        if (unit instanceof Unit) {
            if (!((Unit) unit).getAI().isOutsideCombat()) {
                if (activeUnit instanceof Unit) {
                    if (((Unit) activeUnit).isScion()) {
                        return UNIT_VISION.IN_PLAIN_SIGHT;
                    }
                }
            }
        }

        Boolean result = checkInSightSector(activeUnit, unit);
        if (result == null) {
            if (ShadowMaster.getShadowUnit() == activeUnit) {
                if (activeUnit.getCoordinates().dst_(unit.getCoordinates()) <= 5) {
                    return UNIT_VISION.IN_SIGHT;
                }
            }
            if (activeUnit.getVisionMode() != VISION_MODE.X_RAY_VISION)
                if (isBlocked(unit, activeUnit)) {
                    return UNIT_VISION.BLOCKED;
                }
            return UNIT_VISION.BEYOND_SIGHT;
        } else {
            return (result) ? UNIT_VISION.IN_PLAIN_SIGHT : UNIT_VISION.IN_SIGHT;
        }

    }


    protected Boolean checkInSightSector(BattleFieldObject source, DC_Obj target) {
        if (VisionManager.isVisionHacked() && source.isMine()) {
            return true;
        }
        DequeImpl<Coordinates> coordinates = cache.get(source);
        if (coordinates == null || coordinates.isEmpty()) {
            coordinates = getVisibleCoordinates(source);
            cache.put(source, coordinates);
        }
        Boolean result = coordinates.contains(target.getCoordinates());
        if (result) {
//            LogMaster.log(0, target + " is visible: " + coordinates);
        } else {
            coordinates = cacheSecondary.get(source);
            if (coordinates == null || coordinates.isEmpty()) {
                coordinates = getVisibleCoordinatesSecondary(source);
                cacheSecondary.put(source, coordinates);
            }
            result = coordinates.contains(target.getCoordinates());
//            main.system.auxiliary.log.LogMaster.log(1,result+" VISION SECTOR for "
//                    +target.getNameAndCoordinate());
            if (result) {
//                LogMaster.log(0, target + " is half-visible: " + coordinates);
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

    public void resetSightStatuses(BattleFieldObject observer) {

        resetUnitVision(observer, master.getGame().getStructures());
        resetUnitVision(observer, master.getGame().getUnits());
        //        master.getVisionController().getUnitVisionMapper().
        Set<DC_Cell> cells = isFastMode() ? master.getGame().getBattleFieldManager().
                getCellsWithinRange(observer, observer.getMaxVisionDistance()
                ) : master.getGame().getCells();
        resetUnitVision(observer, cells);
    }

    private boolean isFastMode() {
        return true;
    }

    private void resetUnitVision(BattleFieldObject observer, Collection<? extends Obj> units) {
        int maxDistance = observer.getIntParam(PARAMS.SIGHT_RANGE) * 2 + 1;
        for (Obj obj : units) {
            DC_Obj unit = (DC_Obj) obj;
            if (PositionMaster.getDistance(observer, obj) > maxDistance) {
                master.getVisionController().getUnitVisionMapper().set(observer, unit, null);
                continue;
            }
            UNIT_VISION status = getUnitVisionStatusPrivate(unit, observer);
            master.getVisionController().getUnitVisionMapper().set(observer, unit, status);
        }
    }

    public void resetUnitVision(BattleFieldObject observer, DC_Obj object) {
        UNIT_VISION status = null;

        if (observer.isMine())
            status = getUnitVisionStatusPrivate(object, observer);
        else {
            if (ExplorationMaster.isExplorationOn() &&
                    master.getGame().getDungeonMaster().getExplorationMaster().getTimeMaster().isPeriodResetRunning()) {
                status = master.getVisionController().getUnitVisionMapper().get(observer, object);
            } else {
//            status = master.getVisionController().getUnitVisionMapper().getVar(observer, object);
                if (status == null) {
                    //final hack
                    status = getUnitVisionStatusPrivate(object, observer);
                }
            }
        }
        object.setUnitVisionStatus(status, observer);
    }
}
