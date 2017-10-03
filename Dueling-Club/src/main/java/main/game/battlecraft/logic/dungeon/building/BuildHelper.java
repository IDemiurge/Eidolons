package main.game.battlecraft.logic.dungeon.building;

import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.game.battlecraft.logic.battlefield.CoordinatesMaster;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.battlecraft.logic.dungeon.Dungeon;
import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.BLOCK_TYPE;
import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.DUNGEON_TEMPLATES;
import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.ROOM_TYPE;
import main.swing.XDimension;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.BooleanMaster;
import main.system.math.DC_PositionMaster;
import main.system.math.MathMaster;

import java.util.LinkedList;
import java.util.List;

public class BuildHelper {
    /*
     * 1) Get Required room-types
	 * 2) 
	 * 
	 * random wall VS always down-OR-right 
	 */

    BuildParameters params; // per zone?
    DungeonPlan plan;
    private Dungeon dungeon;
    private DungeonBuilder builder;
    private MapZone zone;
    private List<Coordinates> usedCoordinates;
    private int id;

    public BuildHelper(Dungeon dungeon, BuildParameters buildParameters) {

        this.dungeon = dungeon;
        this.plan = dungeon.getPlan();

        this.usedCoordinates = new LinkedList<>();
        this.id = 0;
        params = buildParameters;
        if (params == null) {
            params = new BuildParameters();
        }
    }

    public BuildHelper(Dungeon dungeon) {
        this(dungeon, null);

    }

    public Coordinates getMainRoomCoordinates(DUNGEON_TEMPLATES template, ROOM_TYPE type,
                                              int width, int height) {
        boolean corner = false;
        switch (type) {
            case BATTLEFIELD:
                return new Coordinates(0, 0);
            case ENTRANCE_ROOM:
                if (dungeon.getMainEntrance() == null) {
                    return new Coordinates(1, 1);
                }
                return dungeon.getMainEntrance().getCoordinates();
            case EXIT_ROOM:
                return dungeon.getMainExit().getCoordinates();
            case SECRET_ROOM:
            case TREASURE_ROOM:
                corner = true;
        }
        int x = !plan.isFlippedX() ? params.getIntValue(BUILD_PARAMS.WALL_WIDTH) : dungeon
                .getCellsX()
                - params.getIntValue(BUILD_PARAMS.WALL_WIDTH);

        int y = (dungeon.getCellsY() - height) / 2;
        if (corner) {
            y = BooleanMaster.random() ? params.WALL_WIDTH : plan.getBorderY();
        }

        if (!plan.isRotated()) {
            if (corner) {
                x = BooleanMaster.random() ? params.WALL_WIDTH : plan.getBorderX();
            } else {
                x = (dungeon.getCellsX() - width) / 2;
            }

            y = !plan.isFlippedY() ? params.WALL_WIDTH : dungeon.getCellsY() - params.WALL_WIDTH;
        }

        return new Coordinates(true, x, y);
    }

    public XDimension getRoomSize(ROOM_TYPE room) {
        int width = dungeon.getCellsX() * room.getWidthMod() / 100;
        int height = dungeon.getCellsY() * room.getHeightMod() / 100;
        // apply size mod to min/max?
        if (room.getMaxX() != 0) {
            if (width > room.getMaxX()) {
                width = room.getMaxX();
            } else if (width < room.getMinX()) {
                width = room.getMinX();
            } else {
                int random = RandomWizard.getRandomIntBetween(room.getMinX(), room.getMaxX());
                width = MathMaster.getMinMax(RandomWizard.getRandomIntBetween(width, random), room
                        .getMinX(), room.getMaxX());
            }
        }
        if (room.getMaxY() != 0) {
            if (height > room.getMaxY()) {
                height = room.getMaxY();
            } else if (height < room.getMinY()) {
                height = room.getMinY();
            } else {
                int random = RandomWizard.getRandomIntBetween(room.getMinY(), room.getMaxY());
                height = MathMaster.getMinMax(RandomWizard.getRandomIntBetween(height, random),
                        room.getMinY(), room.getMaxY());

            }
        }
        width = MathMaster.applyMod(width, plan.getWidthMod());
        height = MathMaster.applyMod(height, plan.getHeightMod());
        width = MathMaster.applyMod(width, plan.getSizeMod());
        height = MathMaster.applyMod(height, plan.getSizeMod());
        return new XDimension(width, height);
    }

    public boolean tryPlaceRoom(ROOM_TYPE type, Coordinates c, int width, int height) {
        boolean flipX = plan.isFlippedX();
        boolean flipY = plan.isFlippedY();
        return tryPlaceRoom(type, c, width, height, flipX, flipY);
    }

    public boolean tryPlaceCorridor(MapBlock block, Coordinates base, FACING_DIRECTION direction) {
        return tryPlaceCorridor(block, base, direction, false);
    }

    public boolean tryPlaceCorridor(MapBlock block, Coordinates base, FACING_DIRECTION direction,
                                    boolean culdesac) {
        Coordinates c = base;
        FACING_DIRECTION originalDirection = direction;
        List<Coordinates> coordinates = new LinkedList<>();
        int max_bents = 2;
        int length = 1;
        int bents = 0;
        Boolean turn = null;
        // Boolean lastBent =null;
        while (true) { // break CUL DE SACS?!
            c = c.getAdjacentCoordinate(direction.getDirection());
            if (c == null) {
                return false;
            }
            Boolean result = checkLinkOrReject(block, c, culdesac);
            coordinates.add(c);
            length++;
            if (result == null) {
                if (culdesac) {
                    result = checkCulDeSacEnds(originalDirection, c, length);
                }
            }
            if (result != null) {
                if (result) {
                    newBlock(coordinates, culdesac ? BLOCK_TYPE.CULDESAC : BLOCK_TYPE.CORRIDOR);
                } else {
                    return false;
                }
                return true;
            }

            if (turn == null) {
                if (length > 1) {
                    if (bents < max_bents) {
                        turn = checkTurn(block, length);
                    }
                }
                if (turn != null) {
                    direction = FacingMaster.rotate(direction, turn);
                }
            } else {
                turn = null;
            }
        }

    }

    private Boolean checkCulDeSacEnds(FACING_DIRECTION originalDirection, Coordinates c, int length) {
        // if (CoordinatesMaster.getDistanceFromEdge(c, params.WALL_WIDTH,
        // originalDirection,dungeon) == 1)
        // return true;

        if (length > Math.min(plan.getCellsX(), plan.getCellsY())) {
            return true;
        }
        return null;
    }

    private Coordinates getAdjacentPassage(Coordinates c, List<Coordinates> exceptions,
                                           Boolean diagonal) {
        List<Coordinates> coordinates = c.getAdjacentCoordinates(diagonal);
        for (Coordinates c1 : coordinates) {
            if (exceptions.contains(c1)) {
                continue;
            } else if (!usedCoordinates.contains(c1)) {
                continue;
            } else
            // TODO COULD BE MULTIPLE!!!
            {
                return c1;
            }
        }
        return null;
    }

    private MapBlock newBlock(List<Coordinates> coordinates, BLOCK_TYPE type, ROOM_TYPE roomType) {
        MapBlock b = newBlock(coordinates, type);
        b.setRoomType(roomType);
        LogMaster.log(1, " Room Type: " + roomType);
        assert (b.getWidth() > 1 && b.getHeight() > 1);

        return b;

    }

    private MapBlock newBlock(List<Coordinates> coordinates, BLOCK_TYPE type) {
        if (zone == null) {
            this.zone = plan.getZones().get(0);
        }
        MapBlock block = new MapBlock(id, type, zone, plan, coordinates);
        plan.getBlocks().add(block);
        id++;
        usedCoordinates.addAll(coordinates);
        LogMaster.log(1, "New Block: " + block);
        return block;
    }

    private Boolean checkLinkOrReject(MapBlock baseBlock, Coordinates c, boolean culdesac) {
        Coordinates adjacent = getAdjacentPassage(c, baseBlock.getCoordinates(), false);
        if (adjacent != null) {
            if (culdesac) {
                return false;
            }
            usedCoordinates.add(adjacent); // WILL BE IN LINK_COORDINATE
            c = adjacent;
        }
        if (baseBlock.getType() == BLOCK_TYPE.CULDESAC) {
            return null; // or false?
        }
        MapBlock block = plan.getBlockByCoordinate(c);
        if (block == null) {
            return null;
        }
        if (block == baseBlock || culdesac) {
            return false;
        }
        int links = block.getConnectedBlocks().size();
        if (links > 2) {
            return false;
        }
        // if (baseBlock.getRoomType()==) TODO
        if (block.getType() == BLOCK_TYPE.CULDESAC) {
            return false;
        }
        if (block.getConnectedBlocks().containsKey(baseBlock)) {
            return false;
        }
        baseBlock.link(block, c);
        block.link(baseBlock, c);
        return true;
    }

    private Boolean checkTurn(MapBlock block, int length) {
        int chance = 33; // params.
        if (block.getType() == BLOCK_TYPE.CULDESAC) {
            chance /= 2;
        }
        if (!RandomWizard.chance(chance)) {
            return null;
        }
        return RandomWizard.random();
    }

    public boolean tryPlaceRoom(ROOM_TYPE type, Coordinates c, int width, int height,
                                boolean flipX, boolean flipY) {
        List<Coordinates> coordinates = getCoordinates(c, width, height, flipX, flipY);
        if (!checkCoordinates(coordinates)) {
            return false;
        }
        newBlock(coordinates, BLOCK_TYPE.ROOM, type);
        // re-write the method?
        // width will expand down-right...
        return true;// int displace_range //direction always DOWN_RIGHT?

    }

    private boolean checkCoordinates(List<Coordinates> coordinates) {
        return checkCoordinates(coordinates, false);
    }

    private boolean checkCoordinates(List<Coordinates> coordinates, boolean allowAdjacent) {
        for (Coordinates c : coordinates) {
            if (!isWithinDungeonBounds(c)) {
                return false;
            }
            // if (plan.isCoordinateMappedToBlock(c1))
            if (usedCoordinates.contains(c)) {
                return false;
            }
            if (!allowAdjacent) {
                if (checkHasAdjacentIntersections(c)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isWithinDungeonBounds(Coordinates c) {
        if (c.isInvalid()) {
            return false;
        }
        if (!dungeon.isSurface()) {
            if (c.x < params.getIntValue(BUILD_PARAMS.WALL_WIDTH)) {
                return false;
            }
            if (c.x > plan.getBorderX()) {
                return false;
            }
            if (c.y < params.getIntValue(BUILD_PARAMS.WALL_WIDTH)) {
                return false;
            }
            if (c.y > plan.getBorderY()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkHasAdjacentIntersections(Coordinates c) {
        return getAdjacentPassage(c, new LinkedList<>(), true) != null;
    }

    private List<Coordinates> getCoordinates(Coordinates baseCoordinate, int width, int height,
                                             boolean flipX, boolean flipY) {
        FACING_DIRECTION lengthDirection = (flipY) ? FACING_DIRECTION.NORTH
                : FACING_DIRECTION.SOUTH;
        FACING_DIRECTION widthDirection = (flipX) ? FACING_DIRECTION.WEST : FACING_DIRECTION.EAST;
        return DC_PositionMaster.getRectangle(true, lengthDirection, widthDirection,
                baseCoordinate, height, width);

    }

    // boolean reverseDirection
    public int getMaxLength(MapZone zone, Coordinates baseCoordinate, Boolean horizontal) {
        if (!CoordinatesMaster.isWithinBounds(baseCoordinate, zone.x1, zone.x2, zone.y1, zone.y2)) {
            return 0;
        }
        int max = horizontal ? zone.getWidth() : zone.getHeight();
        if (!ListMaster.isNotEmpty(zone.getBlocks())) {
            return max;
        }
        max = horizontal ? zone.getX2() : zone.getY2();
        int min = horizontal ? baseCoordinate.getY() : baseCoordinate.getX();
        int length = 0;
        for (int i = min; i < max; i++) {
            int x = i;
            int y = baseCoordinate.y;
            if (!horizontal) {
                x = baseCoordinate.x;
                y = i;
            }
            if (!checkCoordinateValid(zone, x, y)) {
                break;
            }
            length++;
        }
        LogMaster.log(1, "Max " + (horizontal ? "Length" : "Height")
                + " for " + zone + " from " + baseCoordinate + " = " + length);
        return length;
    }

    public boolean checkCoordinateValid(MapZone zone, int x, int y) {
        // map new Coordinates(x, y) to block - if not null...
        Coordinates coordinates = new Coordinates(x, y);
        if (coordinates.isInvalid()) {
            return false;
        }
        return zone.getBlock(coordinates) == null;
    }

    public BuildParameters getParams() {
        return params;
    }

    public void setParams(BuildParameters params) {
        this.params = params;
    }

    public List<Coordinates> getUsedCoordinates() {
        return usedCoordinates;
    }

    public void setUsedCoordinates(List<Coordinates> usedCoordinates) {
        this.usedCoordinates = usedCoordinates;
    }

    public Coordinates getRandomWallCoordinate(FACING_DIRECTION direction, MapBlock block) {
        if (RandomWizard.chance(params.getIntValue(BUILD_PARAMS.CORRIDOR_OFFSET_CHANCE))) {
            List<Coordinates> edgeCoordinatesFromSquare = CoordinatesMaster
                    .getEdgeCoordinatesFromSquare(direction, block.getCoordinates());
            return new RandomWizard<Coordinates>().getRandomListItem(edgeCoordinatesFromSquare);
        }
        Boolean prefLessMoreMiddle = null;
//        if (block.getRoomType() != ROOM_TYPE.THRONE_ROOM) {
            prefLessMoreMiddle = BooleanMaster.random();
//        }
        Coordinates coord = CoordinatesMaster.getFarmostCoordinateInDirection(direction.getDirection(),
                block.getCoordinates(), prefLessMoreMiddle);

        if (block.getRoomType() != ROOM_TYPE.THRONE_ROOM) {
            // random offset?
        }

        return coord;
    }

    public void setPlan(DungeonPlan plan) {
        this.plan = plan;
    }

    public enum BUILD_PARAMS {
        WIDTH,
        HEIGHT,
        WALL_WIDTH,
        CORRIDOR_OFFSET_CHANCE,
        CUL_DE_SACS,
        TURN_CHANCE,
        FILL_PERCENTAGE,
        // PREFERRED_FILL_PERCENTAGE,
        ADDITIONAL_ROOMS,
        MAIN_ROOMS,
        WIDTH_MOD,
        HEIGHT_MOD,
        SIZE_MOD,
        RANDOM_ROOMS,
        CORRIDORS,
        FILLER_TYPE;
        private DungeonPlan plan;
    }

    // public Coordinates getBaseCoordinate(MapZone startZone, MapZone
    // targetZone,
    // BLOCK_TYPE b, DungeonPlan map) {
    // if (block == null)
    // return BASE_COORDINATE;
    // // use getLastBlock()? good for linking room to last corridor...but some
    // // rooms would have mini-corridor in themselves, on some of its SIDES,
    // // so it just has to be *adjacent* to something... then FILL
    // // zones can be same; in that case try squeezing
    // int width = startZone.getWidth();
    // int height = targetZone.getHeight();
    // // store the last used coordinates; see that it is adjacent; or to the
    // Boolean prefLessMoreMiddle = null;
    // directionPrefX = RandomWizard.random();
    // DIRECTION d = directionPrefX ? DEFAULT_BUILD_DIRECTION.getXDirection()
    // : DEFAULT_BUILD_DIRECTION.getYDirection();
    // Coordinates c = CoordinatesMaster.getFarmostCoordinateInDirection(d,
    // block.getCoordinates(), prefLessMoreMiddle)
    // .getAdjacentCoordinate(d, 2); // wall width TODO
    // if (!checkBaseCoordinateForSize(c, targetZone, width, height)) {
    // directionPrefX = !directionPrefX;
    // d = directionPrefX ? DEFAULT_BUILD_DIRECTION.getXDirection()
    // : DEFAULT_BUILD_DIRECTION.getYDirection();
    // c = CoordinatesMaster.getFarmostCoordinateInDirection(d,
    // block.getCoordinates(), prefLessMoreMiddle)
    // .getAdjacentCoordinate(d, 2);
    // if (!checkBaseCoordinateForSize(c, targetZone, width, height)) {
    // prefLessMoreMiddle = RandomWizard.random();
    // c = CoordinatesMaster.getFarmostCoordinateInDirection(d,
    // block.getCoordinates(), prefLessMoreMiddle)
    // .getAdjacentCoordinate(d, 2);
    // if (!checkBaseCoordinateForSize(c, targetZone, width, height)) {
    // prefLessMoreMiddle = !prefLessMoreMiddle;
    // c = CoordinatesMaster.getFarmostCoordinateInDirection(d,
    // block.getCoordinates(), prefLessMoreMiddle)
    // .getAdjacentCoordinate(d, 2);
    // }
    // if (!checkBaseCoordinateForSize(c, targetZone, width, height))
    // return null;
    //
    // // squeeze() -> reduce w/h incrementally, find best fit
    // }
    // }
    // // checkZoneFull(zone)
    //
    // // switch (b) {
    // // }
    // return c;
    // }
    //
    // // base coordinate - entrance in room? or 0-0 point of sorts?
    // public List<Coordinates> getCoordinates(BLOCK_TYPE b,
    // Coordinates baseCoordinate, MapZone zone, Object... args) {
    // Boolean horizontal;
    // int width = zone.getWidth();
    // int height = zone.getHeight();
    // int length = 0;
    // int maxY = getMaxLength(zone, baseCoordinate, false);
    // int maxX = getMaxLength(zone, baseCoordinate, true); // reverse?
    // DIRECTION d = directionPrefX ? DEFAULT_BUILD_DIRECTION.getXDirection()
    // : DEFAULT_BUILD_DIRECTION.getYDirection(); // TODO
    // List<Coordinates> coordinates = null;
    // switch (b) {
    // case SMALL_ROOM:
    // width /= 2;
    // height /= 2;
    // case CLEARING:
    // if (b == BLOCK_TYPE.CLEARING) {
    // // replace filling with Empty Cells in the whole Zone?
    // }
    // case ROOM:
    // // alter dimensions randomly if possible
    // // int random = RandomWizard
    // // .getRandomInt(Math.min(height, width) / 2);
    // // if (RandomWizard.random())
    // // width = Math.max(2, width - random); // random?
    // // else
    // // height = Math.max(2, height - random);
    // // width = Math.min(width, maxX);
    // // height = Math.min(height, maxY);
    // // square/triangle/diamond/'circle'/natural-style/etc
    //
    // if (args != null) {
    // width = (int) args[0];
    // height = (int) args[1];
    // }
    //
    // coordinates = DC_PositionMaster
    // .getRectangle(
    // FacingManager
    // .getFacingFromDirection(directionPrefX ? DEFAULT_BUILD_DIRECTION
    // .getXDirection()
    // : DEFAULT_BUILD_DIRECTION
    // .getYDirection()),
    // baseCoordinate, height, width, rotated);
    // // TODO preCheck valid coordinates?
    // break;
    // case CROSS:
    // case CULDESAC:
    // // into corner/side; surround always by walls
    // // 3 width - 1 corridor and 2 walls
    // // what's different from CORRIDOR ??? -> base coordinate?
    // // orientation?
    // case CORRIDOR:
    // horizontal = directionPrefX; // (Boolean) args[0]
    // int corridorWidth = 1;
    // // (Integer) args[1];
    // // Coordinates dest = new Coordinates(x2, y2);
    // length = (horizontal) ? maxX : maxY;
    // // continue up to next block!
    // coordinates = DC_PositionMaster.getRectangle(
    // FacingManager.getFacingFromDirection(d),
    // baseCoordinate, length, corridorWidth, rotated);
    // if (b == BLOCK_TYPE.CROSS) {
    // Coordinates crossBase = coordinates
    // .getOrCreate(coordinates.size() / 2); // TODO
    // length = getMaxLength(zone, baseCoordinate, true);
    // coordinates.addAll(DC_PositionMaster.getRectangle(
    // FacingManager.getFacingFromDirection(d), crossBase,
    // length, corridorWidth, rotated));
    // }
    // return coordinates;
    // // target room/... length = [up to]
    // // if (d.isDiagonal()) // non linear
    // // break;
    //
    // }
    //
    // return coordinates;
    // }
    //
    // public boolean checkBaseCoordinateForSize(Coordinates c, MapZone zone,
    // int width, int height) {
    // if (c == null || zone == null)
    // return false;
    // // support rotation of width/height
    // if (getMaxLength(zone, c, true) < width) {
    // return false;
    // }
    // if (getMaxLength(zone, c, false) < height) {
    // return false;
    // }
    //
    // return true;
    // }

}
