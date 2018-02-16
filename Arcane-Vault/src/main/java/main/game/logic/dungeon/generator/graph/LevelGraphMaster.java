package main.game.logic.dungeon.generator.graph;

import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.ROOM_TYPE;
import main.game.logic.dungeon.generator.GeneratorEnums;
import main.game.logic.dungeon.generator.GeneratorEnums.GRAPH_RULE;
import main.game.logic.dungeon.generator.GeneratorEnums.LEVEL_GRAPH_LINK_TYPE;
import main.game.logic.dungeon.generator.GeneratorEnums.PATH_TYPE;
import main.game.logic.dungeon.generator.LevelData;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.datatypes.DequeImpl;

import java.util.*;

/**
 * Created by JustMe on 2/13/2018.
 */
public class LevelGraphMaster {
    LevelGraph graph;
    LevelData data;
    ArrayList<LevelGraphNode> unconnected;

    public LevelGraphMaster(LevelData data) {
        this.data = data;
    }

    public LevelGraph buildGraph() {
          graph =new LevelGraph();
        unconnected =     new ArrayList<>(graph.getNodes());
        buildMainPaths();
        createNodes(graph, data);
        buildCustomPaths();
        connectNodesRandomly(graph, data, unconnected); //the remainder
//        applyRules(graph, data);
        unconnected.forEach(node-> graph.removeLevelGraphNode(node));
        return graph;
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
        unconnected =     new ArrayList<>(graph.getNodes());
        unconnected.removeIf(node ->  ListMaster.isNotEmpty(graph.getAdjList().get(node)));

//        for (DUNGEON_TEMPLATES sub : data.getTemplates()) {
//            //zones!.. force links?
//            switch (sub) {
//
//            }
//        }
    }
    private void buildMainPaths() {
        int numberOfMainPaths = 2;
        LevelGraphNode startNode = graph.addNode(ROOM_TYPE.ENTRANCE_ROOM);
        LevelGraphNode exitNode = graph.addNode(ROOM_TYPE.EXIT_ROOM);
        buildPaths(numberOfMainPaths, startNode, exitNode);
    }
    private void buildCustomPaths() {
        int numberOfTreasurePaths = 2;
        GraphPath path = getPaths().iterator().next();
        LevelGraphNode startNode = path.getNodes().get(path.getNodes().size() / 2);
        LevelGraphNode exitNode = graph.addNode(ROOM_TYPE.TREASURE_ROOM);
        while (numberOfTreasurePaths > 0) {
            buildPaths(1, startNode, exitNode);
            numberOfTreasurePaths--;
        }
    }

    private void buildPaths(int numberOfPaths, LevelGraphNode startNode, LevelGraphNode exitNode) {

        while (numberOfPaths-->0) {//TODO can try reverse path too
            int steps = 5;
            unconnected = new ArrayList<>(graph.getNodes());
            unconnected.remove(startNode);
            unconnected.remove(exitNode);
            unconnected.removeIf(n -> new DequeImpl<>(graph.getAdjList().get(n)).size() > 3);
            GraphPath path = createPath(
             startNode, exitNode, steps// ,PATH_TYPE.easy
            );
            if (path == null) {
                break;
            } else
                getPaths().add(path);
        }

    }


    private GraphPath createPath(LevelGraphNode startNode,
                                 LevelGraphNode endNode, int steps) {
        LevelGraphNode tip1 = startNode;
        LevelGraphNode tip2 = endNode;

        LinkedHashMap nodes = new LinkedHashMap();
        nodes.put(0, startNode);
        nodes.put(1, endNode);
        boolean fromEnd = false;
        //what for? perhaps just from end?
        int i = 2;
        while (steps > 0) {
            steps--;
//            fromEnd = !fromEnd; how to?
            LevelGraphNode node = (fromEnd) ? tip2 : tip1;
            LevelGraphNode node2 = getOrCreateLinkNode(node);
            unconnected.remove(node2);
            connect(node, node2);
            if (fromEnd)
                tip2 = node2;
            else
                tip1 = node2;
            //TODO if fromEnd??
            nodes.put(i, node2);
            i++;
        }
        connect(tip1, tip2);

//sort by distance from start
//        node = startNode;
//        while(true){
//            path.add(node);
//            node= node.
//        }
        return new GraphPath(nodes, startNode, endNode, graph,  PATH_TYPE.easy);
    }

    public Set<GraphPath> getPaths() {
        return graph.getPaths();
    }

    private void connectNodesRandomly(LevelGraph graph, LevelData data,  Collection<LevelGraphNode> unconnected) {
        //create a path from start to exit in N steps
        unconnected.removeIf(n -> n.getRoomType() == ROOM_TYPE.ENTRANCE_ROOM || n.getRoomType() == ROOM_TYPE.EXIT_ROOM);
        while (true) {
            LevelGraphEdge link = connectTwoRandomNodes(graph, data, unconnected);
            if (link == null)
                break;
        }
        //apply rules that connect arbitrary nodes
        //TODO if enter/exit room is unconnected, it's a fail!
        unconnected.forEach(node -> graph.removeLevelGraphNode(node));
    }


    private LevelGraphEdge connectTwoRandomNodes(LevelGraph graph, LevelData data, Collection<LevelGraphNode> unconnected) {
        if (unconnected.size() < 2)
        {
//            getRandomLinkNode
            return null;
        }
        int n = RandomWizard.getRandomInt(unconnected.size());
        LevelGraphNode node = (LevelGraphNode) unconnected.toArray()[n];
        unconnected.remove(node);
        LevelGraphNode nodeTwo = chooseLinkNode(node,   unconnected);
        return connect(node, nodeTwo);
    }

    private LevelGraphEdge connect(LevelGraphNode node, LevelGraphNode nodeTwo) {
        LEVEL_GRAPH_LINK_TYPE type = GeneratorEnums.LEVEL_GRAPH_LINK_TYPE.NORMAL;
        LevelGraphEdge link = new LevelGraphEdge(type, node, nodeTwo);
        graph.addEdge(link);
        main.system.auxiliary.log.LogMaster.log(1,"Connected: "+node
        +" with " + nodeTwo);
        return link;
    }


    private LevelGraphNode getOrCreateLinkNode(LevelGraphNode node) {
        if (unconnected.size() > 0) {
            return unconnected.iterator().next();
        }
        ROOM_TYPE type =createLinkNode(node);
        return graph.addNode(type);
    }

    private ROOM_TYPE createLinkNode(LevelGraphNode node) {
        return  ROOM_TYPE.COMMON_ROOM;
    }

    private LevelGraphNode chooseLinkNode(LevelGraphNode node,
                                          Collection<LevelGraphNode> unconnected) {
        Map<ROOM_TYPE, Integer> weighMap = new HashMap<>();
//      TODO smart choice!  RandomWizard.getObjTypeByWeight()
        int n = RandomWizard.getRandomInt(unconnected.size());
        return (LevelGraphNode) unconnected.toArray()[n];
    }

    private void applyRules(LevelGraph graph, LevelData data) {
        int n = RandomWizard.getRandomInt(10);
        while (n > 0) {
            n--;
            GRAPH_RULE rule = getRandomRule();
            Object[] args = getRuleArgs(rule);
            new GraphTransformer().applyRule(rule, graph, args);
        }
    }

    private Object[] getRuleArgs(GRAPH_RULE rule) {
        switch (rule) {

        }
        return new Object[0];
    }

    private GRAPH_RULE getRandomRule() {
        return new EnumMaster<GRAPH_RULE>().getRandomEnumConst(GRAPH_RULE.class);
    }


}
