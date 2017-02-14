package main.game.battlefield;

import main.ability.conditions.special.ClearShotCondition;
import main.content.CONTENT_CONSTS.*;
import main.content.C_OBJ_TYPE;
import main.content.PARAMS;
import main.content.properties.G_PROPS;
import main.entity.Ref;
import main.entity.obj.*;
import main.entity.obj.unit.DC_HeroObj;
import main.game.DC_Game;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.logic.battle.player.DC_Player;
import main.game.logic.dungeon.Entrance;
import main.game.player.Player;
import main.rules.action.StealthRule;
import main.rules.mechanics.ConcealmentRule;
import main.rules.mechanics.ConcealmentRule.VISIBILITY_LEVEL;
import main.swing.components.obj.drawing.VisibilityMaster;
import main.system.auxiliary.log.Chronos;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.math.MathMaster;
import main.system.math.PositionMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import main.test.debug.DebugMaster;

import java.util.*;

public class VisionManager implements GenericVisionManager {

    private static ClearShotCondition clearShotCondition;
    private static DC_HeroObj unit;
    private static boolean visionHacked;
    private DC_Game game;
    private Map<DC_Obj, DequeImpl<Coordinates>> cache = new HashMap<>();
    private Map<DC_Obj, DequeImpl<Coordinates>> cacheSecondary = new HashMap<>();
    private DC_HeroObj activeUnit;
    private boolean fastMode;

    public VisionManager(DC_Game game) {
        this.game = game;
    }

    public static boolean isVisionHacked() {
        return visionHacked;
    }

    public static void setVisionHacked(boolean visionHacked) {
        VisionManager.visionHacked = visionHacked;
    }

    public static DequeImpl<Coordinates> getSpectrumCoordinates(Integer range,
                                                                Integer side_penalty, Integer back_bonus, DC_Obj source, boolean vision,
                                                                FACING_DIRECTION facing) {
        return getSpectrumCoordinates(range, side_penalty, back_bonus, source, vision, facing,
                false);
    }

    public static DequeImpl<Coordinates> getSpectrumCoordinates(Integer range,
                                                                Integer side_penalty, Integer back_bonus, DC_Obj source, boolean vision,
                                                                FACING_DIRECTION facing,
                                                                boolean extended) {
        DequeImpl<Coordinates> list = new DequeImpl<>();
        DC_HeroObj unit = null;
        Coordinates orig = source.getCoordinates();
        if (source instanceof DC_HeroObj) {
            unit = (DC_HeroObj) source;
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

        Chronos.mark("Clear shot check for " + source.getNameAndCoordinate());
        removeShadowed(list, source, facing);
        Chronos.logTimeElapsedForMark("Clear shot check for " + source.getNameAndCoordinate());
        if (vision) {
            // removeConcealed(list, unit, facing);
            // addIlluminated(list, unit, facing);
            addSpecial(list, unit, facing);
            unit.setSightSpectrumCoordinates(list, extended);
        }
        list.add(source.getCoordinates());
        return list;
    }

    private static void addSpecial(DequeImpl<Coordinates> list, DC_HeroObj source,
                                   FACING_DIRECTION facing) {
        for (Obj obj : source.getGame().getObjects(C_OBJ_TYPE.BF)) {
            if (FacingMaster.getSingleFacing(source, (BattlefieldObj) obj) == FACING_SINGLE.IN_FRONT) {
                if (obj.getProperty(G_PROPS.BF_OBJECT_GROUP).equals(BF_OBJECT_GROUP.WALL)) {
                    list.add(obj.getCoordinates());
                }
                // if (((DC_HeroObj ) obj).isHuge()

            }
        }
    }

    // TODO
    private static void removeShadowed(DequeImpl<Coordinates> list, DC_Obj source,
                                       FACING_DIRECTION facing) {
        // if (source.isFlying())
        // return;
        Collection<Coordinates> removeList = new LinkedList<>();
        for (Coordinates c : list) {
            Obj obj = source.getGame().getBattleField().getGrid().getObjOrCell(c);
            if (obj != null) {
                Ref ref = new Ref(source);
                ref.setMatch(obj.getId());
                boolean clearShot = getClearShotCondition().check(ref);
                if (!clearShot) {
                    removeList.add(c);
                }
            }
        }
        list.removeAll(removeList);
    }

    private static ClearShotCondition getClearShotCondition() {
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
    private static void addLine(Coordinates c, int range, DequeImpl<Coordinates> list,
                                DIRECTION facing, boolean sides) {
        addLine(c, range, list, facing, sides, false);
    }

    // @Deprecated
    // public static boolean checkConcealed(DC_Obj source, DC_Obj target) {
    // int range = source.getIntParam(PARAMS.SIGHT_RANGE);
    // // TODO detection? illumination check?
    // Integer concealment = 0;
    // // target.getIntParam(PARAMS.STEALTH)
    // // - source.getIntParam(PARAMS.DETECTION);
    // if (!source.checkPassive(STANDARD_PASSIVES.DARKVISION))
    // concealment += target.getIntParam(PARAMS.CONCEALMENT);
    // range = range - (range * concealment / 100);
    // // range -= concealment;
    // return (range < PositionMaster.getDistance(source, target));
    // }

    private static void addLine(Coordinates c, int range, DequeImpl<Coordinates> list,
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

    private static void addSides(DequeImpl<Coordinates> list, Coordinates orig, DIRECTION facing,
                                 int range, boolean remove) {
        DIRECTION side = DirectionMaster.rotate90(facing, true);
        addLine(orig.getAdjacentCoordinate(side), range, list, side, false, remove);

        side = DirectionMaster.rotate90(facing, false);
        addLine(orig.getAdjacentCoordinate(side), range, list, side, false, remove);

    }

    public static boolean checkConcealed(DC_Obj unit) {
        return false;
    }

    public static boolean checkDetectedEnemy(DC_Obj obj) {
        return checkDetected(obj, true);
    }

    public static boolean checkDetected(DC_Obj obj) {
        return checkDetected(obj, false);
    }

    public static boolean checkDetectedForPlayer(DC_Obj obj) {

        return obj.getPlayerVisionStatus(false) == UNIT_TO_PLAYER_VISION.DETECTED;
    }

    public static boolean checkDetected(DC_Obj obj, boolean enemy) {
        if (obj == null) {
            return false;
        }
        if (!enemy || obj.getOwner().isMe()) {
            if (unit != null) {
                if (obj.getOwner() == unit.getOwner()) {
                    return true;
                }
            }
        }
        return obj.getPlayerVisionStatus(!enemy) == UNIT_TO_PLAYER_VISION.DETECTED;
    }

    public static boolean checkVisible(DC_Obj obj) {
        boolean visible = checkVisible(obj, false);
        if (!visible) {
            obj.setDetected(false);
        }
        return visible;
    }

    public static boolean checkVisible(DC_Obj obj, boolean active) {
        if (unit == null) {
            return true;
        }
        if (unit.getZ() != obj.getZ() && !(obj instanceof Entrance)) // dungeon
            // sublevel
        {
            return false;
        }
        if (unit.getOwner().isMe()) {
            if (obj.getOwner().isMe()) {
                return true;
            }
        }
        // if (obj.getPerceptionStatus(active) ==
        // PERCEPTION_STATUS.KNOWN_TO_BE_THERE)
        // return true;

        if (VisibilityMaster.isZeroVisibility(obj, active)) {
            if (obj.getPlayerVisionStatus(active) == UNIT_TO_PLAYER_VISION.UNKNOWN) {
                return false;
            }
        }

        if (obj.getPlayerVisionStatus(active) == UNIT_TO_PLAYER_VISION.INVISIBLE
                || obj.getPlayerVisionStatus(active) == UNIT_TO_PLAYER_VISION.INVISIBLE_ALLY) {
            return false;
        }

        // if (obj.getVisibilityLevel()==VISIBILITY_LEVEL.CLEAR)
        // return true; // ILLUMINATION SHOULD ADD TO SIGHTED-COORDINATES
        // VISIBILITY TODO
        return true;

    }

    public static boolean checkKnown(DC_Obj obj) {
        if (obj instanceof AttachedObj) {
            obj = (DC_Obj) ((AttachedObj) obj).getOwnerObj();
            if (obj == null) {
                return false;
            }
        }
        if (checkDetected(obj)) {
            return true;
        }

        if (unit != null) {
            if (obj.getOwner() == unit.getOwner()) {
                return true;
            }
        }

        return (obj.getActivePlayerVisionStatus() == UNIT_TO_PLAYER_VISION.KNOWN);
    }

    public void resetVisibilityStatuses() {
        ConcealmentRule.clearCache();
        WaitMaster.waitForInput(WAIT_OPERATIONS.GUI_READY);
        activeUnit = (DC_HeroObj) game.getTurnManager().getActiveUnit(true);

        if (activeUnit == null) {
            LogMaster.log(0, "null active unit for visibility!");
            return;
        }
        boolean mine = activeUnit.getOwner().isMe();

        unit = activeUnit;
        activeUnit.setPlainSightSpectrumCoordinates(new DequeImpl<>());
        activeUnit.setSightSpectrumCoordinates(new DequeImpl<>());

        Chronos.mark("UNIT VISIBILITY REFRESH");

        LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Resetting " + activeUnit
                + "'s visibility for neutral units...");
        try {
            setRelativeActiveUnitVisibility(activeUnit, Player.NEUTRAL.getControlledUnits());
        } catch (Exception e) {

        }
        LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Resetting " + activeUnit
                + "'s visibility for enemy units...");
        setRelativeActiveUnitVisibility(activeUnit, game.getPlayer(!mine).getControlledUnits());

        LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Resetting " + activeUnit
                + "'s visibility for terrain cells...");
        Set<Obj> cells = isFastMode() ? game.getBattleField().getGrid()
                .getCellsWithinVisionBounds() : game.getCells();
        setRelativeActiveUnitVisibility(activeUnit, cells);

        Chronos.logTimeElapsedForMark("UNIT VISIBILITY REFRESH");

        Chronos.mark("PLAYER VISIBILITY REFRESH");

        LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Resetting " + activeUnit
                + "'s visibility for allied units...");
        setAlliedVisibility(game.getPlayer(mine).getControlledUnits());

        VisibilityMaster.resetVisibilityLevels();

        Chronos.mark("PLAYER for terrain cells VISIBILITY REFRESH");
        LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Resetting player detection for terain cells...");
        setRelativePlayerVisibility(game.getPlayer(mine), cells);
        Chronos.logTimeElapsedForMark("PLAYER for terain cells VISIBILITY REFRESH");

        LogMaster
                .log(LogMaster.VISIBILITY_DEBUG, "Resetting player detection for neutral units...");
        setRelativePlayerVisibility(game.getPlayer(mine), Player.NEUTRAL.getControlledUnits());

        LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Resetting player detection for enemy units...");
        setRelativePlayerVisibility(game.getPlayer(mine), game.getPlayer(!mine)
                .getControlledUnits());

        // setVisibilityLevel(game.getPlayer(!mine).getControlledUnits());
        // setVisibilityLevel(game.getPlayer(mine).getControlledUnits());
        // setVisibilityLevel(Player.NEUTRAL.getControlledUnits());
        // setVisibilityLevel(cells);

        activeUnit.setUnitVisionStatus(UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT);
        activeUnit.setVisibilityLevel(VISIBILITY_LEVEL.CLEAR_SIGHT);
        resetLastKnownCoordinates();
        Chronos.logTimeElapsedForMark("PLAYER VISIBILITY REFRESH");
    }

    private void resetLastKnownCoordinates() {
        for (DC_HeroObj u : game.getUnits()) {
            if (checkVisible(u)) {
                u.setLastKnownCoordinates(u.getCoordinates());
            }
        }

    }

    private void setVisibilityLevel(Set<Obj> controlledUnits) {
        for (Obj obj : controlledUnits) {
            DC_Obj target = (DC_Obj) obj;
            target.setVisibilityLevel(getUnitVisibilityLevel(target, activeUnit));
        }
    }

    private void setAlliedVisibility(Set<Obj> controlledUnits) {
        for (Obj obj : controlledUnits) {
            DC_Obj unit = (DC_Obj) obj;
            UNIT_TO_UNIT_VISION status = getUnitVisionStatusPrivate(unit, activeUnit);
            // UNIT_TO_PLAYER_VISION detectionStatus =
            // UNIT_TO_PLAYER_VISION.DETECTED;

            if (!activeUnit.isMine()) {
                if (unit.getActivePlayerVisionStatus() == UNIT_TO_PLAYER_VISION.INVISIBLE) {
                    if (status == UNIT_TO_UNIT_VISION.CONCEALED) {
                        status = UNIT_TO_UNIT_VISION.BEYOND_SIGHT;
                    }
                    // detectionStatus = UNIT_TO_PLAYER_VISION.INVISIBLE_ALLY;
                }
            }
            // unit.setPlayerVisionStatus(detectionStatus); //TODO NEW - enemy
            // exclusive
            unit.setUnitVisionStatus(status);
        }
    }

    // private static void addIlluminated(DequeImpl<Coordinates> list,
    // DC_HeroObj source,
    // FACING_DIRECTION facing) {
    // for (Obj obj : source.getGame().getObjects(C_OBJ_TYPE.BF)) {
    // if (IlluminationRule.checkIlluminated(source, obj, facing))
    // // ((DC_Obj) obj).checkStatus(STATUS.ILLUMINATED))
    // {
    // if (FacingMaster.getSingleFacing(source, (BattlefieldObj) obj) ==
    // FACING_SINGLE.IN_FRONT)
    // list.add(obj.getCoordinates());
    // }
    // }
    // }

    private void setRelativeActiveUnitVisibility(DC_HeroObj activeUnit, Set<Obj> units) {
        for (Obj obj : units) {
            DC_Obj unit = (DC_Obj) obj;

            UNIT_TO_UNIT_VISION status = getUnitVisionStatusPrivate(unit, activeUnit);

            LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Setting visibility for " + unit + " to "
                    + status.toString());

            unit.setUnitVisionStatus(status);
        }
    }

    public UNIT_TO_UNIT_VISION getUnitVisibilityStatus(DC_Obj unit, DC_HeroObj activeUnit) {
        clearCacheForUnit(activeUnit);
        return getUnitVisionStatusPrivate(unit, activeUnit);
    }

    public UNIT_TO_UNIT_VISION getUnitVisibilityStatus(DC_Obj unit) {
        if (activeUnit == null) {
            return null;
        }
        return getUnitVisibilityStatus(unit, activeUnit);
    }

    private UNIT_TO_UNIT_VISION getUnitVisionStatusPrivate(DC_Obj unit, DC_HeroObj activeUnit) {
        UNIT_TO_UNIT_VISION status;

        Boolean result = checkInSightSector(activeUnit, unit);
        if (result == null) {
            status = UNIT_TO_UNIT_VISION.BEYOND_SIGHT;
        } else {
            status = (result) ? UNIT_TO_UNIT_VISION.IN_PLAIN_SIGHT : UNIT_TO_UNIT_VISION.IN_SIGHT;
        }

        // if (status == UNIT_TO_UNIT_VISION.IN_SIGHT)
        // if (checkConcealed(activeUnit, unit)) // TODO check visibility
        // obstacles
        // status = UNIT_TO_UNIT_VISION.CONCEALED;

        return status;

    }

    private void setRelativePlayerVisibility(DC_Player player, Set<Obj> units) {

        for (Obj obj : units) {
            DC_Obj target = (DC_Obj) obj;
            if (DebugMaster.isOmnivisionOn()) {
                if (player.isMe()) {
                    target.setPlayerVisionStatus(UNIT_TO_PLAYER_VISION.DETECTED);
                    return;
                }
            }
            // Chronos.logTimeElapsedForMark("setRelativePlayerVisibility");

            boolean result = false; // detected or not

            Chronos.mark("checkInvisible " + target);
            if (checkInvisible(target)) {
                if (target.getActivePlayerVisionStatus() != UNIT_TO_PLAYER_VISION.INVISIBLE) {
                    player.getLastSeenCache().put(target, target.getCoordinates());
                }
                target.setPlayerVisionStatus(UNIT_TO_PLAYER_VISION.INVISIBLE);
                if (Chronos.getTimeElapsedForMark("checkInvisible " + target) > 5) {
                    Chronos.logTimeElapsedForMark("checkInvisible " + target);
                }
                continue;
            }
            if (Chronos.getTimeElapsedForMark("checkInvisible " + target) > 5) {
                Chronos.logTimeElapsedForMark("checkInvisible " + target);
            }

            // if (target.getUnitVisionStatus() == UNIT_TO_UNIT_VISION.IN_SIGHT)
            // {
            // target.setDetected(true);
            // result = true;
            // } else { //sight range will influence VL

            for (Obj obj1 : player.getControlledUnits()) {
                // if (fastMode) //TODO
                if (obj1 != activeUnit) {
                    continue;
                }
                DC_HeroObj source = (DC_HeroObj) obj1;
                Chronos.mark("getUnitVisibilityLevel " + target);
                VISIBILITY_LEVEL status = getUnitVisibilityLevel(target, source);
                if (Chronos.getTimeElapsedForMark("getUnitVisibilityLevel " + target) > 5) {
                    Chronos.logTimeElapsedForMark("getUnitVisibilityLevel " + target);
                }

                // target.setIdentificationLevel(status);
                // if any unit has him in sight, he's detected
                if (status == VISIBILITY_LEVEL.CLEAR_SIGHT) {
                    // TODO IF AN OUTLINE IS SEEN - 'KNOWN'?
                    LogMaster.log(LogMaster.VISIBILITY_DEBUG, ">>>> " + target + " SPOTTED BY "
                            + source);
                    target.setDetected(true);
                    result = true;
                    break; // TODO set VL to *max* => Identification Level
                }

            }
            if (result) {
                target.setDetected(true);
                if (player.isMe()) {
                    target.setDetectedByPlayer(true);
                }
                target.setPlayerVisionStatus(UNIT_TO_PLAYER_VISION.DETECTED);
                player.getLastSeenCache().put(target, target.getCoordinates());
            } else if (!VisibilityMaster.isZeroVisibility(target)) {
                if (player.isMe()) {
                    if (obj instanceof DC_Cell) {
                        target.setDetectedByPlayer(true);
                    } else {
                        DC_HeroObj unit = (DC_HeroObj) obj;
                        if (unit.isWall() || unit.isLandscape()) {
                            target.setDetectedByPlayer(true);
                        }
                    }
                } else {
                    if (target.isDetected()

                        // TODO only walls? || (obj.getOwner() == Player.NEUTRAL &&
                        // obj.getOBJ_TYPE_ENUM() == OBJ_TYPES.BF_OBJ)
                            ) {
                        target.setPlayerVisionStatus(UNIT_TO_PLAYER_VISION.KNOWN);
                    } else {
                        target.setPlayerVisionStatus(UNIT_TO_PLAYER_VISION.UNKNOWN);
                    }
                    // //Chronos.logTimeElapsedForMark("checkInvisible " + obj);
                }
            }

        }

    }

    private VISIBILITY_LEVEL getUnitVisibilityLevel(DC_Obj target, DC_HeroObj source) {
        VISIBILITY_LEVEL visibilityLevel = VisibilityMaster.getVisibilityLevel(target, source);

        return visibilityLevel;
    }

    /**
     * only invoke after checking if DETECTED
     */
    private boolean checkInvisible(DC_Obj unit) {
        return StealthRule.checkInvisible(unit);
        // if (unit.getPlayerVisionStatus() == UNIT_TO_PLAYER_VISION.DETECTED)
        // return false;
        // if (unit.getIntParam(PARAMS.STEALTH) == 0)
        // return false;
        // else {
        // boolean result = true;
        // int stealth = unit.getIntParam(PARAMS.STEALTH);
        //
        // for (Obj obj : game.getPlayer(!unit.getOwner().isMe())
        // .getControlledUnits()) {
        // if (checkInSight((DC_HeroObj) obj, unit))
        // continue;
        // int detection = obj.getIntParam(PARAMS.DETECTION);
        // if (detection >= stealth) {
        // result = false;
        // break;
        // }
        // }
        // return result;
        // }
    }

    private Boolean checkInSightSector(DC_HeroObj source, DC_Obj target) {
        if (isVisionHacked()) {
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

    public void clearCacheForUnit(DC_Obj obj) {
        cacheSecondary.remove(obj);
        cache.remove(obj);
    }

    public void refresh() {
        LogMaster.log(LogMaster.VISIBILITY_DEBUG, "Refreshing visibility...");

        Chronos.mark("VISIBILITY REFRESH");
        cache.clear();

        cacheSecondary.clear();
        resetVisibilityStatuses();
        Chronos.logTimeElapsedForMark("VISIBILITY REFRESH");
    }

    private DequeImpl<Coordinates> getVisibleCoordinatesSecondary(DC_HeroObj source) {
        return getVisibleCoordinates(source, true);
    }

    private DequeImpl<Coordinates> getVisibleCoordinates(DC_HeroObj source) {
        return getVisibleCoordinates(source, false);
    }

    private DequeImpl<Coordinates> getVisibleCoordinates(DC_HeroObj source, boolean extended) {
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

    public DequeImpl<Coordinates> getVisibleCoordinatesNormalSight(DC_HeroObj source,
                                                                    boolean extended) {
        DequeImpl<Coordinates> coordinates = source.getSightSpectrumCoordinates(extended);
        if (!coordinates.isEmpty()) {
            return coordinates;
        }
        return getSpectrumCoordinates(null, null, null, source, true, null, extended);
    }

    // returns direction of the shadowing
    private DIRECTION getShadowingDirection(DC_HeroObj source, DC_Obj obj) {
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

    @Override
    public boolean checkInvisible(Obj obj1) {
        DC_Obj obj = (DC_Obj) obj1;
        return !checkVisible(obj);
    }

    public boolean isFastMode() {
        return fastMode;
    }

    public void setFastMode(boolean fastMode) {
        this.fastMode = fastMode;
    }

}
