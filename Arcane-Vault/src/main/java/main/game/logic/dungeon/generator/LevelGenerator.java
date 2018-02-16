package main.game.logic.dungeon.generator;

import main.content.enums.DungeonEnums.SUBLEVEL_TYPE;
import main.game.battlecraft.logic.dungeon.Dungeon;
import main.game.logic.dungeon.editor.LE_ObjMaster;
import main.game.logic.dungeon.editor.Level;
import main.game.logic.dungeon.editor.LevelEditor;
import main.game.logic.dungeon.editor.Mission;
import main.game.logic.dungeon.generator.graph.LevelGraph;
import main.game.logic.dungeon.generator.graph.LevelGraphMaster;
import main.game.logic.dungeon.generator.model.LevelModel;
import main.game.logic.dungeon.generator.model.LevelModelGenerator;
import main.game.logic.dungeon.generator.tilemap.TileMap;
import main.game.logic.dungeon.generator.tilemap.TileMapper;
import main.system.auxiliary.Loop;

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
        Loop loop = new Loop(12);
        while (true) {
            new LevelGenerator().generateTileMap(getLevelData(0));
            if (loop.ended()) {
                break;
            }
        }

    }

    public static LevelData getLevelData(
     int sublevel
//     ,Place place, ObjType mission,
//                                  Location location,
//                                   Quest quest,
////                                  BOSS_TYPE bossType,
//                                  OBJECTIVE_TYPE objectiveType
    ){
        int x = 70;
        int y = 50;
        return new LevelData(SUBLEVEL_TYPE.COMMON, x, y, sublevel);
    }

    public static TileMap generateTileMap(LevelData data){
        LevelGraph graph=new LevelGraphMaster(data).buildGraph();
        LevelModel model = new LevelModelGenerator(data).buildModel(graph);
        TileMap map = new TileMapper(model, data).map();
        return map;
    }
    public static void generate(Mission currentMission, Level level) {
        Dungeon dungeon = level.getDungeon();
        TileMap map = generateTileMap(getLevelData(0));
//        DungeonPlan plan = new DungeonPlan(null, dungeon);
//        plan.setObjMap(map.getObjMap());
//        dungeon.setPlan(plan);

        new LE_ObjMaster().initTileMap(map.getTiles());

        LevelEditor.getSimulation().getUnits().clear();
        LevelEditor.getSimulation().getBfObjects().clear();
        level.init();
//        level.setMap(new DungeonMapGenerator().generateMap(
//         initBuildParams(true, alt, dungeon), dungeon));
//        level.init();
    }
}
