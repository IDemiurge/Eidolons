package eidolons.game.module.dungeoncrawl.generator;

import eidolons.content.PROPS;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.generator.fill.RngFillMaster;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraph;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphMaster;
import eidolons.game.module.dungeoncrawl.generator.init.RngLevelInitializer;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModelBuilder;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMap;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import eidolons.macro.generation.ScenarioGenerator;
import eidolons.macro.map.Place;
import main.content.enums.DungeonEnums.DUNGEON_TYPE;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.Loop;
import main.system.auxiliary.data.FileManager;

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

    public static final boolean TEST_MODE = true;
    private static final boolean REAL = false;

    public static void main(String[] args) {
        if (REAL){
            realGeneration();
            return;
        }
        Loop loop = new Loop(5);
//        KotlinTestKt.max()
        List<TileMap> maps = new ArrayList<>();
        List<LevelModel> models = new ArrayList<>();
        LevelGenerator generator = new LevelGenerator();
        while (loop.continues()) {
            LevelData data = LevelDataMaker.generateData(SUBLEVEL_TYPE.COMMON, LOCATION_TYPE.CRYPT);
            try {
                LevelModel model = generator.generateLevelModel(data);
                models.add(model);
                TileMap map = generator.generateTileMap(model, data);
                maps.add(map);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        int i = 0;
        for (LevelModel model : models) {
            main.system.auxiliary.log.LogMaster.log(1, i++ + ": \n" +
             model.toString());
        }
        for (int j = 0; j < 10; j++) {
            main.system.auxiliary.log.LogMaster.log(1," " );
        }
        for (LevelModel model : models) {
            RngFillMaster.fill(model,model.getData());
            main.system.auxiliary.log.LogMaster.log(1, i++ + ": \n" +
             model.toString());

        }

//        realGeneration();
    }

    private static void realGeneration() {
        DC_Engine.mainMenuInit();
        DC_Engine.dataInit(true);
//        DataManager.getTypesSubGroup()
        ObjType placeType = DataManager.getType("Dungeon", MACRO_OBJ_TYPES.PLACE);
        ObjType scenarioType = ScenarioGenerator.generateRandomLevelScenario(placeType);

        int i = 0;
        for (String path : ContainerUtils.open(scenarioType.getProperty(PROPS.SCENARIO_MISSIONS))) {
            String contents =
             PathFinder.getRandomLevelPath()+ FileManager.readFile(path);
            main.system.auxiliary.log.LogMaster.log(1, i++ + ": \n" +contents);

        }
    }

    public  DungeonLevel generateLevel(SUBLEVEL_TYPE sublevelType,
                                       LOCATION_TYPE locationType) {
        LevelData data = LevelDataMaker.generateData(sublevelType,  locationType);
        return generateLevel(data);
    }

        public  DungeonLevel generateLevel(LevelData data) {
        LevelModel model =  generateLevelModel(data);
        TileMap tileMap = generateTileMap(model, data);
        DungeonLevel level = new DungeonLevel(tileMap, model, data.getSublevelType(), data.getLocationType());

        RngFillMaster.fill(model,data);

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

    public static DungeonLevel generateForData(LevelData data) {
     return new LevelGenerator().generateLevel(data);
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
