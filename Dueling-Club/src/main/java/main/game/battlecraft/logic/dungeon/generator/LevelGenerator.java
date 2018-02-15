package main.game.battlecraft.logic.dungeon.generator;

import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.dungeon.generator.graph.LevelGraph;
import main.game.battlecraft.logic.dungeon.generator.graph.LevelGraphMaster;
import main.game.battlecraft.logic.dungeon.generator.model.LevelModel;
import main.game.battlecraft.logic.dungeon.generator.model.LevelModelGenerator;
import main.game.battlecraft.logic.dungeon.generator.tilemap.TileMapper;
import main.game.module.adventure.map.Place;

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

    public static void main(String[] args) {
       new LevelGenerator().generateLevel(getLevelData(0));
    }

    public void generateLevels(Place place,ObjType mission){

    }
        public String generateLevel(LevelData data){
        LevelGraph graph=new LevelGraphMaster(data).buildGraph();
            LevelModel model = new LevelModelGenerator(data).buildModel(graph);
            new TileMapper(model, data).createLevel();
            return null;
        }

//
//    private void fill() {
//        placeDoors();
//        placeLightEmitters();
//        placeContainers();
//        placeDecor();
//    }

    public static LevelData getLevelData(
     int sublevel
//     ,Place place, ObjType mission,
//                                  Location location,
//                                   Quest quest,
////                                  BOSS_TYPE bossType,
//                                  OBJECTIVE_TYPE objectiveType

    ){
        int x = 30;
        int y = 20;
        return new LevelData(SUBLEVEL_TYPE.COMMON, x, y, sublevel);
    }

}
