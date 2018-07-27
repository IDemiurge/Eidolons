package eidolons.game.module.dungeoncrawl.generator;

import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraph;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphMaster;
import eidolons.game.module.dungeoncrawl.generator.init.RngLevelInitializer;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModelBuilder;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import eidolons.macro.map.Place;
import main.content.enums.DungeonEnums.DUNGEON_TYPE;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.Loop;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by JustMe on 2/13/2018.
 * <p>
 * Zones and blocks
 * Perhaps a logical abstract map first?
 * 30x20+ levels or so
 * split into 2-3 zones
 * each zone will have a template
 * <p>
 * this is where those 'promenade', 'great hall' things come in
 */
public class LevelGenerator {

    public static void main(String[] args) {
        Loop loop = new Loop(7);
        List<TileMap> maps = new ArrayList<>();
        List<LevelModel> models = new ArrayList<>();
        LevelGenerator generator = new LevelGenerator();
        while (true) {
            LevelData data = LevelDataMaker.generateData(SUBLEVEL_TYPE.COMMON, LOCATION_TYPE.CRYPT);
            try {
                LevelModel model = generator.generateLevelModel(data);
                models.add(model);
                TileMap map = generator.generateTileMap(model, data);
                maps.add(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (loop.ended()) {
                break;
            }
        }
        int i = 0;
        for (LevelModel sub : models) {
            main.system.auxiliary.log.LogMaster.log(1, i++ + ": \n" +
             sub.toString());
        }
    }
    public  DungeonLevel generateLevel(SUBLEVEL_TYPE sublevelType,
                                       LOCATION_TYPE locationType) {
        LevelData data = LevelDataMaker.generateData(sublevelType,  locationType);
        LevelModel model =  generateLevelModel(data);
        TileMap tileMap = generateTileMap(model, data);
        DungeonLevel level = new DungeonLevel(tileMap, model, sublevelType, locationType);

        new RngLevelInitializer().init(level);
        return level;
    }


    public LevelModel generateLevelModel(LevelData data) {
        LevelGraph graph = new LevelGraphMaster(data).buildGraph();
        main.system.auxiliary.log.LogMaster.log(1, "graph: " + graph);
        LevelModel model = new LevelModelBuilder(data).buildModel(graph);
        main.system.auxiliary.log.LogMaster.log(1, "model: " + model);
        return model;
    }

    public TileMap generateTileMap(LevelData data) {
        return generateTileMap(generateLevelModel(data), data);
    }

    public TileMap generateTileMap(LevelModel model, LevelData data) {

        TileMap map = new TileMapper(model, data).map();
        return map;
    }



    public static void generateMainLevelPool() {
        List<ObjType> placeTypes = DataManager.getTypesGroup(MACRO_OBJ_TYPES.PLACE, "");
        LevelGenerator generator = new LevelGenerator();
        List<DungeonLevel> levels = generator.generateLevelsForPlaces(placeTypes);
        for (DungeonLevel level : levels) {
            //       TODO      FileManager.write(level.toXml(), getPathForLevel(level));
            //        level.getTileMap()
        }
    }
    public static void generateLevelsForPlace(Place place) {

    }

    private List<DungeonLevel> generateLevelsForPlaces(List<ObjType> placeTypes) {
        Set<LOCATION_TYPE> subTypes = new LinkedHashSet();
        Set<DUNGEON_TYPE> types = new LinkedHashSet();
        for (ObjType placeType : placeTypes) {
            //            placeType.getProperty()
        }
        for (DUNGEON_TYPE type : types) {
            for (LOCATION_TYPE subType : subTypes) {
                LevelData data = LevelDataMaker.generateData(SUBLEVEL_TYPE.COMMON, subType );
                LevelModel model =  generateLevelModel(data);
                TileMap tileMap = generateTileMap(model, data);
                //                DungeonLevel level = new DungeonLevel(tileMap, model, type, subType);
            }
        }


        return null;
    }
    //    public static void generate(Mission currentMission, Level level) {
    //        Dungeon dungeon = level.getDungeon();
    //        TileMap map = generateTileMap(getLevelData(0));
    ////        DungeonPlan plan = new DungeonPlan(null, dungeon);
    ////        plan.setObjMap(map.getObjMap());
    ////        dungeon.setPlan(plan);
    //
    //        new LE_ObjMaster().initTileMap(map.getTiles());
    //
    //        LevelEditor.getSimulation().getUnits().clear();
    //        LevelEditor.getSimulation().getBfObjects().clear();
    //        level.init();
    ////        level.setMap(new DungeonMapGenerator().generateMap(
    ////         initBuildParams(true, alt, dungeon), dungeon));
    ////        level.init();
    //    }
}
