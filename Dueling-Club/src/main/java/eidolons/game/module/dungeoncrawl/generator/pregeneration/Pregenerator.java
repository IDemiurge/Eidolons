package eidolons.game.module.dungeoncrawl.generator.pregeneration;

import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.LevelDataMaker;
import eidolons.game.module.dungeoncrawl.generator.LevelGenerator;
import eidolons.game.module.dungeoncrawl.generator.LevelValidator;
import eidolons.game.module.dungeoncrawl.generator.pregeneration.PregeneratorData.PREGENERATOR_VALUES;
import eidolons.game.module.dungeoncrawl.generator.test.GenerationStats;
import eidolons.game.module.dungeoncrawl.generator.test.LevelStats;
import eidolons.macro.generation.ScenarioGenerator;
import eidolons.system.text.NameMaster;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.FileManager;
import main.system.data.DataUnit;
import main.system.data.DataUnitFactory;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 8/17/2018.
 */
public class Pregenerator implements Runnable {


    public static final SUBLEVEL_TYPE[][] GENERATED_SUBLEVELS = {
     {SUBLEVEL_TYPE.COMMON,},
     {SUBLEVEL_TYPE.PRE_BOSS,},
     {SUBLEVEL_TYPE.BOSS,}
    };


    public static final LOCATION_TYPE[] GENERATED_LOCATIONS = LOCATION_TYPE.values();
    private static final int THREADS = 3;
    private static List<Pregenerator> running = new ArrayList<>();
    private static List<GenerationStats> analysisStats = new ArrayList<>();
    private PregeneratorData data;
    private List<DungeonLevel> generated = new ArrayList<>();

    public Pregenerator(PregeneratorData data) {
        this.data = data;
    }

    public static void main(String[] args) {
        //logging off - or into a file...
        for (int i = 0; i < THREADS; i++) {
            Pregenerator instance;
            running.add(instance = new Pregenerator(Pregenerator.getData(i)));
            new Thread(instance, " thread").start();
        }
        WaitMaster.waitForInput(WAIT_OPERATIONS.GAME_FINISHED);
        printAnalysis();
    }

    private static void printAnalysis() {
        //to file too...
    }

    private static PregeneratorData getData(int i) {
        DataUnitFactory<DataUnit<PREGENERATOR_VALUES>> factory = new DataUnitFactory();
        factory.setValueNames(PregeneratorData.PREGENERATOR_VALUES.values());
        //       TODO  factory.setValues(vals);

        SUBLEVEL_TYPE[] sublevelTypes = GENERATED_SUBLEVELS[i];
        LOCATION_TYPE[] locationTypes = GENERATED_LOCATIONS;
        return new PregeneratorData(factory.constructDataString(),
         sublevelTypes, locationTypes);
    }

    @Override
    public void run() {
        GenerationStats stats = new GenerationStats();
        LevelValidator validator = new LevelValidator();
        for (SUBLEVEL_TYPE type : data.sublevelTypes) {
            for (LOCATION_TYPE locationType : data.locationTypes) {
                while (true) {
                    DungeonLevel level = generateLevel(locationType, type);
                    int rate; //average?
                    //special validation
                    //                    validator.validateModel()
                    addToStats(level, stats);
                    //                    level.getModel()
                    validator.getStats();
                    saveLevel(level);
                }

            }
        }
        //        logResults(stats);
        analysisStats.add(stats);
        running.remove(this);
        if (running.isEmpty()) {
            WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_FINISHED, true);
        }
    }

    private void addToStats(DungeonLevel level, GenerationStats stats) {
    }

    private DungeonLevel generateLevel(LOCATION_TYPE locationType, SUBLEVEL_TYPE type) {
        int tries = data.getIntValue(PregeneratorData.PREGENERATOR_VALUES.ATTEMPTS);
        LevelGenerator generator = new LevelGenerator(tries);

        LevelStats stats = new LevelStats();

        LevelData data = LevelDataMaker.generateData(type, locationType);
        //change data?
        DungeonLevel level = null;
        try {
            level = generator.generateLevel(data);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            error(locationType, type);
            return level;
        }

        return level;

    }

    private void logResults() {

        main.system.auxiliary.log.LogMaster.log(1, " ");
    }

    private void saveLevel(DungeonLevel level) {

        generated.add(level);
        String stringData = level.toXml();
        String name = ScenarioGenerator.getLevelName(level.getLocationType(), level.getSublevelType()) + ".xml";
        name = NameMaster.getUniqueVersionedFileName(name,
         StrPathBuilder.build(PathFinder.getRandomLevelPath(), level.getSublevelType().name()));

        String path = StrPathBuilder.build(getPath(level.getLocationType()), name);
        FileManager.write(stringData, path);

    }


    private void error(LOCATION_TYPE locationType, SUBLEVEL_TYPE type) {
    }

    private String getPath(LOCATION_TYPE locationType) {
        return StrPathBuilder.build(PathFinder.getRandomLevelPath(),
         "pregenerated", locationType.name());
    }


    public void generateMainLevelPool() {


    }
}
