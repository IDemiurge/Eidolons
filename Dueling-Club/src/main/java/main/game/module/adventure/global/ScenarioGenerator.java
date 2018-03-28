package main.game.module.adventure.global;

import main.content.DC_TYPE;
import main.content.PROPS;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.type.ObjType;
import main.game.module.adventure.map.Place;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.text.NameMaster;

/**
 * Created by JustMe on 3/10/2018.
 */
public class ScenarioGenerator {

    public static ObjType generateScenarioType(Place place) {
        //dungeon level

        //missions
//        TERRAIN_TYPE terrainType;
//        PLACE_TYPE type=null ;
//        LOCATION_TYPE
//        switch (type) {
//            case DUNGEON:
//                break;
//            case LOCATION:
//                break;
//            case BUILDING:
//                break;
//        }
        //that's already about generating the missions!
        //but this stuff will need to be saved() somehow too!
        //temp directory for each Macro Game
        //store levels, party data's etc

        //how to import the LE branch?
//        MacroDataManager.saveLevel(level);
//        Mission surface;
//        Mission[] sub= new Mission[n];
//        Mission boss;

        int n = 1;
        String missions = "";
        String path = StrPathBuilder.build(
         PathFinder.getDungeonLevelFolder(), "place dungeons",
         place.getProperty(MACRO_PROPS.PLACE_SUBTYPE));
        for (int i = 0; i < n; i++) {
            String mission = FileManager.getRandomFilePath(path);
            mission = StringMaster.removePreviousPathSegments(mission, PathFinder.getDungeonLevelFolder());
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
}
