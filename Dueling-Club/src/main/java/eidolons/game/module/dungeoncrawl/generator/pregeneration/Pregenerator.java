package eidolons.game.module.dungeoncrawl.generator.pregeneration;

import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.LevelDataMaker;
import eidolons.game.module.dungeoncrawl.generator.LevelGenerator;
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
    PregeneratorData data;

    public Pregenerator(PregeneratorData data) {
        this.data = data;
    }

    public static void main(String[] args) {
        //logging off - or into a file...
        for (int i = 0; i < THREADS; i++) {
            new Thread(new Pregenerator(Pregenerator.getData(i)), " thread").start();
        }
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
        int rate;
        saveLevels();
        logResults();
    }

    private void logResults() {
    }

    private void saveLevels() {
    }

    public void generateMainLevelPool() {
        for (SUBLEVEL_TYPE type : data.sublevelTypes) {
            for (LOCATION_TYPE locationType : data.locationTypes) {
                while (true) {
                    generateLevel(locationType, type);
                }

            }
        }


    }

    private void generateLevel(LOCATION_TYPE locationType, SUBLEVEL_TYPE type) {
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
            return;
        }
//        genMap.put(level, stats);

        String stringData = level.toXml();
        String name = ScenarioGenerator.getLevelName(locationType, type) + ".xml";
        name = NameMaster.getUniqueVersionedFileName(name,
         StrPathBuilder.build(PathFinder.getRandomLevelPath(), type.name()));

        String path = StrPathBuilder.build(getPath(locationType), name);
        FileManager.write(stringData, path);

    }

    private void error(LOCATION_TYPE locationType, SUBLEVEL_TYPE type) {
    }

    private String getPath(LOCATION_TYPE locationType) {
        return StrPathBuilder.build(PathFinder.getRandomLevelPath(),
         "pregenerated", locationType.name());
    }


}
