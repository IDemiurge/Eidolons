package main.game.battlecraft.logic.dungeon.generator;

import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.dungeon.generator.graph.LevelGraph;
import main.game.battlecraft.logic.dungeon.generator.graph.LevelGraphMaster;
import main.game.battlecraft.logic.dungeon.generator.model.LevelModel;
import main.game.battlecraft.logic.dungeon.generator.model.LevelModelGenerator;
import main.game.battlecraft.logic.meta.scenario.ObjectiveMaster.OBJECTIVE_TYPE;
import main.game.module.adventure.map.Place;
import main.game.module.adventure.town.TownHall.Quest;
import main.game.module.dungeoncrawl.dungeon.Location;

/**
 * Created by JustMe on 2/13/2018.
 *
 * Zones and blocks
 * Perhaps a logical abstract map first?
 * 30x20+ levels or so
 * split into 2-3 zones
 * each zone will have a template
 *
 * this is where those 'promenade', 'great hall' things come in
 */
public class LevelGenerator {

    public void generateLevels(Place place,ObjType mission){

    }
        public String generateLevel(LevelData data){
        LevelGraph graph=new LevelGraphMaster().buildGraph(data);
            LevelModel model = new LevelModelGenerator().buildModel(graph);
fill();
    }

//
//    private void fill() {
//        placeDoors();
//        placeLightEmitters();
//        placeContainers();
//        placeDecor();
//    }

    public LevelData getLevelData(Place place, ObjType mission,
                                  int sublevel,
                                  Location location,
                                   Quest quest,
//                                  BOSS_TYPE bossType,
                                  OBJECTIVE_TYPE objectiveType

    ){
        int x = 30;
        int y = 20;
        return new LevelData(SUBLEVEL_TYPE.COMMON, x, y, sublevel);
    }

}
