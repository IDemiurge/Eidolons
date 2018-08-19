package eidolons.game.module.dungeoncrawl.generator.tilemap;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;

import java.util.List;

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

                if (cell != null && isPassable(cell)!=passableOrNot)
                    i = 0;
                else  if (i >= n) {
                    return true;
                } else i++;
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
            if (cell==null ){
                i+=nullValue;
            } else
            if (isPassable(cell)==passableOrNot)
            {
                if (direction==d)
                    i+=bonusForSameDirection;
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
        switch (type) {
            case INDESTRUCTIBLE:
            case WALL:
            case WALL_WITH_DECOR_OVERLAY:
            case WALL_WITH_LIGHT_OVERLAY:
                return false;
        }
        return true;
    }

    public static boolean isEntranceCell(Coordinates c, Room room) {
        if (room.getCells()[c.x][c.y].equals(ROOM_CELL.ROOM_EXIT.getSymbol()))
            return true;
        List<Coordinates> list = room.getExitCoordinates();
        if (list.contains(c))
            return true;
        int i =0;
        for (FACING_DIRECTION exit : room.getUsedExits()) {
            if (room.getExitCoordinates().size()<=i)
                continue;
            Coordinates nextC = room.getExitCoordinates().get(i++)
             .getAdjacentCoordinate(exit. getDirection().rotate180());
            if (c.equals(nextC))
                return true;
        }
        if (room.getEntranceCoordinates()==null ){
            return false;
        }
        return c.x == room.getEntranceCoordinates().x &&
         room.getEntranceCoordinates().y == c.y;
    }

    public static float getDistanceFromEdge(Coordinates c, int width, int height) {
        int distX = Math.min(c.x, width - c.x);
        int distY = Math.min(c.y, height - c.y);
        return (float) Math.sqrt(distX * distX + distY * distY);

    }

    public static float getDistanceFromCenter(Coordinates c, int width, int height) {
        int distX = Math.abs( width/2 - c.x);
        int distY  = Math.abs( height/2 - c.y);
        return (float) Math.sqrt(distX * distX + distY * distY);
    }

    public static boolean isEdgeCell(Coordinates c, Room room) {
        if (c.x==0)
            return true;
        if (c.y==0)
            return true;
        return c.x==room.getWidth()-1 ||
         c.y==room.getHeight()-1 ;
    }
}
