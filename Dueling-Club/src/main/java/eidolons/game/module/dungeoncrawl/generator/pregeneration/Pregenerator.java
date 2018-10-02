package eidolons.game.module.dungeoncrawl.generator.pregeneration;

import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.LevelDataMaker;
import eidolons.game.module.dungeoncrawl.generator.LevelGenerator;
import eidolons.game.module.dungeoncrawl.generator.LevelValidator;
import eidolons.game.module.dungeoncrawl.generator.LevelValidator.RNG_FAIL;
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
import main.data.XLinkedMap;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.data.DataUnit;
import main.system.data.DataUnitFactory;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_DATA_MODIFICATION.HALF_ZONES;
import static eidolons.game.module.dungeoncrawl.generator.test.GenerationStats.GEN_STAT.AVRG_FILL_RATIO;
import static eidolons.game.module.dungeoncrawl.generator.test.GenerationStats.GEN_STAT.AVRG_RATE;

/**
 * Created by JustMe on 8/17/2018.
 */
public class Pregenerator implements Runnable {


    public static final SUBLEVEL_TYPE[] GENERATED_SUBLEVELS_ALL = {
            SUBLEVEL_TYPE.COMMON,
            SUBLEVEL_TYPE.PRE_BOSS,
            SUBLEVEL_TYPE.BOSS,
    };
    public static final SUBLEVEL_TYPE[][] GENERATED_SUBLEVELS = {
            {SUBLEVEL_TYPE.COMMON,},
            {SUBLEVEL_TYPE.PRE_BOSS,},
            {SUBLEVEL_TYPE.BOSS,}
    };
    public static final boolean TEST_MODE = false;
    public static final boolean NO_VALIDATION = false;
    public static final GEN_STAT[] averageValues = {
            AVRG_RATE,
            AVRG_FILL_RATIO,
            //     AVRG_EXITS_DISTANCE,
    };
    private static final int THREADS = 2;
    private static final Boolean RANDOM = null;
    public static LOCATION_TYPE[] LOCATION_TYPES = {
            LOCATION_TYPE.CAVE,
            LOCATION_TYPE.DUNGEON,
            LOCATION_TYPE.CEMETERY,
            LOCATION_TYPE.TOWER,
            LOCATION_TYPE.CRYPT,
    };
    public static final LOCATION_TYPE[][] GENERATED_LOCATIONS = {
            {
                    LOCATION_TYPE.DUNGEON,
                    LOCATION_TYPE.CRYPT,
            },
            {
                    LOCATION_TYPE.TOWER,
                    LOCATION_TYPE.CEMETERY,
                    LOCATION_TYPE.CAVE,
            },
    };
    private static List<Pregenerator> running = new ArrayList<>();
    private static Map<PregeneratorData, List<GenerationStats>> analysisStats = new XLinkedMap<>();
    private PregeneratorData data;
    private int generated;
    private int valid;
    private List<DungeonLevel> successful = new ArrayList<>();
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

        String text = "Generation analysis: \n";
        for (PregeneratorData data : analysisStats.keySet())
            for (GenerationStats stat : analysisStats.get(data)) {
                text += "Generation with data: \n";
                text += data.toString() + "\n";
                text += stat.toString() + "\n\n\n";
            }

        String path = StrPathBuilder.build(PathFinder.getRandomLevelPath(),
                "pregenerated", "reports");
        String name = NameMaster.getUniqueVersionedFileName("report.txt",
                path);
        FileManager.write(text, StrPathBuilder.build(path, name));
        System.out.println(" " + text);
        //pregen data
    }

    private static PregeneratorData getData(int i) {
        DataUnitFactory<DataUnit<PREGENERATOR_VALUES>> factory = new DataUnitFactory();
        factory.setValueNames(PregeneratorData.PREGENERATOR_VALUES.values());

        String[] vals = Arrays.stream(PREGENERATOR_VALUES.values()).map(v -> v.getDefaultValue() + "").collect(Collectors.toList())
                .toArray(new String[PREGENERATOR_VALUES.values().length]);
        factory.setValues(vals);

        SUBLEVEL_TYPE[] sublevelTypes = GENERATED_SUBLEVELS_ALL;
        LOCATION_TYPE[] locationTypes = GENERATED_LOCATIONS[i];
        return new PregeneratorData(factory.constructDataString(),
                sublevelTypes, locationTypes);
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
        FileManager.write(text, StrPathBuilder.build(path, name));
        System.out.println(" " + text);
        //pregen data
    }

    @Override
    public void run() {
        randomizationMod = data.getFloatValue(
                PREGENERATOR_VALUES.RANDOMIZATION_MOD) / 100;
        for (SUBLEVEL_TYPE type : data.sublevelTypes) {
            for (LOCATION_TYPE locationType : data.locationTypes) {
                stats = new GenerationStats(locationType, type);
                successful = new ArrayList<>();
                generated = 0;
                while (true) { //TODO max failed  // time
                    DungeonLevel level = null;
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
                            levelStats.setValue(LEVEL_STAT.FAIL_REASON, RNG_FAIL.LOW_RATING.name());
                            valid++;
                        } else {
                            System.out.println(successful.size() + "th Success: \n" +
                                    "Rate =" + level.getRate() +
                                    "\n" +
                                    "" + level);
                            successful.add(level);
                            saveLevel(level);
                        }
                    }
                    try {
                        addToStats(level, stats, levelStats);
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                    if (successful.size() >= data.getIntValue(
                            PREGENERATOR_VALUES.LEVELS_REQUIRED))
                        break;
                }

                stats.setValue(GEN_STAT.SUCCESS_RATE, "" + (successful.size() * 100 /
                        generated));
                logResults(stats);
                MapMaster.addToListMap(analysisStats, data, stats);
                //successMap.put(locationType)
            }
        }
        //        logResults(stats);

        running.remove(this);
        if (running.isEmpty()) {
            WaitMaster.receiveInput(WAIT_OPERATIONS.GAME_FINISHED, true);
        }
    }

    private DungeonLevel generateLevel(LOCATION_TYPE locationType, SUBLEVEL_TYPE type) {
        int tries = this.data.getIntValue(PREGENERATOR_VALUES.MAX_ATTEMPTS_PER_LEVEL);
        generator = new LevelGenerator(tries);
        LevelData data = LevelDataMaker.generateData(type, locationType);
        //change data?
        LevelDataMaker.randomize(randomizationMod, RANDOM, data, stats);

        LevelDataMaker.applyMod(randomizationMod, data, HALF_ZONES, null);
        return generator.generateLevel(data, true);
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
        if (StringMaster.isEmpty(failReason))
            failReason = RNG_FAIL.ERROR_MODEL.toString();

        stats.addCount(GEN_STAT.FAIL_REASONS, failReason);

        for (GEN_STAT item : averageValues) {
            //            stats.addAverage(val, value);
            //fill, rate, quality, ...
            int val = getVal(item, level, stats);
            if (val > 0 && valid > 0)
                stats.addAverage(item, val, valid);
        }
    }

    private int getVal(GEN_STAT item, DungeonLevel level, GenerationStats stats) {
        switch (item) {
            case AVRG_RATE:
                return (int) level.getRate();
            case AVRG_FILL_RATIO:
                return (int) (level.getFillRatio() * 100);
        }
        return 0;
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

    public class AttemptsExceeded extends RuntimeException {

    }
}
