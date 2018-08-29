package eidolons.game.module.dungeoncrawl.generator.tilemap;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.auxiliary.data.ArrayMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 7/25/2018.
 */
public class TilesMaster {
    public static int getCellsOfType(ROOM_CELL roomCell, TileMap tileMap) {
        return (int) tileMap.getMap().values().stream().filter(cell -> cell == roomCell).count();
    }

    public static boolean isCornerCell(Coordinates c, TileMap tileMap) {
        return hasConsecutiveAdjacent(c, tileMap, false, 3);
    }

    public static boolean hasConsecutiveAdjacent(Coordinates c, TileMap tileMap,
                                                 boolean passableOrNot, int n) {
        //at least 3 walls adjacent, and all adjacent between themselves!
        //or we could say that at least a streak of 3 when getting adjacent-direction by clockwise
        int i = 0;
        for (DIRECTION direction : DIRECTION.clockwise) {
            ROOM_CELL cell = tileMap.getMap().get(c.getAdjacentCoordinate(direction));

            if (cell != null && isPassable(cell) != passableOrNot)
                i = 0;
            else if (i >= n) {
                return true;
            } else i++;
        }

        return false;
    }

    public static boolean isCellAdjacentTo(Coordinates c,
                                           Room room, Boolean diags_no_only, ROOM_CELL... types) {

        for (Coordinates c1 : c.getAdjacentCoordinates(diags_no_only)) {
            for (ROOM_CELL type : types) {
                if (ArrayMaster.within2D(room.getCells(), c1.x, c1.y))
                    if (room.getCells()[c1.x][c1.y].equals(type.getSymbol())) {
                        return true;
                    }

            }

        }
        return false;
    }

    public static int getInSpectrum(Coordinates c, TileMap tileMap,
                                    boolean passableOrNot, DIRECTION d) {
        return getInSpectrum(c, tileMap, passableOrNot, d, 2, 0);
    }

    public static int getInSpectrum(Coordinates c, TileMap tileMap,
                                    boolean passableOrNot, DIRECTION d,
                                    int bonusForSameDirection, int nullValue) {
        int i = 0;
        for (DIRECTION direction : new DIRECTION[]{d, d.rotate45(true), d.rotate45(false)}) {
            ROOM_CELL cell = tileMap.getMap().get(c.getAdjacentCoordinate(direction));
            if (cell == null) {
                i += nullValue;
            } else if (isPassable(cell) == passableOrNot) {
                if (direction == d)
                    i += bonusForSameDirection;
                else
                    i++;
            }
        }
        return i;
    }

    public static boolean isPassable(String s) {
        ROOM_CELL type = ROOM_CELL.getBySymbol(s);
        return isPassable(type);
    }

    public static boolean isPassable(ROOM_CELL type) {
        if (type == null) {
            return false;
        }
        switch (type) {
            case INDESTRUCTIBLE:
            case WALL:
            case WALL_WITH_DECOR_OVERLAY:
            case WALL_WITH_LIGHT_OVERLAY:
                return false;
        }
        return true;
    }

    public static boolean isEnclosedCell(Coordinates c, Room room) {
        int wrap = getWallWrapForCell(c, room, false);
        return wrap > 1;
    }

    public static boolean isPassageCell(Coordinates c, Room room) {
        int wrap = getWallWrapForCell(c, room, true);
        return wrap == 1 && getAdjacentCells(TileMapper.createTileMap(room.getCells(),
         new AbstractCoordinates(0, 0)).getMap(),
         c, ROOM_CELL.INDESTRUCTIBLE, ROOM_CELL.WALL, ROOM_CELL.WALL_WITH_DECOR_OVERLAY,
         ROOM_CELL.WALL_WITH_LIGHT_OVERLAY, null).size() < 4;
    }

    private static int getWallWrapForCell(Coordinates c, Room room, Boolean diags_no_only) {
        int wrap = 0;
        for (DIRECTION d : DIRECTION.getAdjacencyDirections(diags_no_only)) {
            Coordinates c1 = c.getAdjacentCoordinate(d);
            if (c1.x < 0 || c1.y < 0 || c1.x >= room.getWidth() || c1.y >= room.getHeight())
                continue;
            String cell = room.getCells()[c1.x][c1.y];
            if (!isPassable(ROOM_CELL.getBySymbol(cell))) {
                c1 = c.getAdjacentCoordinate(d.flip());
                if (c1.x < 0 || c1.y < 0 || c1.x >= room.getWidth() || c1.y >= room.getHeight())
                    continue;
                cell = room.getCells()[c1.x][c1.y];
                if (!isPassable(ROOM_CELL.getBySymbol(cell)))
                    wrap++;
            }
        }
        return wrap;
    }

    public static boolean isEntranceCell(Coordinates c, Room room) {
        return isEntranceCell(c, room, room.getEntranceCoordinates(), room.getEntrance());
    }

    public static boolean isEntranceCell(Coordinates c, Room room,
                                         Coordinates entrance, FACING_DIRECTION entranceSide) {
        if (room.getCells()[c.x][c.y].equals(ROOM_CELL.ROOM_EXIT.getSymbol()))
            return true;
        List<Coordinates> list = room.getExitCoordinates();
        if (list.contains(c))
            return true;
        int i = 0;
        for (FACING_DIRECTION exit : room.getUsedExits()) {
            if (room.getExitCoordinates().size() <= i)
                continue;
            Coordinates nextC = room.getExitCoordinates().get(i++)
             .getAdjacentCoordinate(exit.getDirection().rotate180());
            if (c.equals(nextC))
                return true;
        }
        if (c.equals(entrance))
            return true;

        if (entranceSide != null) {
            Coordinates nextC =entrance
             .getAdjacentCoordinate(entranceSide.getDirection().rotate180());
            if (c.equals(nextC))
                return true;
        }
        return false;
    }

    public static int getAdjacentCount(Map<Coordinates, ROOM_CELL> modelMap,
                                       Coordinates c, ROOM_CELL... cellTypeOrNull) {

        return getAdjacentCells(modelMap, c, cellTypeOrNull).size();
    }

    public static List<Coordinates> getAdjacentCells(Map<Coordinates, ROOM_CELL> modelMap,
                                                     Coordinates c, ROOM_CELL... cellTypes) {
        return getAdjacentCells(modelMap, true, c, cellTypes);
    }

    public static List<Coordinates> getAdjacentCells(Map<Coordinates, ROOM_CELL> modelMap,
                                                     Boolean diags, Coordinates c, ROOM_CELL... cellTypes) {
        List<Coordinates> cells = new ArrayList<>();
        n:
        for (Coordinates coordinates : c.getAdjacentCoordinates(diags)) {
            for (ROOM_CELL cellType : cellTypes) {
                if ((cellType == null && !modelMap.containsKey(coordinates)) ||
                 modelMap.get(coordinates) == cellType) {
                    cells.add(coordinates);
                    continue n;
                }
            }

        }
        return cells;
    }

    public static float getDistanceFromEdge(Coordinates c, int width, int height) {
        int distX = Math.min(c.x, width - c.x);
        int distY = Math.min(c.y, height - c.y);
        return (float) Math.sqrt(distX * distX + distY * distY);

    }

    public static float getDistanceFromCenter(Coordinates c, int width, int height) {
        int distX = Math.abs(width / 2 - c.x);
        int distY = Math.abs(height / 2 - c.y);
        return (float) Math.sqrt(distX * distX + distY * distY);
    }

    public static boolean isEdgeCell(Coordinates c, Room room) {
        if (c.x == 0)
            return true;
        if (c.y == 0)
            return true;
        return c.x == room.getWidth() - 1 ||
         c.y == room.getHeight() - 1;
    }

}
