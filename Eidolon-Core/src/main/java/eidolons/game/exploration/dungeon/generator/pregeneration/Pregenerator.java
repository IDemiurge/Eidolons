package eidolons.game.exploration.dungeon.generator.pregeneration;

import eidolons.game.exploration.dungeon.generator.GeneratorEnums;
import eidolons.game.exploration.dungeon.generator.LevelData;
import eidolons.game.exploration.dungeon.generator.LevelDataMaker;
import eidolons.game.exploration.dungeon.generator.LevelGenerator;
import eidolons.game.exploration.dungeon.generator.LevelValidator;
import eidolons.game.exploration.dungeon.generator.test.GenerationStats;
import eidolons.game.exploration.dungeon.generator.test.LevelStats;
import eidolons.game.exploration.dungeon.struct.DungeonLevel;
import eidolons.game.exploration.dungeon.generator.tilemap.TileMapper;
import eidolons.system.text.NameMaster;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.data.DataUnit;
import main.system.data.DataUnitFactory;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 8/17/2018.
 */
public class Pregenerator implements Runnable {

    public static final String customPrefix = "overfill ";

    public static final SUBLEVEL_TYPE[] GENERATED_SUBLEVELS_ALL = {
     SUBLEVEL_TYPE.COMMON,
//     SUBLEVEL_TYPE.PRE_BOSS,
//     SUBLEVEL_TYPE.BOSS,
    };
    public static final SUBLEVEL_TYPE[][] GENERATED_SUBLEVELS = {
     {SUBLEVEL_TYPE.COMMON,},
     {SUBLEVEL_TYPE.PRE_BOSS,},
     {SUBLEVEL_TYPE.BOSS,}
    };
    public static final boolean TEST_MODE = false;
    public static final boolean NO_VALIDATION = false;
    public static final GenerationStats.GEN_STAT[] averageValues = {
     GenerationStats.GEN_STAT.AVRG_RATE,
     GenerationStats.GEN_STAT.AVRG_FILL_RATIO,
     //     AVRG_EXITS_DISTANCE,
    };
    public static final LOCATION_TYPE[][] GENERATED_LOCATIONS = {
     {
//             LOCATION_TYPE.CASTLE,
//      LOCATION_TYPE.CRYPT,
      LOCATION_TYPE.CAVE,
//      LOCATION_TYPE.CEMETERY,
//      LOCATION_TYPE.TOWER,
     },
     {
//      LOCATION_TYPE.CASTLE,
//             LOCATION_TYPE.TOWER,
//      LOCATION_TYPE.DUNGEON,
//      LOCATION_TYPE.TEMPLE,
//      LOCATION_TYPE.CEMETERY,
     },
    };
    private static final int THREADS = 1;
    private static final int LEVELS_TO_GENERATE_PER_TYPE = 10;
    private static final Boolean RANDOM = null;
    private static final boolean DATA_RANDOMIZATION_ON = false;
    private static final boolean KEEP_STATS = true;
    private static final boolean RANDOM_ORDER = true;
    private static final boolean CUSTOM_REQS = true;
    public static final String CUSTOM_REQS_maxRooms = "11";
    public static final String CUSTOM_REQS_minRooms = "8";

    private static List<Pregenerator> running = new ArrayList<>();
    private static final Map<PregeneratorData, List<GenerationStats>> analysisStats = new XLinkedMap<>();
    private final PregeneratorData data;
    private int generated;
    private int valid;
    private Map<String, List<DungeonLevel>> successful = new HashMap<>();
    private LevelGenerator generator;
    private GenerationStats stats;
    private float randomizationMod;

    public Pregenerator(PregeneratorData data) {
        this.data = data;
    }

    public static void main(String[] args) {
        //logging off - or into a file...
        LogMaster.setOff(true);
        if (!TEST_MODE)
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

        StringBuilder textBuilder = new StringBuilder("Generation analysis: \n");
        for (PregeneratorData data : analysisStats.keySet())
            for (GenerationStats stat : analysisStats.get(data)) {
                textBuilder.append("Generation with data: \n");
                textBuilder.append(data.toString()).append("\n");
                textBuilder.append(stat.toString()).append("\n\n\n");
            }
        String text = textBuilder.toString();

        String path = StrPathBuilder.build(PathFinder.getRandomLevelPath(),
         "pregenerated", "reports");
        String name = NameMaster.getUniqueVersionedFileName("report.txt",
         path);
        FileManager.write(text, StrPathBuilder.build(path, name), true);
        System.out.println(" " + text);
        //pregen data
    }

    private static PregeneratorData getData(int i) {
        DataUnitFactory<DataUnit<PregeneratorData.PREGENERATOR_VALUES>> factory = new DataUnitFactory();
        factory.setValueNames(PregeneratorData.PREGENERATOR_VALUES.values());

        String[] vals = Arrays.stream(PregeneratorData.PREGENERATOR_VALUES.values()).map(v -> v.getDefaultValue() + "").collect(Collectors.toList())
         .toArray(new String[PregeneratorData.PREGENERATOR_VALUES.values().length]);
        factory.setValues(vals);

        LOCATION_TYPE[] locationTypes = GENERATED_LOCATIONS[i];
        PregeneratorData data = new PregeneratorData(factory.constructDataString(),
                GENERATED_SUBLEVELS_ALL, locationTypes);
        data.setValue(PregeneratorData.PREGENERATOR_VALUES.LEVELS_REQUIRED,
         "" + LEVELS_TO_GENERATE_PER_TYPE);
        return data;
    }

    public static boolean isRunning() {
        return running.size() > 0;
    }

    public static List<Pregenerator> getRunning() {
        return running;
    }

    public static void setRunning(List<Pregenerator> running) {
        Pregenerator.running = running;
    }

    private void logResults(GenerationStats stat) {
        String text = "Generation with data: \n";
        text += data.toString() + "\n";
        text += stat.toString() + "\n\n\n";

        String path = StrPathBuilder.build(PathFinder.getRandomLevelPath(),
         "pregenerated", "reports");
        String name = NameMaster.getUniqueVersionedFileName("single report.txt",
         path);
        FileManager.write(text, StrPathBuilder.build(path, name), true);
        System.out.println(" " + text);
        //pregen data
    }

    @Override
    public void run() {
        successful = new HashMap<>();
        randomizationMod = data.getFloatValue(
         PregeneratorData.PREGENERATOR_VALUES.RANDOMIZATION_MOD) / 100;

        List<SUBLEVEL_TYPE> sublevelTypes = new ArrayList<>(Arrays.asList(data.sublevelTypes));
        List<LOCATION_TYPE> locationTypes = new ArrayList<>(Arrays.asList(data.locationTypes));
        if (RANDOM_ORDER) {
            for (SUBLEVEL_TYPE type : sublevelTypes) {
                for (LOCATION_TYPE locationType : locationTypes) {
                    successful.put(getKey(type, locationType), new ArrayList<>());
                }
            }
        }
        while (true) {
            int i = RandomWizard.getRandomIndex(sublevelTypes);
            SUBLEVEL_TYPE type = sublevelTypes.get(i);
            i = RandomWizard.getRandomIndex(locationTypes);
            LOCATION_TYPE locationType = locationTypes.get(i);
            if (checkDone(type, locationType)) {
                for (SUBLEVEL_TYPE t : sublevelTypes) {
                    if (!checkDone(t, locationType)) {
                    }
                }
                locationTypes.remove(locationType);
            }
            if (locationTypes.isEmpty()) {
                break;
            }


            stats = new GenerationStats(locationType, type);
            generated = 0;
            DungeonLevel level;
            try {
                level = generateLevel(locationType, type);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
                error(locationType, type);
                continue;
            }
            generated++;

            System.out.println(generated + "th created");
            LevelStats levelStats = new LevelStats(level);
            LevelValidator validator = generator.getValidator();
            validator.setStats(levelStats);

            if (validator.isLevelValid(level) || NO_VALIDATION) {
                if (!checkRate(level)) {
                    levelStats.setValue(LevelStats.LEVEL_STAT.FAIL_REASON, LevelValidator.RNG_FAIL.LOW_RATING.name());
                    valid++;
                } else {
                    System.out.println(successful.size() + "th Success: \n" +
                     "Rate =" + level.getRate() +
                     "\n" +
                     "" + level);
                    MapMaster.addToListMap(successful,
                     getKey(type, locationType), level);

                    saveLevel(level);
                }
            }
            if (KEEP_STATS)
                try {
                    addToStats(level, stats, levelStats);
                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }

        }
        if (KEEP_STATS) {
            stats.setValue(GenerationStats.GEN_STAT.SUCCESS_RATE, "" + (successful.size() * 100 /
             generated));
            logResults(stats);
            MapMaster.addToListMap(analysisStats, data, stats);
        }

        running.remove(this);
        if (running.isEmpty()) {
            WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_FINISHED, true);
        }
    }

    private boolean checkDone(SUBLEVEL_TYPE type, LOCATION_TYPE locationType) {
        return
         successful.get(getKey(type, locationType)).size() >= data.getIntValue(
          PregeneratorData.PREGENERATOR_VALUES.LEVELS_REQUIRED);
    }

    private String getKey(SUBLEVEL_TYPE sublevelTypes, LOCATION_TYPE locationType) {
        return "" + sublevelTypes + locationType;
    }

    private DungeonLevel generateLevel(LOCATION_TYPE locationType, SUBLEVEL_TYPE type) {
        int tries = this.data.getIntValue(PregeneratorData.PREGENERATOR_VALUES.MAX_ATTEMPTS_PER_LEVEL);
        generator = new LevelGenerator(tries);
        LevelData data = LevelDataMaker.generateData(type, locationType);
        if (CUSTOM_REQS) {
            data.getReqs().setValue(LevelDataMaker.LEVEL_REQUIREMENTS.maxRooms, CUSTOM_REQS_maxRooms);
            data.getReqs().setValue(LevelDataMaker.LEVEL_REQUIREMENTS.minRooms, CUSTOM_REQS_minRooms);
            data.setValue(GeneratorEnums.LEVEL_VALUES.CLEAN_DISABLED, true);
            data.setValue(GeneratorEnums.LEVEL_VALUES.FILL_GLOBAL_COEF, 250);
            data.setValue(GeneratorEnums.LEVEL_VALUES.ADDITIONAL_FILL_RUNS, 2);
            data.setValue(GeneratorEnums.LEVEL_VALUES.SIZE_MOD, 200);
        }
        for (GeneratorEnums.LEVEL_VALUES fillVal : GeneratorEnums.LEVEL_VALUES.FILL_VALS) {
            int product = data.getIntValue(fillVal) * data.getIntValue(GeneratorEnums.LEVEL_VALUES.FILL_GLOBAL_COEF) / 100;
            data.setValue(fillVal, product);
        }

            if (DATA_RANDOMIZATION_ON) {
            //change data?
            LevelDataMaker.randomize(randomizationMod, RANDOM, data, stats);
            LevelDataMaker.applyMod(randomizationMod, data, GeneratorEnums.LEVEL_DATA_MODIFICATION.HALF_ZONES, null);
        }
        return generator.generateLevel(data, true);
    }

    private boolean checkRate(DungeonLevel level) {
        float rate;
        try {
            rate = new LevelRater(level).rateLevel();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            return false;
        }
        level.setRate(rate);
        return level.getRate() >= getMinRate();
    }

    private float getMinRate() {
        return data.getIntValue(PregeneratorData.PREGENERATOR_VALUES.MIN_RATING);
    }

    private void addToStats(DungeonLevel level, GenerationStats stats, LevelStats levelStats) {

        //check max min
        String failReason = levelStats.getValue(LevelStats.LEVEL_STAT.FAIL_REASON);
        if (StringMaster.isEmpty(failReason))
            failReason = LevelValidator.RNG_FAIL.ERROR_MODEL.toString();

        stats.addCount(GenerationStats.GEN_STAT.FAIL_REASONS, failReason);

        for (GenerationStats.GEN_STAT item : averageValues) {
            //            stats.addAverage(val, value);
            //fill, rate, quality, ...
            int val = getVal(item, level, stats);
            if (val > 0 && valid > 0)
                stats.addAverage(item, val, valid);
        }
    }

    private int getVal(GenerationStats.GEN_STAT item, DungeonLevel level, GenerationStats stats) {
        switch (item) {
            case AVRG_RATE:
                return (int) level.getRate();
            case AVRG_FILL_RATIO:
                return (int) (level.getFillRatio() * 100);
        }
        return 0;
    }


    public static void saveLevel(DungeonLevel level) {

        String stringData = level.toXml();
        String name =
                 level.getLocationType() + " " +
                level.getSublevelType() + ".xml";
        if (customPrefix!=null ){
            name = customPrefix+name;
        }
        name = NameMaster.getUniqueVersionedFileName(name, getPath(level.getLocationType()));
        name = name.toLowerCase();

        String path = StrPathBuilder.build(getPath(level.getLocationType()), name);
        main.system.auxiliary.log.LogMaster.log(1,"Level saved as: " +path);
        FileManager.write(stringData, path, true);

    }


    private void error(LOCATION_TYPE locationType, SUBLEVEL_TYPE type) {
    }

    private static String getPath(LOCATION_TYPE locationType) {
        return StrPathBuilder.build( PathFinder.getRandomLevelPath(),
         "pregenerated", locationType.name());
    }

    public void generateMainLevelPool() {


    }

    public static class AttemptsExceeded extends RuntimeException {

    }
}
