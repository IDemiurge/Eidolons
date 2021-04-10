package macro.generation;

import eidolons.content.PROPS;
import eidolons.game.module.dungeoncrawl.struct.DungeonLevel;
import eidolons.dungeons.generator.LevelGenerator;
import eidolons.macro.map.Place;
import eidolons.system.data.MetaDataUnit;
import eidolons.system.data.MetaDataUnit.META_DATA;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import eidolons.system.text.NameMaster;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.FileManager;
import main.system.datatypes.WeightMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 3/10/2018.
 */
public class ScenarioGenerator {

    private static final LOCATION_TYPE DEFAULT_LOCATION = LOCATION_TYPE.CAVE;
    private static final String DEFAULT_LEVEL = PathFinder.getDungeonLevelFolder()+"/default.xml";

    public static ObjType generateScenarioType(Place place) {
        if (isRandomGenerationOn()) {
            return generateRandomLevelScenario(place);
        }
        int n = 1;
        String missions = "";
        String path = StrPathBuilder.build(
         PathFinder.getDungeonLevelFolder(), "place dungeons",
         place.getProperty(MACRO_PROPS.PLACE_SUBTYPE));
        for (int i = 0; i < n; i++) {
            String mission;
            try {
                mission = FileManager.getRandomFilePath(path);
            } catch (Exception e) {
                mission = FileManager.getRandomFilePath(StrPathBuilder.build(
                 PathFinder.getDungeonLevelFolder(), "place dungeons", "default"));
                main.system.ExceptionMaster.printStackTrace(e);
            }

            mission = PathUtils.removePreviousPathSegments(mission, PathFinder.getDungeonLevelFolder());
            ObjType missionType = new ObjType("", DC_TYPE.FLOORS);
            missionType.setProperty(PROPS.FLOOR_FILE_PATH, mission);
            DataManager.addType(missionType);
            missions += mission;
        }
        //        String missions=DataManager.toString(DataManager.getTypes(DC_TYPE.MISSIONS));
        //will be available as info for player review?

        ObjType templateType =
         DataManager.getRandomType(DC_TYPE.SCENARIOS, "Crawl");
        ObjType scenarioType = new ObjType(
         NameMaster.getUniqueVersionedName(place.getName(), DC_TYPE.SCENARIOS), templateType);
        scenarioType.setProperty(PROPS.SCENARIO_MISSIONS, missions);

        DataManager.addType(scenarioType);
        //place types
        //        scenarioType.setpr
        return scenarioType;
    }

    private static boolean isRandomGenerationOn() {
        return true;
    }

    public static ObjType generateRandomLevelScenario(Entity place) {
        return generateRandomLevelScenario(100, place);
    }

    public static ObjType generateRandomLevelScenario(int tries, Entity place) {
        return generateRandomLevelScenario(tries, place.getName(),
         place.getProperty(MACRO_PROPS.PLACE_SUBTYPE));
    }

    private static int getNumberOfLevels(ObjType scenarioType, LOCATION_TYPE locationType) {
        //        scenarioType.getProperty()
        return 3;
    }

    public static ObjType generateRandomLevelScenario(int tries, String scenarioName, String locationTypeName) {
        ObjType templateType =
         DataManager.getRandomType(DC_TYPE.SCENARIOS, "Crawl");
        LOCATION_TYPE locationType = new EnumMaster<LOCATION_TYPE>().
         retrieveEnumConst(LOCATION_TYPE.class, locationTypeName);

        ObjType scenarioType =
         locationType == null ? DataManager.getType(scenarioName, DC_TYPE.SCENARIOS) :
          new ObjType(NameMaster.getUniqueVersionedName(scenarioName, DC_TYPE.SCENARIOS),
           templateType);


        List<LOCATION_TYPE> locationTypes = new ArrayList<>();
        if (locationType == null) {
            locationTypes = getLocationTypes(scenarioType);
        } else
            locationType = checkAltLocationType(locationType);
        int n = getNumberOfLevels(scenarioType, locationType);
        List<SUBLEVEL_TYPE> types =
         createSublevelTypes(n, locationType);

        String levelPaths = "";
        int i = 0;
        for (SUBLEVEL_TYPE type : types) {
            if (locationTypes.size() > i)
                locationType = locationTypes.get(i++);
            if (isUsePregenerated()) {
                String level = choosePregenLevel(type, locationType);
                if (level == null ) {
                    level = getAltPregenLevel(type, locationType);
                    locationType =  DEFAULT_LOCATION;
                }
                if (level == null) {
                    main.system.auxiliary.log.LogMaster.log(1,"NO LEVEL CHOSEN! USING DEFAULT:  " + DEFAULT_LEVEL);
                    levelPaths+= DEFAULT_LEVEL+ ContainerUtils.getContainerSeparator();
                }
                else {
                    main.system.auxiliary.log.LogMaster.log(1,"LEVEL CHOSEN:  " +level);
                    String path =
                     StrPathBuilder.build(
                      getPath(locationType), level);
                    levelPaths += (path) + ContainerUtils.getContainerSeparator();
                }
            } else {
                DungeonLevel level = new LevelGenerator(tries).generateLevel(type, locationType);
//              TODO   Pregenerator.saveLevel(level);

                String stringData = level.toXml();
                String name = getLevelName(locationType, type) + ".xml";
                name = NameMaster.getUniqueVersionedFileName(name,
                 StrPathBuilder.build(PathFinder.getRandomLevelPath(), type.name()));

                String path = StrPathBuilder.build(getPath(locationType), name);
                FileManager.write(stringData, path);
                levelPaths += (path) + ContainerUtils.getContainerSeparator();

            }
        }
        scenarioType.setProperty(PROPS.SCENARIO_MISSIONS, levelPaths);
        DataManager.addType(scenarioType);
        scenarioType.setGroup("Random", false);
        return scenarioType;
    }

    private static List<LOCATION_TYPE> getLocationTypes(ObjType scenarioType) {
        List<LOCATION_TYPE> list = new ArrayList<>();
        for (String locationTypeName : ContainerUtils.openContainer(scenarioType.getProperty(PROPS.SCENARIO_MISSIONS))) {
            LOCATION_TYPE locationType = new EnumMaster<LOCATION_TYPE>().
             retrieveEnumConst(LOCATION_TYPE.class, locationTypeName);
            list.add(locationType);
        }
        return list;
    }


    private static LOCATION_TYPE checkAltLocationType(LOCATION_TYPE locationType) {
        switch (locationType) {
            case SEWER:
            case HELL:
            case ASTRAL:
                return LOCATION_TYPE.TOWER;
            case CASTLE:
                return LOCATION_TYPE.TEMPLE;
            case BARROW:
                return LOCATION_TYPE.CRYPT;
            case HIVE:
            case DEN:
            case RUIN:
            case CAMP:
            case HOUSE:
            case GROVE:
                return LOCATION_TYPE.CAVE;
        }

        return locationType;
    }

    private static boolean isUsePregenerated() {
        return OptionsMaster.getGameplayOptions().getBooleanValue(GAMEPLAY_OPTION.PREGENERATED_RNG_LEVELS);
    }

    public static String getPath(LOCATION_TYPE locationType) {
        return StrPathBuilder.build(PathFinder.getRandomLevelPath(),
         locationType.name());
    }

    public static String getLevelName(LOCATION_TYPE locationType, SUBLEVEL_TYPE type) {
        return locationType + " " + type;
    }

    private static String choosePregenLevel(SUBLEVEL_TYPE type,
                                            LOCATION_TYPE locationType) {
        List<File> levels = FileManager.getFilesFromDirectory(getPath(locationType), false);
        levels = levels.stream().filter(file -> file.getName().toLowerCase()
         .startsWith(getLevelName(locationType, type).toLowerCase())).collect(Collectors.toList());
        if (levels.isEmpty()) {
            return null;
        }
        if (isSequentialPregenChoice()) {
            try {
                int index = getNextSequentialPregenIndex(locationType, type, levels.size());
                if (levels.size()<=index) {
                    index= 0;
                }
                main.system.auxiliary.log.LogMaster.log(1,"Sequential LEVEL index:  " +index);
                return levels.get(index).getName();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
        return FileManager.getRandomFile(levels).getName();
    }

    private static String getAltPregenLevel(SUBLEVEL_TYPE type, LOCATION_TYPE locationType) {
        return choosePregenLevel(SUBLEVEL_TYPE.COMMON, DEFAULT_LOCATION);
    }

    private static int getNextSequentialPregenIndex(LOCATION_TYPE locationType, SUBLEVEL_TYPE type,
                                                    int size) {
        MetaDataUnit data = MetaDataUnit.getInstance();
        WeightMap map = data.getWeightMapValue(META_DATA.LAST_PREGEN_LVL_INDEX_MAP);
        String val = type + " " + locationType;
        data.addCount(META_DATA.LAST_PREGEN_LVL_INDEX, val, size );
        return map.get(val) == null ? 0 : (int) map.get(val);
    }

    private static boolean isSequentialPregenChoice() {
        return OptionsMaster.getGameplayOptions().getBooleanValue(GAMEPLAY_OPTION.SEQUENTIAL_RNG); //TODO options
    }

    private static List<SUBLEVEL_TYPE> createSublevelTypes(int n, LOCATION_TYPE locationType) {
        List<SUBLEVEL_TYPE> list = new ArrayList<>();

        for (int i = 0; i < n; i++) {
//            if (CoreEngine.isFastMode()) {
//                list.add(SUBLEVEL_TYPE.BOSS);
//            }
//            else
                switch (i%3) {
                    case 1:
                        list.add(SUBLEVEL_TYPE.PRE_BOSS);
                        break;
                    case 2:
                        list.add(
                         !isInvertedSize()
                          ? SUBLEVEL_TYPE.BOSS
                          : SUBLEVEL_TYPE.COMMON);
                        break;
                    default:
                        list.add(
                         isInvertedSize()
                          ? SUBLEVEL_TYPE.BOSS
                          : SUBLEVEL_TYPE.COMMON);
                        break;
                }

        }
        return list;
    }

    private static boolean isInvertedSize() {
        return true;
    }
}
