package main.game.module.adventure.global;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.battle.mission.Mission;
import main.game.module.adventure.map.Place;
import main.system.text.NameMaster;

/**
 * Created by JustMe on 3/10/2018.
 */
public class ScenarioGenerator {

    public static ObjType generateScenarioType(Place place){
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
        Mission surface;
        int n=1;
        Mission[] sub= new Mission[n];
        Mission boss;


//        String missions=DataManager.toString(DataManager.getTypes(DC_TYPE.MISSIONS));
        //will be available as info for player review?
        ObjType templateType =
         //DataManager.getType(         RandomWizard.random()? "Mistfall" : "Scum of the Earth", DC_TYPE.SCENARIOS  );
         DataManager.getRandomType( DC_TYPE.SCENARIOS, "Crawl" );
        ObjType scenarioType = new ObjType(
        NameMaster.getUniqueVersionedName(place.getName(), DC_TYPE.SCENARIOS) ,templateType);
        DataManager.addType(scenarioType);
        //place types
//        scenarioType.setpr
        return scenarioType ;
    }
}
