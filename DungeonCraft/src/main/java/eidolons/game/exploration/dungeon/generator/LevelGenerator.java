package eidolons.game.exploration.dungeon.generator;

import eidolons.game.exploration.dungeon.generator.graph.LevelGraph;
import eidolons.game.exploration.dungeon.generator.graph.LevelGraphMaster;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.exploration.dungeon.struct.DungeonLevel;
import eidolons.game.exploration.dungeon.generator.fill.RngFillMaster;
import eidolons.game.exploration.dungeon.generator.init.RngLevelInitializer;
import eidolons.game.exploration.dungeon.generator.init.RngMainSpawner;
import eidolons.game.exploration.dungeon.generator.level.BlockCreator;
import eidolons.game.exploration.dungeon.generator.model.LevelModel;
import eidolons.game.exploration.dungeon.generator.model.LevelModelBuilder;
import eidolons.game.exploration.dungeon.generator.model.ModelFinalizer;
import eidolons.game.exploration.dungeon.generator.model.Traverser;
import eidolons.game.exploration.dungeon.generator.pregeneration.Pregenerator;
import eidolons.game.exploration.dungeon.generator.tilemap.TileMapper;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.system.auxiliary.Loop;
import main.system.auxiliary.log.LogMaster;
import main.system.math.PositionMaster;

import java.util.ArrayList;
import java.util.List;

import static main.system.auxiliary.log.LogMaster.log;

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

    public static final boolean TEST_MODE = false;
    public static final boolean REAL = true;
    public static final boolean LOGGING_OFF = false;
    private static final int TRIES = 100;
    public static LOCATION_TYPE TEST_LOCATION_TYPE = LOCATION_TYPE.CEMETERY;
    public static LOCATION_TYPE[] TEST_LOCATION_TYPES = {
     LOCATION_TYPE.CEMETERY,
     LOCATION_TYPE.CRYPT,
     LOCATION_TYPE.TOWER,
     LOCATION_TYPE.DUNGEON,
     LOCATION_TYPE.TEMPLE,
     LOCATION_TYPE.CASTLE,
    };
    private int maxTries;
    private LevelModelBuilder builder;
    private LevelValidator validator;

    public LevelGenerator(int maxTries) {
        this.maxTries = maxTries;
        validator = new LevelValidator();
    }

    public LevelGenerator() {
        this(Integer.MAX_VALUE);
    }

    public static void main(String[] args) {
        TileMapper.setLoggingOff(TEST_MODE);
        LogMaster.setOff(true);
        realGeneration();

    }

    private static void scenarioTypeGeneration() {
        //        ObjType placeType = DataManager.getType(REAL_TEST_PLACE_TYPE_NAME, MACRO_OBJ_TYPES.PLACE);
        //        ScenarioGenerator.generateRandomLevelScenario(
        //         TEST_MODE ? 1 : 10, placeType);
    }

    private static void realGeneration() {
        DC_Engine.mainMenuInit();
        DC_Engine.dataInit(true);
        //        DataManager.getTypesSubGroup()
        List<DungeonLevel> levels = new ArrayList<>();
        for (LOCATION_TYPE type : TEST_LOCATION_TYPES) {
            TEST_LOCATION_TYPE = type; // java sucks
            try {
                LevelData data = LevelDataMaker.generateData(SUBLEVEL_TYPE.COMMON, type);
                DungeonLevel level = new LevelGenerator(TRIES).generateLevel(
                 data, false);
                levels.add(level);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }


        }
        for (DungeonLevel level : levels) {
            main.system.auxiliary.log.LogMaster.log(1, "\n" +
             "\n" +
             level.getLocationType() +
             "\n " + level);

        }
    }


    public static DungeonLevel generateForData(LevelData data) {
        return new LevelGenerator().generateLevel(data);
    }

    public DungeonLevel generateLevel(SUBLEVEL_TYPE sublevelType,
                                      LOCATION_TYPE locationType) {
        LevelData data = LevelDataMaker.generateData(sublevelType, locationType);
        return generateLevel(data);
    }

    public DungeonLevel generateLevel(LevelData data) {
        return generateLevel(data, false);
    }

    public DungeonLevel generateLevel(LevelData data, boolean allowInvalid) {
        Loop loop = new Loop(maxTries);
        while (loop.continues()) {
            try {
                LevelModel model = generateLevelModel(data);
                PositionMaster.initDistancesCache(null , model.getCurrentWidth(), model.getCurrentHeight());
                new BlockCreator().createBlocks(model);
                RngFillMaster.fill(model, data);
                for (int i = 0; i < data.getIntValue(GeneratorEnums.LEVEL_VALUES.ADDITIONAL_FILL_RUNS); i++) {
                    RngFillMaster.fill(model, data);
                }
                if (data.isNatural())
                    ModelFinalizer.randomizeEdges(model);
                DungeonLevel level = new DungeonLevel(model, data.getSublevelType(), data.getLocationType());
                if (data.isInitializeRequired())
                    new RngLevelInitializer().init(level);

                if (RngMainSpawner.TEST_MODE){
                    new RngMainSpawner().spawn(level);
                }

                if (isCheckTraverse())
                    validator.setTraverser(new Traverser(builder.getNodeModelMap(),
                     model, builder.getGraph(), builder.getEdgeMap()));

                if (!allowInvalid)
                    if (!new LevelValidator().isLevelValid(level))
                        continue;

                return level;
            } catch (Exception e) {
                if (Pregenerator.TEST_MODE)
                    main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        throw new RuntimeException();
    }

    public LevelModel generateLevelModel(LevelData data) {
        LevelGraph graph = new LevelGraphMaster(data).buildGraph();
        log(1, "graph: " + graph);
        LevelModel model = (builder = new LevelModelBuilder(data)).buildModel(graph);
        log(1, "model: " + model);
        return model;
    }

    private boolean isCheckTraverse() {
        return true;
    }


    public boolean validate(LevelModel model) {
        return validator.validateModel(builder.getGraph(), model);
    }

    public LevelValidator getValidator() {
        return validator;
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
