package eidolons.game.module.dungeoncrawl.generator.tilemap;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.level.ZoneCreator;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import eidolons.game.module.dungeoncrawl.generator.model.RoomModel;
import main.data.XLinkedMap;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;

import java.util.Map;

/**
 * Created by JustMe on 2/15/2018.
 */
public class TileMapper {
    private TileConverter converter;
    private LevelModel model;
    private LevelData data;
    private ROOM_CELL DEFAULT_CELL = ROOM_CELL.WALL;

    public TileMapper(LevelModel model, LevelData data) {
        this.model = model;
        this.data = data;
        this.converter = new TileConverter(model, data);
    }

    public static void print(LevelModel model) {
        main.system.auxiliary.log.LogMaster.log(1, model.toASCII_Map());
    }

    public static ROOM_CELL[][] toCellArray(String[][] symbols ) {
        ROOM_CELL[][] cells = new ROOM_CELL[symbols.length][symbols[0].length];
        int x = 0;
        for (String[] column : symbols) {
            for (int y = 0; y < column.length; y++) {
                String symbol = column[y];
                cells[x][y] = ROOM_CELL.getBySymbol(symbol);
            }
            x++;
        }
        return cells;
    }

        public static TileMap createTileMap(Room room) {    TileMap tileMap = new TileMap(room.getWidth(), room.getHeight());

        Map<Coordinates, ROOM_CELL> map = new XLinkedMap<>();
        int x = 0;
        for (String[] column : room.getCells()) {
            for (int y = 0; y < column.length; y++) {
                String symbol = column[y];
                ROOM_CELL cell = ROOM_CELL.getBySymbol(symbol);
                map.put(new AbstractCoordinates(x, y), cell);
            }
            x++;
        }
        tileMap.setMap(map);

        return tileMap;
    }

    public TileMap map() {
        //merge
        TileMap map = new TileMap(model.getCurrentWidth(), model.getCurrentHeight());
        //        build(model);
        fill(model, map);
        return map;
    }

    public TileMap createLevel() {
        map();
        //         new Level
        return null;
    }


    public ROOM_CELL[][] build(LevelModel model) {
        int offsetX = -(model.getLeftMost());
        int offsetY = -(model.getTopMost());
        return build(model.getRoomMap(), offsetX, offsetY,
         model.getCurrentWidth(), model.getCurrentHeight());
    }

    public ROOM_CELL[][] build(Map<Coordinates, Room> map, int offsetX, int offsetY,
                               int currentWidth, int currentHeight) {

        ROOM_CELL[][] cells = new ROOM_CELL[currentWidth][currentHeight];
        //        fillWithDefault(cells);
        for (Coordinates point : map.keySet()) {
            int x = point.x + offsetX;
            int y = point.y + offsetY;
            Room room = map.get(point);
            for (int i = 0; i < room.getCells().length; i++) {
                String[] column = room.getCells()[i];
                for (int j = 0; j < column.length; j++) {
                    String symbol = column[j];
                    ROOM_CELL cell = ROOM_CELL.getBySymbol(symbol);
                    if (i == room.getWidth() / 2)
                        if (j == room.getHeight() / 2) {
                            if (ZoneCreator.TEST_MODE)
                                cell = new EnumMaster<ROOM_CELL>().retrieveEnumConst(ROOM_CELL.class,
                                ""+ room.getZone().getIndex() );
                            else
                                cell = new EnumMaster<ROOM_CELL>().retrieveEnumConst(ROOM_CELL.class,
                                 map.get(point).getType().name());
                        }
                    if (cell != null)
                        try {
                            cells[x][y] = cell;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    y++;
                }
                y = point.y + offsetY;
                x++;
            }

        }
        return cells;
    }

    private void fillWithDefault(ROOM_CELL[][] cells) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                cells[i][j] = DEFAULT_CELL;
            }
        }
    }

    public void fill(LevelModel model, TileMap map) {
        model.setCells(build(model));
        print(model);
        if (true) return;
        int offsetX = -(model.getLeftMost());
        int offsetY = -(model.getTopMost());
        for (Coordinates point : model.getRoomMap().keySet()) {
            RoomModel room = model.getRoomMap().get(point); //block/zone
            int x = point.x;
            int y = point.y;
            for (String[] column : room.getCells()) {
                for (String symbol : column) {
                    ROOM_CELL cell = ROOM_CELL.getBySymbol(symbol);
                    Tile tile = converter.convert(cell, room, x, y++);

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


    //data functions - transform old, save/load


}
