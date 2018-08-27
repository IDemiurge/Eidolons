package eidolons.game.module.dungeoncrawl.generator.pregeneration;

import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.LevelDataMaker;
import eidolons.game.module.dungeoncrawl.generator.LevelGenerator;
import eidolons.game.module.dungeoncrawl.generator.LevelValidator;
import eidolons.game.module.dungeoncrawl.generator.pregeneration.PregeneratorData.PREGENERATOR_VALUES;
import eidolons.game.module.dungeoncrawl.generator.test.GenerationStats;
import eidolons.game.module.dungeoncrawl.generator.test.GenerationStats.GEN_STAT;
import eidolons.game.module.dungeoncrawl.generator.test.LevelStats;
import eidolons.game.module.dungeoncrawl.generator.test.LevelStats.LEVEL_STAT;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static eidolons.game.module.dungeoncrawl.generator.LevelGenerator.TEST_LOCATION_TYPES;
import static main.system.auxiliary.log.LogMaster.log;
import static main.system.auxiliary.log.LogMaster.setOff;

/**
 * Created by JustMe on 8/17/2018.
 */
public class Pregenerator implements Runnable {


    public static final SUBLEVEL_TYPE[][] GENERATED_SUBLEVELS = {
     {SUBLEVEL_TYPE.COMMON,},
     {SUBLEVEL_TYPE.PRE_BOSS,},
     {SUBLEVEL_TYPE.BOSS,}
    };

    public static final boolean TEST_MODE = true;
    public static final LOCATION_TYPE[] GENERATED_LOCATIONS = TEST_LOCATION_TYPES;
    public static final GEN_STAT[] averageValues = {

    };
    private static final int THREADS = 3;
    private static final float RATE_MIN_DEFAULT = 100;
    private static List<Pregenerator> running = new ArrayList<>();
    private static List<GenerationStats> analysisStats = new ArrayList<>();
    private PregeneratorData data;
    private List<DungeonLevel> generated = new ArrayList<>();
    private List<DungeonLevel> successful = new ArrayList<>();
    private LevelGenerator generator;
    private GenerationStats stats;

    public Pregenerator(PregeneratorData data) {
        this.data = data;
    }

    public static void main(String[] args) {
        //logging off - or into a file...
        setOff(!LevelGenerator.TEST_MODE);
        TileMapper.setLoggingOff(true);
        if (TEST_MODE) {
            new Pregenerator(Pregenerator.getData(0)).run();
        } else {
            for (int i = 0; i < THREADS; i++) {
                Pregenerator instance;
                running.add(instance = new Pregenerator(Pregenerator.getData(i)));
                new Thread(instance, " thread").start();
            }
            WaitMaster.waitForInput(WAIT_OPERATIONS.GAME_FINISHED);
        }
        printAnalysis();
    }

    private static void printAnalysis() {
        //to file too...
    }

    private static PregeneratorData getData(int i) {
        DataUnitFactory<DataUnit<PREGENERATOR_VALUES>> factory = new DataUnitFactory();
        factory.setValueNames(PregeneratorData.PREGENERATOR_VALUES.values());

        String[] vals = Arrays.stream(PREGENERATOR_VALUES.values()).map(v -> v.getDefaultValue() + "").collect(Collectors.toList())
         .toArray(new String[PREGENERATOR_VALUES.values().length]);
        factory.setValues(vals);

        SUBLEVEL_TYPE[] sublevelTypes = GENERATED_SUBLEVELS[i];
        LOCATION_TYPE[] locationTypes = GENERATED_LOCATIONS;
        return new PregeneratorData(factory.constructDataString(),
         sublevelTypes, locationTypes);
    }

    @Override
    public void run() {

        for (SUBLEVEL_TYPE type : data.sublevelTypes) {
            for (LOCATION_TYPE locationType : data.locationTypes) {
                stats = new GenerationStats(locationType, type);
                successful = new ArrayList<>();
                generated = new ArrayList<>();
                while (true) { //TODO max failed  // time
                    DungeonLevel level = generateLevel(locationType, type);
                    generated.add(level);
                    LevelStats levelStats = new LevelStats(level);
                    LevelValidator validator = generator.getValidator();
                    validator.setStats(levelStats);
                    if (validator.isLevelValid(level))
                        if (checkRate(level)) {
                            successful.add(level);
                            saveLevel(level);
                        }
                    addToStats(level, stats, levelStats);
                    if (successful.size() >= data.getIntValue(
                     PREGENERATOR_VALUES.LEVELS_REQUIRED))
                        break;
                }

                stats.setValue(GEN_STAT.SUCCESS_RATE, ""+(successful.size() * 100 / generated.size()));
                analysisStats.add(stats);
                //successMap.put(locationType)
            }
        }
        //        logResults(stats);

        logResults();
        running.remove(this);
        if (running.isEmpty()) {
            WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_FINISHED, true);
        }
    }

    private boolean checkRate(DungeonLevel level) {
        float rate = new LevelRater(level).rateLevel();
        level.setRate(rate);
        return level.getRate() >= getMinRate();
    }

    private float getMinRate() {
        return data.getIntValue(PREGENERATOR_VALUES.MIN_RATING);
    }

    private void addToStats(DungeonLevel level, GenerationStats stats, LevelStats levelStats) {

        //check max min
        String failReason = levelStats.getValue(LEVEL_STAT.FAIL_REASON);
        stats.addCount(GEN_STAT.FAIL_REASONS, failReason);

        for (GEN_STAT item : averageValues) {
            //            stats.addAverage(val, value);
            //fill, rate, quality, ...
            int val = getVal(item, level, stats);
        }
    }

    private int getVal(GEN_STAT item, DungeonLevel level, GenerationStats stats) {
        switch (item) {
            case AVRG_RATE:
                return (int) level.getRate();
        }
        return 0;
    }

    private DungeonLevel generateLevel(LOCATION_TYPE locationType, SUBLEVEL_TYPE type) {
        int tries = data.getIntValue(PREGENERATOR_VALUES.MAX_ATTEMPTS_PER_LEVEL);
        generator = new LevelGenerator(tries);

        LevelData data = LevelDataMaker.generateData(type, locationType);
        //change data?
        DungeonLevel level = null;
        try {
            level = generator.generateLevel(data, true);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            error(locationType, type);
            return level;
        }

        return level;

    }

    private void logResults() {
        String text = "Generation with data: \n";
        text += data.toString() + "\n";

        for (GenerationStats stat : analysisStats) {
            text += stat.toString() + "\n\n\n";
        }
        String path = StrPathBuilder.build(PathFinder.getRandomLevelPath(),
         "pregenerated", "reports");
        String name = NameMaster.getUniqueVersionedFileName("report ",
         path);
        FileManager.write(text, StrPathBuilder.build(path,name));
        log(1, " "+text);
        //pregen data
    }

    private void saveLevel(DungeonLevel level) {

        String stringData = level.toXml();
        String name = ScenarioGenerator.getLevelName(level.getLocationType(), level.getSublevelType()) + ".xml";

        name = NameMaster.getUniqueVersionedFileName(name, getPath(level.getLocationType()));

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
