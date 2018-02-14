package main.game.battlecraft.logic.dungeon.generator.graph;

import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.DUNGEON_TEMPLATES;
import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.ROOM_TYPE;
import main.game.battlecraft.logic.dungeon.generator.LevelData;
import main.game.battlecraft.logic.dungeon.generator.graph.LevelGraph.LEVEL_GRAPH_LINK_TYPE;
import main.system.auxiliary.RandomWizard;

import java.awt.*;
import java.util.*;

/**
 * Created by JustMe on 2/13/2018.
 */
public class LevelGraphMaster {
    LevelGraph graph;
    LevelData data;
    Set<LinkedHashSet<LevelGraphEdge>> paths;
    Set<LevelGraphNode> unconnected;

    public LevelGraphMaster(LevelData data) {
        this.data = data;
    }

    public LevelGraph buildGraph() {
        LevelGraph graph =
         new LevelGraph();
        unconnected = graph.getNodes();
        paths = new HashSet<>();
        buildPaths();
        createNodes(graph, data);
        connectNodes(graph, data);
        applyRules(graph, data);

        return graph;
    }

    private void buildPaths() {
        buildMainPaths();
    }
//    int numberOfTreasurePaths;
private void buildCustomPaths() {
    int numberOfTreasurePaths = 2;
    LevelGraphNode startNode = graph.addNode(ROOM_TYPE.ENTRANCE_ROOM);
    LevelGraphNode exitNode = graph.addNode(ROOM_TYPE.TREASURE_ROOM);
}
    private void buildMainPaths() {
        int numberOfMainPaths = 2;
        LevelGraphNode startNode = graph.addNode(ROOM_TYPE.ENTRANCE_ROOM);
        LevelGraphNode exitNode = graph.addNode(ROOM_TYPE.EXIT_ROOM);
        while (paths.size() < numberOfMainPaths) {//TODO can try reverse path too
            int steps=5;
            unconnected = graph.getNodes();
            LinkedHashSet<LevelGraphEdge>  path =  createPath(
             startNode, exitNode, steps, unconnected);
           if (path==null ){
               break;
           } else
               paths.add(path);
        }

    }

    private LinkedHashSet<LevelGraphEdge> createPath(LevelGraphNode startNode,
                                                     LevelGraphNode endNode, int steps,
                                                     Set<LevelGraphNode> unconnected) {
        LinkedHashSet<LevelGraphEdge> path = new LinkedHashSet<>();

        LevelGraphNode tip1 = startNode;
        LevelGraphNode tip2 = endNode;

        LinkedHashSet nodes=  new LinkedHashSet();
        nodes.add(startNode);
        nodes.add(endNode);
        boolean fromEnd= true;
        //what for? perhaps just from end?
        while(steps>0){
            steps--;
            fromEnd = !fromEnd;
            LevelGraphNode node = (fromEnd) ? tip2 : tip1;
            LevelGraphNode node2 = chooseLinkNode(node, unconnected);
            //TODO create??
            connect(node, node2);
            if (fromEnd)
                tip2 = node2;
            else
                tip1 = node2;
            nodes.add(node2);
        }
        connect(tip1, tip2);
//sort by distance from start



//        node = startNode;
//        while(true){
//            path.add(node);
//            node= node.
//        }
        return new GraphPath(startNode, endNode, nodes, PATH_TYPE.easy);
    }


    private void connectNodes(LevelGraph graph, LevelData data) {
        //create a path from start to exit in N steps
        Set<LevelGraphNode> unconnected = graph.getNodes();
        while (paths.size() < n) {
            createPath(
             startNode, exitNode, //TODO can try reverse path too
             steps, unconnected);
        }
        while (true) {
            LevelGraphEdge link = connectNodes(graph, data, unconnected);
            if (link == null)
                break;
        }
        //apply rules that connect arbitrary nodes
        //TODO if enter/exit room is unconnected, it's a fail!
        unconnected.forEach(node -> graph.removeLevelGraphNode(node));
    }


    private LevelGraphEdge connectNodes(LevelGraph graph, LevelData data, Set<LevelGraphNode> unconnected) {
        if (unconnected.size() < 2)
            return null;
        int n = RandomWizard.getRandomInt(unconnected.size());
        LevelGraphNode node = (LevelGraphNode) unconnected.toArray()[n];
        unconnected.remove(node);
        LevelGraphNode nodeTwo = chooseLinkNode(node, graph, data, unconnected);
        return connect(node, nodeTwo);
    }

    private LevelGraphEdge connect(LevelGraphNode node, LevelGraphNode nodeTwo) {
        LEVEL_GRAPH_LINK_TYPE type = LEVEL_GRAPH_LINK_TYPE.NORMAL;
        LevelGraphEdge link = new LevelGraphEdge(type, node, nodeTwo);
        graph.addEdge(link);
        return link;
    }


    private LevelGraphNode chooseLinkNode(LevelGraphNode node, Set<LevelGraphNode> unconnected) {
        Map<ROOM_TYPE, Integer> weighMap = new HashMap<>();
//      TODO smart choice!  RandomWizard.getObjTypeByWeight()
        int n = RandomWizard.getRandomInt(unconnected.size());
        return (LevelGraphNode) unconnected.toArray()[n];
    }

    private void applyRules(LevelGraph graph, LevelData data) {
    }

    private void createNodes(LevelGraph graph, LevelData data) {
        graph.addNode(ROOM_TYPE.ENTRANCE_ROOM);
        graph.addNode(ROOM_TYPE.EXIT_ROOM);
        graph.addNode(ROOM_TYPE.THRONE_ROOM);
        int n = RandomWizard.getRandomInt(4);
        graph.addNodes(ROOM_TYPE.TREASURE_ROOM, n);
        n = RandomWizard.getRandomInt(4);
        graph.addNodes(ROOM_TYPE.GUARD_ROOM, n);
        n = RandomWizard.getRandomInt(4);
        graph.addNodes(ROOM_TYPE.DEATH_ROOM, n);
        n = RandomWizard.getRandomInt(4);
        graph.addNodes(ROOM_TYPE.SECRET_ROOM, n);

        for (DUNGEON_TEMPLATES sub : data.getTemplates()) {
            //zones!.. force links?
            switch (sub) {

            }
        }
    }

    public enum PATH_TYPE {
        secret, hard, easy,
    }
}
