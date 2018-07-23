package eidolons.game.module.dungeoncrawl.generator.tilemap;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import eidolons.game.module.dungeoncrawl.generator.model.RoomModel;

import java.awt.*;

/**
 * Created by JustMe on 2/15/2018.
 */
public class TileMapper {
    private TileConverter converter;
    private LevelModel model;
    private LevelData data;
    private TileMap map;
    private ROOM_CELL DEFAULT_CELL = ROOM_CELL.WALL;

    public TileMapper(LevelModel model, LevelData data) {
        this.model = model;
        this.data = data;
        this.converter = new TileConverter(model, data);
    }

    public static void print(LevelModel model) {
        main.system.auxiliary.log.LogMaster.log(1, model.toASCII_Map());
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

    public void build(LevelModel model) {
        int offsetX = -(model.getLeftMost());
        int offsetY = -(model.getTopMost());
        ROOM_CELL[][] cells = new ROOM_CELL[model.getCurrentWidth()][model.getCurrentHeight()];
//        fillWithDefault(cells);
        for (Point point : model.getRoomMap().keySet()) {
            int x = point.x + offsetX;
            int y = point.y + offsetY;
            Room room = model.getRoomMap().get(point);
            for (String[] column : room.getCells()) {
                for (String symbol : column) {
                    ROOM_CELL cell = ROOM_CELL.getBySymbol(symbol);
                    if (cell != null)
                        try {
                            cells[x][y] = cell;
                        } catch (Exception e) {
                            //                        e.printStackTrace();
                        }
                    y++;
                }
                y = point.y + offsetY;
                x++;
            }

        }
        model.setCells(cells);
    }

    private void fillWithDefault(ROOM_CELL[][] cells) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                cells[i][j] = DEFAULT_CELL;
            }
        }
    }

    public void fill(LevelModel model, TileMap map) {
        build(model);
        print(model);
        if (true) return;
        int offsetX = -(model.getLeftMost());
        int offsetY = -(model.getTopMost());
        for (Point point : model.getRoomMap().keySet()) {
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
