package main.game.logic.dungeon.generator.tilemap;

import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.game.logic.dungeon.generator.GeneratorEnums.ROOM_CELL;
import main.game.logic.dungeon.generator.LevelData;
import main.game.logic.dungeon.generator.model.LevelModel;
import main.game.logic.dungeon.generator.model.Room;
import main.game.logic.dungeon.generator.model.RoomModel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;

/**
 * Created by JustMe on 2/15/2018.
 */
public class TileMapper {
    LevelModel model;
    LevelData data;
    TileMap map;

    public TileMapper(LevelModel model, LevelData data) {
        this.model = model;
        this.data = data;
    }

    public TileMap map() {
        //merge
        map = new TileMap(model.getCurrentWidth(), model.getCurrentHeight());
//        build(model);
        fill(model, map);
        return map;
    }

    public TileMap createLevel() {
        map();
//         new Level
        return null;
    }

    private void build(LevelModel model) {
        int offsetX = -(model.getLeftMost());
        int offsetY = -(model.getTopMost());
        ROOM_CELL[][] cells = new ROOM_CELL[model.getCurrentWidth()][model.getCurrentHeight()];
        for (Point point : model.getRoomMap().keySet()) {
            int x = point.x + offsetX;
            int y = point.y + offsetY;
            Room room = model.getRoomMap().get(point);
            for (String[] column : room.getCells()) {
                for (String symbol : column) {
                    ROOM_CELL cell = ROOM_CELL.getBySymbol(symbol);
                    try {
                        cells[x][y] = cell;
                    } catch (Exception e) {
//                        e.printStackTrace();
                    }
                    y++;
                }
                y=point.y + offsetY;
                x++;
            }

        }
        model.setCells(cells);
    }

    private void print(LevelModel model) {
        ROOM_CELL[][] cells = model.getCells();
        String string = "\n";
        for (int x = 0; x < model.getCurrentWidth(); x++) {
            for (int y = 0; y < model.getCurrentHeight(); y++) {
                if (cells[x][y]==null )
                    string += "X";
                else
                string += cells[x][y].getSymbol();
            }
            string += "\n";

        }
        main.system.auxiliary.log.LogMaster.log(1, string);

    }

    private void fill(LevelModel model, TileMap map) {
        build(model);
        print(model);
        if (true) return;
        int offsetX = -(model.getLeftMost());
        int offsetY = -(model.getTopMost());
        for (Point point : model.getRoomMap().keySet()) {
            RoomModel room = model.getRoomMap().get(point);
            int x = point.x;
            int y = point.y;
            for (String[] column : room.getCells()) {
                for (String symbol : column) {
                    ROOM_CELL cell = ROOM_CELL.getBySymbol(symbol);
                    Tile tile = convert(cell, room, x, y++);

                    if (tile.getData().length == 0)
                        main.system.auxiliary.log.LogMaster.log(1, (x + offsetX) + "-" + (y + offsetY)
                         + " is empty");
                    else
                        main.system.auxiliary.log.LogMaster.log(1, (x + offsetX) + "-" + (y + offsetY)
                         + "= " + tile.getData()[0].getKey());

                    try {
                        map.getTiles()[x + offsetX][y + offsetY] = tile;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                x++;
            }
        }
        for (ROOM_CELL[] row : model.getCells()) {
//            for (String sub: )
        }
    }

    private Tile convert(ROOM_CELL cell, RoomModel room, int x, int y) {
        if (cell == ROOM_CELL.FLOOR)
            return new Tile();
        switch (cell) {
            case WALL:
                return new Tile(getWall(room, x, y));
            case CONTAINER:
//                return new Tile(getContainer(room, x, y));
            case DOOR:
//                return new Tile(getDoor(room, x, y));
            case ART_OBJ:
//                return new Tile(getArtObj(room, x, y));
            case GUARD:
//                return new Tile(getGuard(room, x, y));
            case LIGHT_EMITTER:
//                return new Tile(getLightEmitter(room, x, y));
        }
        return new Tile();
    }

    private Pair<String, OBJ_TYPE>[] getWall(RoomModel room, int x, int y) {
        String wallType = "Stone Wall"; //from block/zone!!!
        return new Pair[]{
         new ImmutablePair(wallType, DC_TYPE.BF_OBJ)
        };
    }


    //data functions - transform old, save/load


}
