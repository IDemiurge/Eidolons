package main.game.logic.generic;

import main.client.cc.logic.UnitLevelManager;
import main.content.C_OBJ_TYPE;
import main.content.PROPS;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjAtCoordinate;
import main.entity.type.ObjType;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.Coordinates.FACING_DIRECTION;
import main.game.battlefield.CoordinatesMaster;
import main.game.battlefield.DirectionMaster;
import main.game.battlefield.FacingMaster;
import main.game.battlefield.map.DungeonMapGenerator.MAP_ZONES;
import main.game.core.game.DC_Game;
import main.game.core.game.DC_Game.GAME_TYPE;
import main.game.logic.arena.Wave;
import main.game.logic.dungeon.building.MapBlock;
import main.rules.action.StackingRule;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.graphics.GuiManager;
import main.system.launch.CoreEngine;
import main.system.math.PositionMaster;

import java.util.*;

public class Positioner {
    public static final FACING_DIRECTION DEFAULT_PLAYER_SIDE = FACING_DIRECTION.NONE;
    public static final FACING_DIRECTION SECOND_PLAYER_SIDE = FACING_DIRECTION.SOUTH;
    public static final DIRECTION DEFAULT_CENTER_SPAWN_SIDE = DIRECTION.LEFT;
    private FACING_DIRECTION DEFAULT_ENEMY_SIDE = RandomWizard.random() ? FACING_DIRECTION.EAST
            : FACING_DIRECTION.WEST;
    // = FACING_DIRECTION.NORTH;
    private int VERTICAL_MAX_UNIT_PER_ROW = 7;
    private int HORIZONTAL_MAX_UNIT_PER_ROW = 5;
    private SpawnManager spawner;
    private List<FACING_DIRECTION> sides = new LinkedList<>();
    private Map<FACING_DIRECTION, Map<Coordinates, ObjType>> unitGroups = new LinkedHashMap<>();
    private Map<Coordinates, FACING_DIRECTION> facingMap = new HashMap<>();
    private FACING_DIRECTION side;
    private FACING_DIRECTION forcedSide;
    private Map<MapBlock, Map<Coordinates, ObjType>> unitDungeonGroups = new LinkedHashMap<>();
    private Map<Coordinates, List<ObjType>> unitCache;
    private Integer maxSpacePercentageTaken = 100;

    public Positioner() {

    }

    public Positioner(SpawnManager spawnManager) {
        this.setSpawner(spawnManager);
    }

    public static Coordinates adjustCoordinate(Coordinates c, FACING_DIRECTION facing) {
        return adjustCoordinate(null, c, facing);
    }

    public static Coordinates adjustCoordinate(Entity entity, Coordinates c, FACING_DIRECTION facing) {
        if (c == null) {
            return null;
        }
        Loop loop = new Loop(50);
        Coordinates coordinate = new Coordinates(c.x, c.y);
        while (loop.continues()) { // TODO remove from adj. list to limit
            // iterations to 8!
            DIRECTION direction = getRandomSpawnAdjustDirection();
            coordinate = c.getAdjacentCoordinate(direction);
            if (coordinate != null) {
                if (!DC_Game.game.isSimulation()) {
                    if (DC_Game.game.getBattleField().canMoveOnto(entity, coordinate)) {
                        break;
                    }
                }
                if (new StackingRule(DC_Game.game).canBeMovedOnto(entity, coordinate)) {
                    break;
                }
            }
        }
        loop = new Loop(50); // second layer in case first one is fully
        // blocked
        while (!loop.continues() && !DC_Game.game.getBattleField().canMoveOnto(entity, coordinate)

                // (DC_Game.game.getBattleField().getGrid().isCoordinateObstructed(coordinate)
                || coordinate == null) {

            Coordinates adjacentCoordinate = c
                    .getAdjacentCoordinate(getRandomSpawnAdjustDirection());
            coordinate = adjustCoordinate(adjacentCoordinate, facing);
        }
        if (coordinate.isInvalid()) {
            return null;
        }
        return coordinate;
    }

    public static DIRECTION getRandomSpawnAdjustDirection() {
        DIRECTION direction = FacingMaster.getRandomFacing().getDirection();
        if (RandomWizard.chance(20)) {
            direction = DirectionMaster.rotate45(direction, RandomWizard.random());
        }
        return direction;
    }

    public Map<Unit, Coordinates> getPartyCoordinates(List<Unit> members) {
        List<String> list = new LinkedList<>();
        for (Unit h : members) {
            list.add(h.getName());
        }
        List<Coordinates> c = getPlayerPartyCoordinates(list);

        return new MapMaster<Unit, Coordinates>().constructMap(members, c);
    }

    public List<Coordinates> getPlayerPartyCoordinates(List<String> partyTypes) {
        return getPartyCoordinates(null, true, partyTypes);
    }

    public List<Coordinates> getPartyCoordinates(Coordinates origin, Boolean me,
                                                 List<String> partyTypes) {

        List<Coordinates> list = new LinkedList<>();
        // TODO
        if (CoreEngine.isArcaneVault() || CoreEngine.isLevelEditor()) {
            origin = new Coordinates(PositionMaster.getMiddleIndex(false), PositionMaster
                    .getMiddleIndex(true));
        } else {
            FACING_DIRECTION side = DEFAULT_PLAYER_SIDE;
            if (DC_Game.game.getDungeonMaster().getDungeon().getSpawningSide() != null) {
                side = DC_Game.game.getDungeonMaster().getDungeon().getSpawningSide();
            }
            if (getGame().getGameType() == GAME_TYPE.SCENARIO) {
                if (getGame().getBattleManager().getEncounter() != null) {
                    if (!getSpawner().getGame().getBattleManager().getEncounter().isSurrounded()) {
                        side = SECOND_PLAYER_SIDE;
                    }
                }
            }
            unitGroups.put(side, new LinkedHashMap<>());

            if (me != null) {
                if (me) {
                    if (PartyManager.getParty() != null) {
                        Unit mh = PartyManager.getParty().getMiddleHero();
                        if (mh != null) {
                            int index = partyTypes.indexOf(mh.getName());
                            if (index > -1) {
                                String s = partyTypes.remove(index);
                                partyTypes.add(0, s);
                            }
                        }
                    }
                    origin = DC_Game.game.getDungeonMaster().getDungeon()
                            .getPlayerSpawnCoordinates(); // ??
                    if (origin == null) {
                        origin = Coordinates.getMiddleCoordinate(side);
                    }

                    if (DC_Game.game.getDungeonMaster().getDungeon().getPlayerSpawnCoordinates() != null) {
                        origin = DC_Game.game.getDungeonMaster().getDungeon()
                                .getPlayerSpawnCoordinates();
                    }

                } else {

                    origin = DC_Game.game.getDungeonMaster().getDungeon()
                            .getEnemySpawningCoordinates();
                    if (origin == null) {
                        // getGame().getDungeon().getDefaultEnemyCoordinates();
                        // for
                        // more control... TODO
                        origin = getEnemyTestPartyCoordinates();
                    }
                }
            }
        }
        for (String type : partyTypes) {
            ObjType objType = DataManager.getType(type, C_OBJ_TYPE.UNITS_CHARS);
            // DIRECTION[] prefs = {
            // DIRECTION.LEFT, DIRECTION.RIGHT,
            // };
            Coordinates c = getFirstLayerCenterCoordinate(origin, objType, false);
            if (!DC_Game.game.isSimulation()) {
                if (unitGroups.get(side) != null) {
                    unitGroups.get(side).put(c, objType); // facing? TODO
                }
            }
            list.add(c);
        }

        return list;
    }

    public Coordinates getEnemyTestPartyCoordinates() {
        // TODO encounter?

        // default - getOrCreate a random point in some range from player start

        Coordinates playerC = DC_Game.game.getDungeonMaster().getDungeon()
                .getPlayerSpawnCoordinates();
        if (playerC == null) {
            playerC = Coordinates.getMiddleCoordinate(Positioner.DEFAULT_PLAYER_SIDE);
        }
        Loop.startLoop(100);
        while (Loop.loopContinues()) {
            int x = playerC.x + RandomWizard.getRandomIntBetween(-4, 4);
            int y = playerC.y + RandomWizard.getRandomIntBetween(-4, 4);
            if (y >= GuiManager.getBattleFieldHeight() - 1) {
                continue;
            }
            if (x >= GuiManager.getBattleFieldWidth() - 1) {
                continue;
            }
            if (y <= 0) {
                continue;
            }
            if (x <= 0) {
                continue;
            }
            return new Coordinates(x, y);
        }
        return null;
    }

    public List<ObjAtCoordinate> getCoordinatesForUnitGroup(List<ObjType> presetGroupTypes,
                                                            Wave wave, int unitLevel) {

        Map<Coordinates, ObjType> map = new LinkedHashMap<>();

        boolean custom = false;
        Coordinates presetCenterCoordinate = wave.getCoordinates();
        if (wave.isPresetCoordinate()) {
            if (presetCenterCoordinate != null) {
                custom = true;
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

        List<ObjAtCoordinate> group = new LinkedList<>();
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
        List<Coordinates> list = new LinkedList<>();
        for (ObjType type : presetGroupTypes) {
            // c = getNextSideCoordinate(getBaseCoordinate(side));
            Coordinates c;
            if (custom) {
                c = getFirstLayerCenterCoordinate(presetCenterCoordinate, type, true);
            } else {
                c = getNextCoordinate(side, maxUnitsPerRow);
            }

            List<ObjType> units = unitCache.get(c);
            if (units == null) {
                units = new LinkedList<>();
            }
            // TODO [update] use facing with this map..
            units.add(type);
            unitCache.put(c, units);
            list.add(c);
            facingMap.put(c, FacingMaster.rotate180(side)
                    // side
            );
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
        List<Coordinates> list = new LinkedList<>();
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
        facingMap.put(c,

                FacingMaster.rotate180(side));
        return c;
    }

    private Coordinates getFirstLayerCenterCoordinate(Coordinates c, ObjType objType) {
        return getFirstLayerCenterCoordinate(c, objType, true);
    }

    private Coordinates getFirstLayerCenterCoordinate(Coordinates c, ObjType objType,
                                                      boolean randomPrefSide) {
        if (checkBlock(c)) {
            if (checkCoordinate(c, objType)) {
                return c;
            }
        }
        DIRECTION spawnSide = DEFAULT_CENTER_SPAWN_SIDE;
        if (randomPrefSide) {
            spawnSide = DirectionMaster.getRandomDirection();
        }
        Coordinates adjacentCoordinate = c.getAdjacentCoordinate(spawnSide);
        if (checkCoordinate(adjacentCoordinate, objType)) {
            facingMap.put(adjacentCoordinate, FacingMaster.getFacingFromDirection(
                    DEFAULT_CENTER_SPAWN_SIDE, false, false));
            return adjacentCoordinate;
        }
        DIRECTION direction = spawnSide;
        DirectionMaster.rotate90(spawnSide, false);
//         = c.getAdjacentCoordinate(direction);
        Coordinates nextCoordinate;// orthogonal
        while (true) {
            direction = DirectionMaster.rotate90(direction, false);
            nextCoordinate = c.getAdjacentCoordinate(direction);
            if (checkCoordinate(nextCoordinate, objType)) {
                facingMap.put(nextCoordinate, FacingMaster.getFacingFromDirection(direction, true,
                        true));
                return nextCoordinate;
            }
            if (direction == spawnSide) {
                break;
            }
        }
        direction = DirectionMaster.rotate45(spawnSide, false);
        // diagonal
        nextCoordinate = adjustCoordinate(nextCoordinate, FacingMaster
                .getFacingFromDirection(direction));
        return nextCoordinate;
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
     * <p>
     * the issue with current method:
     */
    private Coordinates getNextSideCoordinate(Coordinates c) {
        if (checkCoordinate(c, null)) {
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
        // if (checkCoordinate(nextSideCoordinate))
        // return nextSideCoordinate;
        // adjacentCoordinate = getNextSideCoordinate(c
        // .getAdjacentCoordinate(DirectionManager.rotate90(side
        // .getDirection(), clockwise)));
        // nextSideCoordinate = getNextSideCoordinate(adjacentCoordinate);
        // if (checkCoordinate(nextSideCoordinate))
        // return nextSideCoordinate;
        return getNextSideCoordinate(getNextRowCoordinate(c));
    }

    private Coordinates getCoordinateInRow(int row, FACING_DIRECTION facing, int maxUnitsPerRow) {
        boolean vertical = !facing.isVertical();
        boolean invert = !facing.isCloserToZero();
        int index = PositionMaster.getMiddleIndex(vertical);
        int rowIndex = (invert) ? ((!vertical) ? getGame().getDungeonMaster().getLevelHeight()
                : getGame().getDungeonMaster().getLevelWidth())
                - row : row;
        Coordinates coordinate = (vertical) ? new Coordinates(rowIndex, index) : new Coordinates(
                index, rowIndex);
        if (checkCoordinate(coordinate, null)) {
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
                c = ((vertical) ? getGame().getDungeonMaster().getLevelHeight() : getGame()
                        .getDungeonMaster().getLevelWidth())
                        - c;
            }
            coordinate = (vertical) ? new Coordinates(rowIndex, c) : new Coordinates(c, rowIndex);
            if (checkCoordinate(coordinate, null)) {
                return coordinate;
            }

        }

        return null;

    }

    private Coordinates getAdjacentCoordinateInRow(Coordinates c) {
        boolean clockwise = RandomWizard.random();
        Coordinates adjacentCoordinate = c.getAdjacentCoordinate(DirectionMaster.rotate90(side
                .getDirection(), clockwise));
        if (checkCoordinate(adjacentCoordinate, null)) {
            return adjacentCoordinate;
        }
        adjacentCoordinate = c.getAdjacentCoordinate(DirectionMaster.rotate90(side.getDirection(),
                !clockwise));
        if (checkCoordinate(adjacentCoordinate, null)) {
            return adjacentCoordinate;
        }
        return null;
    }

    private Coordinates getNextRowCoordinate(Coordinates c) {
        return c.getAdjacentCoordinate(DirectionMaster.flip(side.getDirection()));
    }

    private boolean checkBlock(Coordinates c) {
        for (Map<Coordinates, ObjType> group : unitDungeonGroups.values()) {
            if (group.keySet().contains(c)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkCoordinate(Coordinates c, Entity entityToPosition) {
        if (c == null) {
            return false;
        }

        // for (Map<Coordinates, ObjType> group : unitGroups.values())
        // if (group.keySet().contains(c)) // TODO
        // return false;
        List<ObjType> otherUnits = null;
        if (unitCache != null) {
            otherUnits = unitCache.get(c);
        }
        if (otherUnits == null) {
            otherUnits = new LinkedList<>();
        }

        try {
            return StackingRule.checkCanPlace(maxSpacePercentageTaken, c, entityToPosition, otherUnits);
        } catch (Exception e) {
            return true;
        }
    }

    private Coordinates getBaseCoordinate(FACING_DIRECTION side) {
        return Coordinates.getMiddleCoordinate(side);
    }

    public FACING_DIRECTION getFacingOptimal(Coordinates c) {
        Collection<Obj> units = getGame().getPlayer(true).getControlledUnits();
        return FacingMaster.getOptimalFacingTowardsUnits(c, units);


    }

    public FACING_DIRECTION getFacingInitial(Coordinates c) {
        // TODO
        return FacingMaster
                .getRelativeFacing(c, getGame().getDungeon().getPlayerSpawnCoordinates());

    }

    private boolean isAutoOptimalFacing() {
        return true;
    }

    public FACING_DIRECTION getFacingForEnemy(Coordinates c) {
        return getFacingOptimal(c);
    }

    public FACING_DIRECTION getPartyMemberFacing(Coordinates c) {

        if (facingMap.containsKey(c)) {
            return facingMap.get(c);
        }
        MAP_ZONES zone = null;
        for (MAP_ZONES z : MAP_ZONES.values()) {
            for (String s : StringMaster.openContainer(z.getCoordinates(), ",")) {
                if (c.toString().equals(s)) {
                    zone = z;
                    break;
                }
            }
        }
        if (zone != null) {
            switch (zone) {
                case SIDE_EAST:
                    return FACING_DIRECTION.WEST;
                case SIDE_NORTH:
                    return FACING_DIRECTION.SOUTH;
                case SIDE_SOUTH:
                    return FACING_DIRECTION.NORTH;
                case SIDE_WEST:
                    return FACING_DIRECTION.EAST;
            }
        }
        return DEFAULT_ENEMY_SIDE;
    }

    public Integer getMaxSpacePercentageTaken() {
        return maxSpacePercentageTaken;
    }

    public void setMaxSpacePercentageTaken(Integer maxSpacePercentageTaken) {
        this.maxSpacePercentageTaken = maxSpacePercentageTaken;
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

    public SpawnManager getSpawner() {
        if (spawner == null) {
            spawner = getGame().getArenaManager().getSpawnManager();
        }
        return spawner;
    }

    public void setSpawner(SpawnManager spawner) {
        this.spawner = spawner;
    }

    public void setForcedSide(FACING_DIRECTION side2) {
        forcedSide = side2;
    }

    public DC_Game getGame() {
        return DC_Game.game;
    }

    // public List<Coordinates> getCoordinates(FACING_DIRECTION facing, int n) {
    // List<Coordinates> coordinates = new LinkedList<>();
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
