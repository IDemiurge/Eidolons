package eidolons.game.module.dungeoncrawl.generator.tilemap;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.LevelBlock;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.init.RngConstProvider;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.model.RoomModel;
import main.content.DC_TYPE;
import main.content.OBJ_TYPE;
import main.content.enums.DungeonEnums.SUBDUNGEON_TYPE;
import main.entity.type.ObjAtCoordinate;
import main.game.bf.Coordinates;
import main.system.StreamMaster;
import main.system.auxiliary.RandomWizard;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 7/20/2018.
 */
public class TileConverter {

    private final LevelModel model;
    private final LevelData metaData;

    public TileConverter(LevelModel model, LevelData metaData) {
        this.metaData = metaData;
        this.model = model;
    }

    private Pair<String, OBJ_TYPE>[] getWall(RoomModel room, int x, int y) {
        String wallType = "Stone Wall"; //from block/zone!!!
        return getBfObjPair(wallType);
    }
 

    private Pair<String, OBJ_TYPE>[] getBfObjPair(String name) {
        return new Pair[]{
         new ImmutablePair(name, DC_TYPE.BF_OBJ)
        };
    }
public enum DECOR_STYLE{

}
    private String getArtObj(LevelBlock block, int x, int y) {
        DECOR_STYLE style = getStyle(metaData.getSubdungeonType(), block.getRoomType());

        String pool = RngConstProvider.getWeightMap(ROOM_CELL.ART_OBJ,style);
        String pick =null;
        Coordinates c = new Coordinates(x, y);
        while (!checkObj(c, block, pool,
         pick = new RandomWizard<String>().getObjectByWeight(pool, String.class))) {

        }
        //check proximity
        //randomize 
        
        switch (metaData.getSubdungeonType()) {
            
        }

        return null;
    }

    private DECOR_STYLE getStyle(SUBDUNGEON_TYPE subdungeonType, ROOM_TYPE roomType) {
        return null;
    }

    private boolean checkObj(Coordinates c, LevelBlock block, String pool, String pick) {
        List<ObjAtCoordinate> objects = new ArrayList<>(block.getObjects());
        List<ObjAtCoordinate> adjacent =
        new StreamMaster<ObjAtCoordinate>().filter(objects,
         (object) -> object.getCoordinates().isAdjacent(c));



        return false;
    }

    public Tile convert(ROOM_CELL cell, RoomModel room, int x, int y) {
        if (cell == ROOM_CELL.FLOOR)
            return new Tile();
        LevelBlock block = model.getBlocks().get(room);
        switch (cell) {
            case WALL:
                return new Tile(getWall(room, x, y));
            case FLOOR:
                break;
            case ENTRANCE:
                break;
            case EXIT:
                break;
            case CONTAINER:
//                return new Tile(getContainer(room, x, y));
            case DOOR:
//                return new Tile(getDoor(room, x, y));
            case ART_OBJ:
                return new Tile(getBfObjPair(getArtObj(block, x, y)));
            case DESTRUCTIBLE_WALL:
                break;
            case SECRET_DOOR:
                break;
            case TRAP:
                break;
            case GUARD:
                //                return new Tile(getGuard(room, x, y));
            case LIGHT_EMITTER:
                //                return new Tile(getLightEmitter(room, x, y));
            case WALL_WITH_LIGHT_OVERLAY:
                break;
            case WALL_WITH_DECOR_OVERLAY:
                break;
            case LOCAL_KEY:
                break;
            case GLOBAL_KEY:
                break;
            case DESTRUCTIBLE:
                break;
            case SPECIAL_CONTAINER:
                break;
            case SPECIAL_DOOR:
                break;
            case SPECIAL_ART_OBJ:
                break;
        }
        return new Tile();
    }



}
