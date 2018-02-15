package main.game.battlecraft.logic.dungeon.generator.tilemap;

import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.game.battlecraft.logic.dungeon.generator.GeneratorEnums.ROOM_CELL;
import main.game.battlecraft.logic.dungeon.generator.LevelData;
import main.game.battlecraft.logic.dungeon.generator.model.LevelModel;
import main.game.battlecraft.logic.dungeon.generator.model.RoomModel;
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

    public TileMap map(){
        //merge
          map= new TileMap(model.getCurrentWidth(), model.getCurrentHeight());
//        build(model);
        fill(model, map);
        return map;
    }

    public TileMap createLevel(){
        map();
//        DungeonPlan plan = new DungeonPlan(null , dungeon);
//        pla.
//         new Level
        return null;
    }
    private void fill(LevelModel model, TileMap map) {
        int offsetX = -(model.getLeftMost().x);
        int offsetY = -(model.getTopMost().y);
        for (Point point : model.getModelMap().keySet()) {
            RoomModel room = model.getModelMap().get(point);
            int x= point.x;
            int y= point.y;
            for (String[] row : room.getCells()) {
                for (String symbol : row) {
                    ROOM_CELL cell = ROOM_CELL.getBySymbol(symbol);
                     Tile tile =convert(cell, room, x++, y);
                     map.getTiles()[x+offsetX][y+offsetY ] = tile;
                }
                y++;
            }
        }
            for (ROOM_CELL[] row : model.getCells()) {
//            for (String sub: )
        }
    }

    private Tile convert(ROOM_CELL cell, RoomModel room, int x, int y) {
        if (cell==ROOM_CELL.FLOOR)
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

    private Pair<String,OBJ_TYPE>[] getWall(RoomModel room, int x, int y) {
        String wallType="Stone Wall"; //from block/zone!!!
        return new Pair[]{
         new ImmutablePair(wallType, DC_TYPE.BF_OBJ)
        };
    }


    private void build(LevelModel model) {
        int offsetX = -(model.getLeftMost().x);
        int offsetY = -(model.getTopMost().y);
        ROOM_CELL[][] cells= new ROOM_CELL[model.getCurrentWidth()][model.getCurrentHeight()];
        for (Point point:     model.getModelMap().keySet()){
        int x = point.x+offsetX;
        int y = point.y+offsetY;
        RoomModel room = model.getModelMap().get(point);
        for (String[] row : room.getCells()) {
            for (String symbol: row) {
                ROOM_CELL cell = ROOM_CELL.getBySymbol(symbol);
                cells[x][y] = cell;
                x++;
            }
            y++;
        }

    }
        model.setCells(cells);
    }

    //data functions - transform old, save/load




}
