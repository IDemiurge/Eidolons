package eidolons.game.module.dungeoncrawl.generator.tilemap;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;

import java.util.List;

/**
 * Created by JustMe on 7/25/2018.
 */
public class TilesMaster {
    public static int getCellsOfType(ROOM_CELL roomCell, TileMap tileMap) {
        return (int) tileMap.getMap().values().stream().filter(cell -> cell == roomCell).count();
    }

    public static boolean isCornerCell(Coordinates c, TileMap tileMap) {
        //at least 3 walls adjacent, and all adjacent between themselves!
        //or we could say that at least a streak of 3 when getting adjacent-direction by clockwise
            int i = 0;
            for (DIRECTION direction : DIRECTION.clockwise) {
                ROOM_CELL cell = tileMap.getMap().get(c.getAdjacentCoordinate(direction));

                if (cell != null && isPassable(cell))
                    i = 0;
                else i++;
            }
            return i >= 3;
    }

    public static boolean isPassable(String s) {
        ROOM_CELL type = ROOM_CELL.getBySymbol(s);
        return isPassable(type);
    }

    public static boolean isPassable(ROOM_CELL type) {
        switch (type) {
            case WALL:
            case WALL_WITH_DECOR_OVERLAY:
            case WALL_WITH_LIGHT_OVERLAY:
                return false;
        }
        return true;
    }

    public static boolean isEntranceCell(Coordinates c, Room room) {
        List<Coordinates> list = room.getExitCoordinatess();
        if (list.contains(c))
            return true;

        if (room.getEntranceCoordinates()==null ){
            return false;
        }
        return c.x == room.getEntranceCoordinates().x &&
         room.getEntranceCoordinates().y == c.y;
    }
}
