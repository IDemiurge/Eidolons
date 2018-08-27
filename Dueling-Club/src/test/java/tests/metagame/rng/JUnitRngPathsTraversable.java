package tests.metagame.rng;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_CELL;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraph;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphEdge;
import eidolons.game.module.dungeoncrawl.generator.graph.LevelGraphNode;
import eidolons.game.module.dungeoncrawl.generator.model.LevelModel;
import eidolons.game.module.dungeoncrawl.generator.model.Room;
import main.game.bf.Coordinates;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by JustMe on 7/23/2018.
 */
public class JUnitRngPathsTraversable extends JUnitRng{
    Map<LevelGraphNode, Room> map;
    @Test
    public void rngTest() {
        super.rngTest();
//        TileMap map = new LevelGenerator().generateTileMap(LevelDataMaker.getDefaultLevelData(0));

        LevelGraph graph = null;
        LevelModel model;
//        model.

        for (LevelGraphNode node : graph.getAdjList().keySet()) {
            Set<LevelGraphEdge> edges = graph.getAdjList().get(node);

        }
        for (LevelGraphEdge edge : graph.getEdges()) {
//            checkCanPass(edge.getNodeOne(), edge.getNodeTwo(), edgeMap.get(edge));
        }
    }

    private void checkCanPass(LevelGraphNode nodeOne, LevelGraphNode nodeTwo) {
//        canPass(map.get(nodeOne), map.get(nodeTwo));

    }

    private void canPass(Coordinates start, Coordinates end  ) {

    }
        private void canPass(Room room, Room room1, Room link) {

        List<Coordinates> passage;
        ROOM_CELL[][] cells;

        /*
        #o#o
        ##o#
        oooo

         */
        //there must be orthogonal adjacency of passable cells in each row/column
    }
}














