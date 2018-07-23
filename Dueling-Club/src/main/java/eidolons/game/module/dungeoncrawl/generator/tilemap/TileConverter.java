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
import main.system.auxiliary.StringMaster;
import main.system.datatypes.WeightMap;
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

    private String getWall(RoomModel room, int x, int y) {
        String wallType = "Stone Wall"; //from block/zone!!!
        return wallType;
    }


    private Pair<String, OBJ_TYPE>[] getBfObjPair(String... names) {
        String string = StringMaster.join(";", names);
        return new Pair[]{
         new ImmutablePair(string, DC_TYPE.BF_OBJ)
        };
    }

    private String getArtObj(LevelBlock block, int x, int y) {
        DUNGEON_STYLE style = getStyle(metaData.getSubdungeonType(), block.getRoomType());

        String pool = RngConstProvider.getWeightMap(ROOM_CELL.ART_OBJ, style);
        String pick = null;
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

    private DUNGEON_STYLE getStyle(SUBDUNGEON_TYPE subdungeonType, ROOM_TYPE roomType) {
        switch (subdungeonType) {
            case CAVE:
                break;
            case HIVE:
                break;
            case DUNGEON:
                break;
            case CASTLE:
                break;
            case SEWER:
                break;
            case HELL:
                break;
            case ASTRAL:
                break;
            case ARCANE:
                break;
            case CRYPT:
                break;
            case DEN:
                break;
            case BARROW:
                break;
            case RUIN:
                break;
            case HOUSE:
                break;
        }
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
            case DESTRUCTIBLE:
                break;

            case WALL:
            case DESTRUCTIBLE_WALL:
                return new Tile(getBfObjPair(getWall(room, x, y)));
            case ENTRANCE:
            case EXIT:
                return new Tile(getBfObjPair(getExitObj(block, x, y)));
            case CONTAINER:
            case SPECIAL_CONTAINER:
                return new Tile(getBfObjPair(getContainerObj(block, x, y)));
            case DOOR:
            case SECRET_DOOR:
                return new Tile(getBfObjPair(getDoor(block, x, y)));
            case ART_OBJ:
            case SPECIAL_ART_OBJ:
                return new Tile(getBfObjPair(getArtObj(block, x, y)));
            case GUARD:
                //                return new Tile(getGuard(block, x, y));
            case LIGHT_EMITTER:
                return new Tile(getBfObjPair(getLightEmitter(block, x, y)));
            case WALL_WITH_LIGHT_OVERLAY:
                return new Tile(getBfObjPair(
                 getWall(room, x, y), getLightEmitterOverlaying(block, x, y)));
            case WALL_WITH_DECOR_OVERLAY:
                return new Tile(getBfObjPair(
                 getWall(room, x, y), getDecorOverlaying(block, x, y)));

        }
        return new Tile();
    }

    private String getDecorOverlaying(LevelBlock block, int x, int y) {
        return null;
    }

    private String getLightEmitterOverlaying(LevelBlock block, int x, int y) {
        return null;
    }

    private String getDoor(LevelBlock block, int x, int y) {
        return null;
    }

    private String getContainerObj(LevelBlock block, int x, int y) {
        return null;
    }

    private String getExitObj(LevelBlock block, int x, int y) {
        return null;
    }

    private String getLightEmitter(LevelBlock block, int x, int y) {
        DUNGEON_STYLE style=block. getStyle();
        WeightMap<String> map = new WeightMap<>();
        switch (style) {

            case Brimstone:
                break;
            case Survivor:
                break;
            case DarkElegance:
                break;
            case Grimy:
                break;
            case Castle:
//               map.put(BF_OBJ_TYPES_LIGHT_EMITTERS.BRAZIER.getName(), 5);
                break;
        }
        return map.toString();
    }

    public enum DUNGEON_STYLE {
        Brimstone,
        Survivor,
         DarkElegance,
        Grimy,
         Castle,
        }


}
