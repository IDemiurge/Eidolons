package eidolons.game.module.dungeoncrawl.generator.tilemap;

import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.init.RngXmlMaster;
import eidolons.game.module.dungeoncrawl.generator.level.ZoneCreator;
import eidolons.game.module.dungeoncrawl.generator.model.AbstractCoordinates;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import main.data.XLinkedMap;
import main.game.bf.Coordinates;
import main.system.auxiliary.EnumMaster;
import main.system.launch.CoreEngine;

import java.util.Map;

/**
 * Created by JustMe on 2/15/2018.
 */
public class TileMapper {
    private TileConverter converter;
    private LevelModel model;
    private LevelData data;
    private ROOM_CELL DEFAULT_CELL = ROOM_CELL.WALL;
    private static boolean loggingOff;

    public TileMapper(LevelModel model, LevelData data) {
        this.model = model;
        this.data = data;
        this.converter = new TileConverter(model, data);
    }

    public static void print(LevelModel model) {
        main.system.auxiliary.log.LogMaster.log(1, model.toASCII_Map());
    }

    public static ROOM_CELL[][] toCellArray(String[][] symbols) {
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

    public static String[][] toSymbolArray(ROOM_CELL[][] symbols) {
        String[][] cells = new String[symbols.length][symbols[0].length];
        int x = 0;
        for (ROOM_CELL[] column : symbols) {
            for (int y = 0; y < column.length; y++) {
                ROOM_CELL symbol = column[y];
                if (symbol==null )
                    cells[x][y] = "-";
                else
                cells[x][y] = symbol.getSymbol();
            }
            x++;
        }
        return cells;
    }

    public static TileMap createTileMap(LevelModel model) {
        ROOM_CELL[][] cells = TileMapper.build(model);
        return TileMapper.createTileMap(TileMapper.toSymbolArray(cells),
         new AbstractCoordinates(0, 0));
    }

    public static TileMap createTileMap(Room room) {
        return createTileMap(room.getCells(), room.getCoordinates());
    }

    public static TileMap createTileMap(String[][] cells, Coordinates offset) {
        Map<Coordinates, ROOM_CELL> map = new XLinkedMap<>();
        int x = 0;
        for (String[] column : cells) {
            for (int y = 0; y < column.length; y++) {
                String symbol = column[y];
                ROOM_CELL cell = ROOM_CELL.getBySymbol(symbol);
                map.put(new AbstractCoordinates(x, y).offset(offset), cell);
            }
            x++;
        }

        return new TileMap(map);
    }

    public static ROOM_CELL[][] getCells(TileMap map) {
        ROOM_CELL[][] cells = new ROOM_CELL[map.getWidth()][map.getHeight()];
        for (Coordinates point : map.getMap().keySet()) {
            ROOM_CELL val = map.getMap().get(point);
            if (val == null)
                continue;
            try {
                cells[point.x][point.y] = val;
            } catch (Exception e) {
                if (!CoreEngine.isMacro())
                    main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        return cells;
    }
    public static String toASCII_String(ROOM_CELL[][] cells, boolean nullToX) {
        return toASCII_String(cells, nullToX, true);
    }

    public static String toASCII_String(ROOM_CELL[][] cells, boolean nullToX
     , boolean OtoDot) {
        return toASCII_String(cells, nullToX, OtoDot, false);
    }

    public static String toASCII_String(ROOM_CELL[][] cells, boolean nullToX
     , boolean OtoDot, boolean overrideBlock) {
        if (!overrideBlock)
            if (loggingOff)
                return "";
        String string = "\n";
        String columns = "\nX     ";
        String separator = "\n      ";

        for (int x = 0; x < cells.length; x++) {
            columns += x + RngXmlMaster.TILEMAP_ROW_SEPARATOR;
            if (x < 10) columns += " ";
            separator += "___";
        }
        for (int y = 0; y < cells[0].length; y++) {
            if (y < 10)
                string += y + "  | ";
            else
                string += y + " | ";
            for (int x = 0; x < cells.length; x++) {
                if (cells[x][y] == null) {
                    if (nullToX)
                        string += "  X";
                    else
                        string += "  -";
                } else
                    string += "  " + cells[x][y].getSymbol();
            }
            string += "\n";

        }
        separator += "\n";
        if (OtoDot) {
            return (columns + separator + string + separator + columns).replace(
             "O", "."
            );
        }
        return columns + separator + string + separator + columns;
    }

    public static ROOM_CELL[][] build(LevelModel model) {
        int offsetX = -(model.getLeftMost());
        int offsetY = -(model.getTopMost());
        return build(model.getRoomMap(), offsetX, offsetY,
         model.getCurrentWidth(), model.getCurrentHeight());
    }

    public static ROOM_CELL[][] build(Map<Coordinates, Room> map, int offsetX, int offsetY,
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
                                 "" + room.getZone().getIndex());
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

    public static void setLoggingOff(boolean loggingOff) {
        TileMapper.loggingOff = loggingOff;
    }

    public static boolean isLoggingOff() {
        return loggingOff;
    }

    public TileMap joinTileMaps() {
        Map<Coordinates, ROOM_CELL> map = new XLinkedMap<>();
        for (LevelBlock block : model.getBlocks().values()) {
            for (Coordinates coordinates : block.getTileMap().getMap().keySet()) {
                map.put(coordinates
                 //                  .getOffset(new AbstractCoordinates(-model.getLeftMost(), -model.getTopMost()))
                 ,
                 block.getTileMap().getMap().get(coordinates));
            }
        }
        return new TileMap(map);
    }

    public TileMap map() {
        model.setCells(build(model));
        print(model);
        //        int offsetX = -(model.getLeftMost());
        //        int offsetY = -(model.getTopMost());
        //        for (Coordinates point : model.getRoomMap().keySet()) {
        //            RoomModel room = model.getRoomMap().get(point); //block/zone
        //            int x = point.x;
        //            int y = point.y;
        //            for (String[] column : room.getCells()) {
        //                for (String symbol : column) {
        //                    ROOM_CELL cell = ROOM_CELL.getBySymbol(symbol);
        //                    Tile tile = converter.convert(cell, room, x, y++);
        //
        //                    if (tile.getData().length == 0)
        //                        main.system.auxiliary.log.LogMaster.log(1, (x + offsetX) + "-" + (y + offsetY)
        //                         + " is empty");
        //                    else
        //                        main.system.auxiliary.log.LogMaster.log(1, (x + offsetX) + "-" + (y + offsetY)
        //                         + "= " + tile.getData()[0].getKey());
        //
        //                    try {
        //                        map.getTiles()[x + offsetX][y + offsetY] = tile;
        //                    } catch (Exception e) {
        //                        e.printStackTrace();
        //                    }
        //                }
        //                x++;
        //            }
        //        }
        //        for (ROOM_CELL[] row : model.getCells()) {
        //            //            for (String sub: )
        //        }
        return new TileMap(new XLinkedMap<>());
    }

    private void fillWithDefault(ROOM_CELL[][] cells) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                cells[i][j] = DEFAULT_CELL;
            }
        }
    }


    //data functions - transform old, save/load


}
