package eidolons.macro.generation;

import eidolons.content.PROPS;
import eidolons.game.module.dungeoncrawl.dungeon.DungeonLevel;
import eidolons.game.module.dungeoncrawl.generator.LevelGenerator;
import eidolons.macro.map.Place;
import eidolons.system.data.MetaDataUnit;
import eidolons.system.data.MetaDataUnit.META_DATA;
import eidolons.system.text.NameMaster;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.PathUtils;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.FileManager;
import main.system.datatypes.WeightMap;
import main.system.launch.CoreEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 3/10/2018.
 */
public class ScenarioGenerator {

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
            String mission = null;
            try {
                mission = FileManager.getRandomFilePath(path);
            } catch (Exception e) {
                mission = FileManager.getRandomFilePath(StrPathBuilder.build(
                 PathFinder.getDungeonLevelFolder(), "place dungeons", "default"));
                main.system.ExceptionMaster.printStackTrace(e);
            }

            mission = PathUtils.removePreviousPathSegments(mission, PathFinder.getDungeonLevelFolder());
            ObjType missionType = new ObjType("", DC_TYPE.MISSIONS);
            missionType.setProperty(PROPS.MISSION_FILE_PATH, mission);
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

    public static ObjType generateRandomLevelScenario(int tries, String scenarioName, String locationTypeName) {
        ObjType templateType =
         DataManager.getRandomType(DC_TYPE.SCENARIOS, "Crawl");

        ObjType scenarioType = new ObjType(
         NameMaster.getUniqueVersionedName(scenarioName, DC_TYPE.SCENARIOS), templateType);


        LOCATION_TYPE locationType = new EnumMaster<LOCATION_TYPE>().
         retrieveEnumConst(LOCATION_TYPE.class, locationTypeName);

        locationType=checkAltLocationType(locationType);
        int n = 3;
        List<SUBLEVEL_TYPE> types =
         createSublevelTypes(n, locationType);

        String levelPaths = "";
        if (isUsePregenerated()) {
            for (SUBLEVEL_TYPE type : types) {
                String path =
                 StrPathBuilder.build(
                  getPath(locationType), choosePregenLevel(type, locationType));
                levelPaths += (path) + ContainerUtils.getContainerSeparator();
            }
        } else
            for (SUBLEVEL_TYPE type : types) {
                DungeonLevel level = new LevelGenerator(tries).generateLevel(type, locationType);

                String stringData = level.toXml();
                String name = getLevelName(locationType, type) + ".xml";
                name = NameMaster.getUniqueVersionedFileName(name,
                 StrPathBuilder.build(PathFinder.getRandomLevelPath(), type.name()));

                String path = StrPathBuilder.build(getPath(locationType), name);
                FileManager.write(stringData, path);
                levelPaths += (path) + ContainerUtils.getContainerSeparator();

                Coordinates.resetCaches();
            }
        scenarioType.setProperty(PROPS.SCENARIO_MISSIONS, levelPaths);
        DataManager.addType(scenarioType);
        scenarioType.setGroup("Random", false);
        return scenarioType;
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
              return  LOCATION_TYPE.CRYPT;
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
        return true;
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
        levels = levels.stream().filter(file -> file.getName()
         .startsWith(getLevelName(locationType, type))).collect(Collectors.toList());
     if (levels.isEmpty()){
         return getAltPregenLevel(type, locationType);
     }
        if (isSequentialPregenChoice()){
            int index=getNextSequentialPregenIndex(locationType, type, levels.size());
            return levels.get(index).getName();
        } else
        return FileManager.getRandomFile(levels).getName();
    }

    private static String getAltPregenLevel(SUBLEVEL_TYPE type, LOCATION_TYPE locationType) {
        return choosePregenLevel(SUBLEVEL_TYPE.COMMON, LOCATION_TYPE.CAVE);
    }

    private static int getNextSequentialPregenIndex(LOCATION_TYPE locationType, SUBLEVEL_TYPE type, int size) {
        MetaDataUnit data = MetaDataUnit.getInstance();
        WeightMap map = data.getWeightMapValue(META_DATA.LAST_PREGEN_LVL_INDEX_MAP);
        String val=type+" " +locationType;
        data.addCount(META_DATA.LAST_PREGEN_LVL_INDEX_MAP, val, size);
        int index = (int) map.get(val);
        return index;
    }

    private static boolean isSequentialPregenChoice() {
        return false; //TODO options
    }

    private static List<SUBLEVEL_TYPE> createSublevelTypes(int n, LOCATION_TYPE locationType) {
        List<SUBLEVEL_TYPE> list = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (CoreEngine.isFastMode()){
                list.add(SUBLEVEL_TYPE.BOSS);
            } else
            switch (i) {
                case 0:
                    list.add(SUBLEVEL_TYPE.COMMON);
                    break;
                case 1:
                    list.add(SUBLEVEL_TYPE.PRE_BOSS);
                    break;
                case 2:
                    list.add(SUBLEVEL_TYPE.BOSS);
                    break;
                default:
                    list.add(SUBLEVEL_TYPE.COMMON);
                    break;
            }

        }
        return list;
    }
}
