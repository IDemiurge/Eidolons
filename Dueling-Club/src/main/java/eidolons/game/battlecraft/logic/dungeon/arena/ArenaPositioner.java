package eidolons.game.battlecraft.logic.dungeon.arena;

import eidolons.client.cc.logic.UnitLevelManager;
import eidolons.content.PROPS;
import eidolons.game.battlecraft.logic.battle.arena.Wave;
import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.location.building.MapBlock;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.Positioner;
import eidolons.game.battlecraft.logic.dungeon.universal.Spawner;
import eidolons.game.core.game.DC_Game;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.DIRECTION;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.bf.DirectionMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.graphics.GuiManager;
import main.system.launch.CoreEngine;
import main.system.math.PositionMaster;
import main.system.util.Unimplemented;

import java.util.*;

public class ArenaPositioner extends Positioner<ArenaDungeon> {
    public static final FACING_DIRECTION DEFAULT_PLAYER_SIDE = FACING_DIRECTION.NONE;
    public static final FACING_DIRECTION SECOND_PLAYER_SIDE = FACING_DIRECTION.SOUTH;
    public static final DIRECTION DEFAULT_CENTER_SPAWN_SIDE = DIRECTION.LEFT;
    private FACING_DIRECTION DEFAULT_ENEMY_SIDE = RandomWizard.random() ? FACING_DIRECTION.EAST
     : FACING_DIRECTION.WEST;
    // = FACING_DIRECTION.NORTH;
    private int VERTICAL_MAX_UNIT_PER_ROW = 7;
    private int HORIZONTAL_MAX_UNIT_PER_ROW = 5;
    private Spawner spawner;
    private List<FACING_DIRECTION> sides = new ArrayList<>();
    private Map<FACING_DIRECTION, Map<Coordinates, ObjType>> unitGroups = new LinkedHashMap<>();
    private FACING_DIRECTION side;
    private FACING_DIRECTION forcedSide;
    private Map<MapBlock, Map<Coordinates, ObjType>> unitDungeonGroups = new LinkedHashMap<>();

    public ArenaPositioner(DungeonMaster<ArenaDungeon> master) {
        super(master);
    }


    public static DIRECTION getRandomSpawnAdjustDirection() {
        DIRECTION direction = FacingMaster.getRandomFacing().getDirection();
        if (RandomWizard.chance(20)) {
            direction = DirectionMaster.rotate45(direction, RandomWizard.random());
        }
        return direction;
    }

    @Unimplemented
    public List<Coordinates> getPartyCoordinates(Coordinates origin, Boolean me,
                                                 List<String> partyTypes) {

        List<Coordinates> list = new ArrayList<>();
        // TODO
        if (CoreEngine.isArcaneVault() || CoreEngine.isLevelEditor()) {
            origin = new Coordinates(PositionMaster.getMiddleIndex(false), PositionMaster
             .getMiddleIndex(true));
        } else {
            FACING_DIRECTION side = ArenaPositioner.DEFAULT_PLAYER_SIDE;
//            if ( getSpawningSide() != null) {
//                side =   getSpawningSide();
//            }
            unitGroups.put(side, new LinkedHashMap<>());

        }
        return list;
    }

    public List<ObjAtCoordinate> getCoordinatesForUnitGroup(List<ObjType> presetGroupTypes,
                                                            Wave wave, int unitLevel) {

        Map<Coordinates, ObjType> map = new LinkedHashMap<>();

        boolean custom = false;
        Coordinates presetCenterCoordinate = wave.getCoordinates();
        if (wave.isPresetCoordinate()) {
            if (presetCenterCoordinate != null) {
                custom = isAutoOptimalFacing();
            }
        }

        if (wave.getBlock() != null) {
            // TODO return
            // getCoordinatesForUnitGroupInMapBlock(presetGroupTypes, wave,
            // wave.getBlock());
        }

        if (forcedSide != null) {
            side = forcedSide;
            // forcedSide = null;
        } else {
            side = new EnumMaster<FACING_DIRECTION>().retrieveEnumConst(FACING_DIRECTION.class,
             wave.getProperty(PROPS.SPAWNING_SIDE));
            if (side == null) {
                nextSide();
            }
        }

        unitGroups.put(side, map);
        int maxUnitsPerRow = getMaxUnitsPerRow(side);

        List<Coordinates> list = getCoordinatesForUnits(presetGroupTypes, custom,
         presetCenterCoordinate, maxUnitsPerRow);

        List<ObjAtCoordinate> group = new ArrayList<>();
        int i = 0;
        for (Coordinates c : list) {
            ObjType type = new ObjType(presetGroupTypes.get(i));
            type.getGame().initType(type);
            i++;
            if (unitLevel > 0) {
                type = new UnitLevelManager().getLeveledType(type, unitLevel, false);
            }
            ObjAtCoordinate objAtCoordinate = new ObjAtCoordinate(type, c);
            group.add(objAtCoordinate);

        }

        return group;
    }


    public List<Coordinates> getCoordinatesForUnits(List<ObjType> presetGroupTypes, boolean custom,
                                                    Coordinates presetCenterCoordinate, int maxUnitsPerRow) {
        unitCache = new HashMap<>();
        List<Coordinates> list = new ArrayList<>();
        for (ObjType type : presetGroupTypes) {
            // c = getNextSideCoordinate(getBaseCoordinate(side));
            Coordinates c;
            if (custom) {
                c = getFirstLayerCenterCoordinate(presetCenterCoordinate, type, isAutoOptimalFacing());
            } else {
                c = getNextCoordinate(side, maxUnitsPerRow);
            }

            List<ObjType> units = unitCache.get(c);
            if (units == null) {
                units = new ArrayList<>();
            }
            // TODO [update] use facing with this map..
            units.add(type);
            unitCache.put(c, units);
            list.add(c);
//            TODO facingMap.put(c, FacingMaster.rotate180(side)
//                    // side
//            );
            // coordinates.add(c);
        }
        return list;
    }

    public List<Coordinates> getCoordinatesForUnitGroupInMapBlock(List<ObjType> presetGroupTypes,
                                                                  Wave wave, MapBlock block) {

        Map<Coordinates, ObjType> map = new HashMap<>();
        unitDungeonGroups.put(block, map);
        // if (block.getType() == BLOCK_TYPE.ROOM) {
        //
        // }
        // block.getCreepGroups();
        Coordinates c = wave.getCoordinates();
        // block.getKeyCoordinate(); // entrance/treasure/center
        // default
        if (c == null) {
            c = CoordinatesMaster.getCenterCoordinate(block.getCoordinates());
            // side/random?
        }
        List<Coordinates> list = new ArrayList<>();
        // block.getConnectedBlocks();
        // block.getCoordinates();
        for (ObjType type : presetGroupTypes) {
            Coordinates centerCoordinate = getFirstLayerCenterCoordinate(c, type);
            map.put(centerCoordinate, type);
            list.add(centerCoordinate);
        }
        return list;
    }

    private int getMaxUnitsPerRow(FACING_DIRECTION side) {
        if (side == DEFAULT_PLAYER_SIDE) {
            return Integer.MAX_VALUE;
        }
        return (side.isVertical()) ? VERTICAL_MAX_UNIT_PER_ROW : HORIZONTAL_MAX_UNIT_PER_ROW;
    }

    private Coordinates getNextCoordinate(FACING_DIRECTION side, int maxUnitsPerRow) {
        int row = 0;
        boolean vertical = !side.isVertical();
        Coordinates c = null;
        while (row < ((vertical) ? GuiManager.getBF_CompDisplayedCellsY() : GuiManager
         .getBF_CompDisplayedCellsX())) {
            c = getCoordinateInRow(row, side, maxUnitsPerRow);
            if (c != null) {
                break;
            }
            row++;
        }
        return c;
    }

    public Coordinates getCoordinatesForNewUnitInGroup(FACING_DIRECTION side, ObjType type) {
        Coordinates c = // getNextCoordinate(side, getMaxUnitsPerRow(side));
         // [QUICK FIX]
         getFirstLayerCenterCoordinate(unitCache.keySet().iterator().next(), type);
        unitGroups.get(side).put(c, type);
        // if center?
//        facingMap.put(c,
//
//                FacingMaster.rotate180(side));
        return c;
    }

    public void blockGroupRemoved(MapBlock block, List<ObjAtCoordinate> group) {
        // Map<Coordinates, ObjType> map = unitDungeonGroups.getOrCreate(block);
        // TODO [QUICK FIX]
        // if (map != null) {
        // for (Coordinates key : group.keySet())
        // map.remove(key);
        // }

    }

    public void sideRemoved() {
        sides.remove(side);
        side = FacingMaster.rotate180(side);
    }

    public FACING_DIRECTION nextSide() {
        if (side == null) {
            side = DEFAULT_ENEMY_SIDE;
            if (side == null) {
                side = FacingMaster.getRandomFacing();
            }
        } else {
            side = FacingMaster.rotate180(side);
        }
        if (sides.contains(side)) {
            side = FacingMaster.rotate(side, RandomWizard.random());
        }
        if (sides.contains(side)) {
            side = FacingMaster.getRandomFacing();
        }
        sides.add(side);

        return side;
    }

    //this is heresy
    private FACING_DIRECTION getRandomSide() {
        while (true) {
            side = FacingMaster.getRandomFacing();
            if (side != DEFAULT_PLAYER_SIDE) {
                return side;
            }
        }
    }

    /**
     * sort row coordinates by proximity to the center, iterate over them... OR
     * is it a pyramid? with some *preferred width*?
     * <portrait>
     * the issue with current method:
     */
    private Coordinates getNextSideCoordinate(Coordinates c) {
        if (checkCanPlaceUnitOnCoordinate(c, null)) {
            return c;
        }
        Coordinates adjacentCoordinate = getAdjacentCoordinateInRow(c);
        if (adjacentCoordinate != null) {
            return adjacentCoordinate;
        }
        // max per row
        // how to return from recursion and go into a new row?

        // maybe no recursion is necessary?
        // row can be a field... or another level of iteration.

        // for (int i
        // clockwise = !clockwise;
        // getOrCreate coordinate (i)
        //

        // Coordinates nextSideCoordinate =
        // getNextSideCoordinate(adjacentCoordinate);
        // if (checkCanPlaceUnitOnCoordinate(nextSideCoordinate))
        // return nextSideCoordinate;
        // adjacentCoordinate = getNextSideCoordinate(c
        // .getAdjacentCoordinate(DirectionManager.rotate90(side
        // .getDirection(), clockwise)));
        // nextSideCoordinate = getNextSideCoordinate(adjacentCoordinate);
        // if (checkCanPlaceUnitOnCoordinate(nextSideCoordinate))
        // return nextSideCoordinate;
        return getNextSideCoordinate(getNextRowCoordinate(c));
    }

    private Coordinates getCoordinateInRow(int row, FACING_DIRECTION facing, int maxUnitsPerRow) {
        boolean vertical = !facing.isVertical();
        boolean invert = !facing.isCloserToZero();
        int index = PositionMaster.getMiddleIndex(vertical);
        int rowIndex = (invert) ? ((!vertical) ? getGame().getDungeonMaster().getDungeonWrapper().getHeight()
         : getGame().getDungeonMaster().getDungeonWrapper().getWidth())
         - row : row;
        Coordinates coordinate = (vertical) ? new Coordinates(rowIndex, index) : new Coordinates(
         index, rowIndex);
        if (checkCanPlaceUnitOnCoordinate(coordinate, null)) {
            return coordinate;
        }

        boolean negative = RandomWizard.random();
        int a = 0;
        if (negative) {
            a = 1;
        }
        for (int i = a; i < 2 * (maxUnitsPerRow - 1); i++) {
            negative = !negative;
            int c = index;
            if (negative) {
                c -= i;
            } else {
                i--;
                c += i;
            }
            if (invert) {
                c = ((vertical) ? getGame().getDungeonMaster().getDungeonWrapper().getHeight() : getGame()
                 .getDungeonMaster().getDungeonWrapper().getWidth())
                 - c;
            }
            coordinate = (vertical) ? new Coordinates(rowIndex, c) : new Coordinates(c, rowIndex);
            if (checkCanPlaceUnitOnCoordinate(coordinate, null)) {
                return coordinate;
            }

        }

        return null;

    }

    private Coordinates getAdjacentCoordinateInRow(Coordinates c) {
        boolean clockwise = RandomWizard.random();
        Coordinates adjacentCoordinate = c.getAdjacentCoordinate(DirectionMaster.rotate90(side
         .getDirection(), clockwise));
        if (checkCanPlaceUnitOnCoordinate(adjacentCoordinate, null)) {
            return adjacentCoordinate;
        }
        adjacentCoordinate = c.getAdjacentCoordinate(DirectionMaster.rotate90(side.getDirection(),
         !clockwise));
        if (checkCanPlaceUnitOnCoordinate(adjacentCoordinate, null)) {
            return adjacentCoordinate;
        }
        return null;
    }

    private Coordinates getNextRowCoordinate(Coordinates c) {
        return c.getAdjacentCoordinate(DirectionMaster.flip(side.getDirection()));
    }

    private Coordinates getBaseCoordinate(FACING_DIRECTION side) {
        return Coordinates.getMiddleCoordinate(side);
    }

    public synchronized List<FACING_DIRECTION> getSides() {
        return sides;
    }

    public synchronized void setSides(List<FACING_DIRECTION> sides) {
        this.sides = sides;
    }

    public FACING_DIRECTION getClosestEdgeY(Coordinates coordinates) {
        return coordinates.y < getGame().getDungeon().getCellsY() / 2 ? FACING_DIRECTION.NORTH
         : FACING_DIRECTION.SOUTH;
    }

    public FACING_DIRECTION getClosestEdgeX(Coordinates coordinates) {
        return coordinates.x < getGame().getDungeon().getCellsX() / 2 ? FACING_DIRECTION.WEST
         : FACING_DIRECTION.EAST;
    }

    public Spawner getSpawner() {
        if (spawner == null) {
            spawner = getGame().getBattleMaster().getSpawner();
        }
        return spawner;
    }

    public void setSpawner(Spawner spawner) {
        this.spawner = spawner;
    }

    public void setForcedSide(FACING_DIRECTION side2) {
        forcedSide = side2;
    }

    public DC_Game getGame() {
        return DC_Game.game;
    }

    // public List<Coordinates> getCoordinates(FACING_DIRECTION facing, int n) {
    // List<Coordinates> coordinates = new ArrayList<>();
    //
    // if (facing == FACING_DIRECTION.NONE || facing == null) {
    // center = true;
    // }
    // for (int i = 0; i < n; i++) {
    // if (center) {
    //
    // } else {
    // c = getFirstLayerCenterCoordinate(playerStartingCoordinates,
    // objType);
    // }
    // }
    // return coordinates;
    //
    // }
}
