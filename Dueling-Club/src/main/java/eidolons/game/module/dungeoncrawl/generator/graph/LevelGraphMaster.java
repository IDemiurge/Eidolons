package eidolons.game.module.dungeoncrawl.generator.graph;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.dungeon.LevelZone;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.GRAPH_RULE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_GRAPH_LINK_TYPE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.LEVEL_VALUES;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.PATH_TYPE;
import eidolons.game.module.dungeoncrawl.generator.LevelData;
import eidolons.game.module.dungeoncrawl.generator.LevelGenerator;
import eidolons.game.module.dungeoncrawl.generator.level.ZoneCreator;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.datatypes.DequeImpl;

import java.util.*;
import java.util.stream.Collectors;

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
        graph = new LevelGraph();
        unconnected = new ArrayList<>(graph.getNodes());
        buildMainPaths();
        createNodes(graph, data);
        //TODO shortcuts!
        buildBonusPaths();
        List<LevelGraphNode> list = graph.getNodes().stream().filter(
         node -> graph.getAdjList().get(node).size() ==2).collect(Collectors.toList());
        list.addAll(unconnected);
        connectNodesRandomly(graph, data, list); //the remainder
        //        applyRules(graph, data);
        list.forEach(node -> graph.removeLevelGraphNode(node));
        assignZonesIndices();
        return graph;
    }

    private void createNodes(LevelGraph graph, LevelData data) {
        float sizeMode = new Float(data.getIntValue(LEVEL_VALUES.SIZE_MODE)) / 10000;

        graph.addNode(ROOM_TYPE.THRONE_ROOM);

        int n = Math.round(sizeMode * 100* data.getIntValue(LEVEL_VALUES.COMMON_ROOM_COEF));
        graph.addNodes(ROOM_TYPE.COMMON_ROOM, n);
        //         n = Math.round(sizeMode * 1 + data.getIntValue(LEVEL_VALUES.TREASURE_ROOM_COEF));
        //        graph.addNodes(ROOM_TYPE.TREASURE_ROOM, n);
        //
        //        n = Math.round(sizeMode * 1 + data.getIntValue(LEVEL_VALUES.GUARD_ROOM_COEF));
        //        graph.addNodes(ROOM_TYPE.GUARD_ROOM, n);
        //
        //        n = Math.round(sizeMode * RandomWizard.getRandomInt(1 + data.getIntValue(LEVEL_VALUES.DEATH_ROOM_COEF)));
        //        graph.addNodes(ROOM_TYPE.DEATH_ROOM, n);
        //
        //        n = Math.round(sizeMode * 1 + data.getIntValue(LEVEL_VALUES.SECRET_ROOM_COEF));
        //        graph.addNodes(ROOM_TYPE.SECRET_ROOM, n);

        unconnected = new ArrayList<>(graph.getNodes());
        unconnected.removeIf(node -> ListMaster.isNotEmpty(graph.getAdjList().get(node)));

        //        for (DUNGEON_TEMPLATES sub : data.getTemplates()) {
        //            //zones!.. force links?
        //            switch (sub) {
        //
        //            }
        //        }
    }

    private void assignZonesIndices() {
        /**
         * TODO
         * from the start of each path, start building zone by adjMap
         * ignore overwrite - just let them grow for their radius
         * each step goes through all zones
         *
         * but where is each zone root located?
         *
         * this will create equal-sized zones or roughly so
         */
        List<LevelZone> zones = ZoneCreator.createZones(data);

        List<LevelGraphNode> unallocated = new ArrayList<>(graph.getNodes());
        List<LevelGraphNode> candidates = new ArrayList<>();
        for (GraphPath path : graph.getPaths()) {
            candidates.add(path.startNode);
            candidates.add(path.endNode);
            candidates.add(path.getNodes().get(path.getNodes().size() / 2));
        }
        Collections.shuffle(candidates);
        LevelGraphNode[]  tipNodes = candidates.stream().distinct().limit(zones.size())
         .collect(Collectors.toList()).toArray(new LevelGraphNode[zones.size()]);
        while (true) {
            int i = 0;
            for (LevelGraphNode tipNode : tipNodes) {
                if (tipNode==null )
                    continue;
                LevelGraphNode tip = tipNode;
                final int index = i;
                List<LevelGraphNode> nodes = graph.getAdjList().get(tipNode).stream().map(
                 edge ->tip==edge.getNodeOne()? edge.getNodeTwo(): edge.getNodeOne()).collect(Collectors.toList());
                tipNode = new RandomWizard<LevelGraphNode>()
                 .getRandomListItem(nodes);
                if (tipNode.getZoneIndex()!=-1)
                {
                    tipNodes[i]=null ;
                    continue;
                }
                nodes.forEach(node -> node.setZoneIndex(index));
                unallocated.removeAll(nodes);
                tipNodes[i++] = tipNode;
            }
            if (i==0)
                break;
            if (unallocated.isEmpty())
                break;
        }
        for (LevelGraphNode node : graph.getNodes()) {
            if (node.getZoneIndex() == -1) {
//                graph.getAdjList().get(node)
                LevelGraphNode  closest = findClosestZoneAssignedNode(node, null );
                node.setZoneIndex(closest.getZoneIndex());
            }
        }
        graph.setZones(zones);
    }

    private LevelGraphNode findClosestZoneAssignedNode(LevelGraphNode node , LevelGraphNode prevNode) {

        List<LevelGraphNode> nodes=    new ArrayList<>() ;
        for (LevelGraphEdge edge : graph.getAdjList().get(node)) {
            nodes.add(node==edge.getNodeOne()?edge.getNodeTwo() : edge.getNodeOne());
        }
        for (LevelGraphNode graphNode : nodes) {
            if (graphNode.getZoneIndex()!=-1)
                return graphNode;
        }
        nodes.removeIf(n -> n == prevNode);
        for (LevelGraphNode graphNode : nodes) {
           node = findClosestZoneAssignedNode(graphNode, node);
           if (node!=null )
               return node;
        }
        return null ;
    }

    private List<LevelGraphNode> getNodesForZone(LevelZone zone) {
        int radius = 4;
        switch (zone.getType()) {
            case BOSS_AREA:
                break;
            case OUTSKIRTS:
                break;
            case MAIN_AREA:
                break;
            case ENTRANCE:
                break;
        }
        List<LevelGraphNode> list = new ArrayList<>();
        for (GraphPath graphPath : graph.getPaths()) {
            for (LevelGraphNode node : graphPath.getNodes().values()) {
                list.add(node);

                graph.getAdjList().get(node);
            }
        }
        return list;
    }

    private void buildMainPaths() {
        int numberOfMainPaths = data.getIntValue(LEVEL_VALUES.MAIN_PATHS);
        LevelGraphNode startNode = graph.addNode(ROOM_TYPE.ENTRANCE_ROOM);
        LevelGraphNode exitNode = graph.addNode(ROOM_TYPE.EXIT_ROOM);
        buildPaths(numberOfMainPaths, startNode, exitNode, true);
    }

    private void buildBonusPaths() {
        int numberOfTreasurePaths = data.getIntValue(LEVEL_VALUES.BONUS_PATHS);
        GraphPath path = getPaths().iterator().next();
        LevelGraphNode startNode = path.getNodes().get(path.getNodes().size() / 2);
        LevelGraphNode exitNode = graph.addNode(ROOM_TYPE.TREASURE_ROOM);
        while (numberOfTreasurePaths > 0) {
            buildPaths(1, startNode, exitNode, false);
            numberOfTreasurePaths--;
        }
    }

    private void buildPaths(int numberOfPaths, LevelGraphNode startNode,
                            LevelGraphNode exitNode, boolean main) {

        while (numberOfPaths-- > 0) {//TODO can try reverse path too
            int steps =RandomWizard.getRandomIntBetween(66, 150)*data.getIntValue(
             main? LEVEL_VALUES.MAIN_PATH_LENGTH : LEVEL_VALUES.BONUS_PATH_LENGTH)/100;
            unconnected = new ArrayList<>(graph.getNodes());
            unconnected.remove(startNode);
            unconnected.remove(exitNode);
            unconnected.removeIf(
             n ->
              new DequeImpl<>(graph.getAdjList().get(n)).size() > 1 || //don't need more links
               n.getRoomType() == ROOM_TYPE.ENTRANCE_ROOM ||
               n.getRoomType() == ROOM_TYPE.EXIT_ROOM
            );
            //TODO refactor!

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
        boolean fromEnd = false;
        //what for? perhaps just from end?
        int i = 2;
        while (steps > 0) {
            steps--;
            //            fromEnd = !fromEnd; how to?
            LevelGraphNode node = (fromEnd) ? tip2 : tip1;
            LevelGraphNode node2 = getOrCreateLinkNode(node, i);
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
        nodes.put(i, endNode);
        connect(tip1, tip2);

        //sort by distance from start
        //        node = startNode;
        //        while(true){
        //            path.add(node);
        //            node= node.
        //        }
        return new GraphPath(nodes, startNode, endNode, graph, PATH_TYPE.easy);
    }

    public Set<GraphPath> getPaths() {
        return graph.getPaths();
    }

    private void connectNodesRandomly(LevelGraph graph, LevelData data, Collection<LevelGraphNode> unconnected) {
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
        if (unconnected.size() < 2) {
            //            getRandomLinkNode
            return null;
        }
        int n = RandomWizard.getRandomInt(unconnected.size());
        LevelGraphNode node = (LevelGraphNode) unconnected.toArray()[n];
        unconnected.remove(node);
        LevelGraphNode nodeTwo = chooseLinkNode(node, unconnected);
        return connect(node, nodeTwo);
    }

    private LevelGraphEdge connect(LevelGraphNode node, LevelGraphNode nodeTwo) {
        LEVEL_GRAPH_LINK_TYPE type = GeneratorEnums.LEVEL_GRAPH_LINK_TYPE.NORMAL;
        LevelGraphEdge link = new LevelGraphEdge(type, node, nodeTwo);
        graph.addEdge(link);
        main.system.auxiliary.log.LogMaster.log(1, "Connected: " + node
         + " with " + nodeTwo);
        return link;
    }


    private LevelGraphNode getOrCreateLinkNode(LevelGraphNode node, int i) {
        if (unconnected.size() > 0) {
            return unconnected.iterator().next();
        }
        ROOM_TYPE type = getLinkNodeType(node, i);
        return graph.addNode(type);
    }

    private ROOM_TYPE getLinkNodeType(LevelGraphNode node, int i) {
        if (node.getRoomType() == ROOM_TYPE.COMMON_ROOM) {
            if (!LevelGenerator.TEST_MODE)
                return RandomWizard.random() ? ROOM_TYPE.GUARD_ROOM : ROOM_TYPE.DEATH_ROOM;
        }
        return ROOM_TYPE.COMMON_ROOM;
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
