package eidolons.dungeons.generator.graph;

import eidolons.dungeons.generator.GeneratorEnums;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.struct.LevelZone;
import eidolons.dungeons.generator.LevelData;
import eidolons.dungeons.generator.LevelDataMaker.LEVEL_REQUIREMENTS;
import eidolons.dungeons.generator.level.ZoneCreator;
import main.system.SortMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.ListMaster;
import main.system.data.DataUnitFactory;
import main.system.datatypes.DequeImpl;
import main.system.datatypes.WeightMap;

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
        createNodes();
        buildMainPaths();
        //TODO shortcuts!
        buildBonusPaths();
        connectNodesRandomly(); //the remainder
        //        applyRules(graph, data);
        assignZonesIndices();
        return graph;
    }

    private void createNodes() {

        DataUnitFactory factory = new DataUnitFactory();
        factory.setValueNames(ROOM_TYPE.mainRoomTypes);
        factory.setValues(Arrays.stream(ROOM_TYPE.mainRoomTypes).map(
         type -> "" + data.getIntValue(LevelData.getROOM_COEF(type))
        ).collect(Collectors.toList()).toArray(new String[ROOM_TYPE.mainRoomTypes.length]));

        WeightMap<ROOM_TYPE> weightMap
         = new WeightMap<>(factory.constructDataString(), ROOM_TYPE.class);

        int n =
         data.getReqs().getIntValue(LEVEL_REQUIREMENTS.maxRooms) - graph.getNodes().size();

        while (n > 0) {
            ROOM_TYPE roomType = weightMap.getRandomByWeight();
            graph.addNodes(roomType, 1);
            if (graph.getNodes().stream().filter(
             node -> node.getRoomType() == roomType
            ).count() > data.getIntValue(LevelData.getROOM_COEF(roomType)) * 3 / 2) {
                weightMap.remove(roomType);
            }
            //other checks? min max for each room type...
            n--;
        }

        unconnected = new ArrayList<>(graph.getNodes());
        unconnected.removeIf(node -> ListMaster.isNotEmpty(graph.getAdjList().get(node)));

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
        Set<LevelGraphNode> candidates = new HashSet<>();
        for (GraphPath path : graph.getPaths()) {
            candidates.add(path.startNode);
            candidates.add(path.endNode);
            candidates.add(path.getNodes().get(path.getNodes().size() / 2));
        }
        candidates.removeIf(Objects::isNull);
        candidates.removeIf(node -> {

            for (LevelGraphNode candidate : candidates) {
                for (LevelGraphEdge edge : graph.getAdjList().get(candidate)) {
                    if (edge.getOtherNode(candidate) == node)
                        return true;
                }
            }
            return false;
        });
        //        Collections.shuffle(candidates);

        LevelGraphNode[] tipNodes = candidates.stream().distinct().limit(zones.size())
         .collect(Collectors.toList()).toArray(new LevelGraphNode[zones.size()]);
        for (int i = 0; i < tipNodes.length; i++) {
            if (tipNodes[i]==null) {
                tipNodes[i]=unallocated.remove(RandomWizard.getRandomIndex(unallocated));
            }
            tipNodes[i].setZoneIndex(i);
        }
        Loop loop = new Loop(graph.getNodes().size() * 2);
        while (loop.continues()) {
            //each zone's tipNode crawls through node web and greedily grabs more nodes!
            int i = 0;
            boolean newAcquired = false;
            for (LevelGraphNode tipNode : tipNodes) {
                if (tipNode == null)
                    continue;
                LevelGraphNode tip = tipNode;
                final int index = i;
                i++;
                List<LevelGraphNode> nodes = graph.getAdjList().get(tipNode).stream().
                 filter(edge -> {
                     if (edge.getNodeOne() == null)
                         return false;
                     return edge.getNodeTwo() != null;
                 })
                 .map(
                  edge -> tip == edge.getNodeOne() ? edge.getNodeTwo() : edge.getNodeOne()).collect(Collectors.toList());
                nodes.removeIf(n -> n.getZoneIndex() != -1 && n.getZoneIndex() != index);
                if (nodes.isEmpty())
                    continue;

                tipNode = new RandomWizard<LevelGraphNode>()
                 .getRandomListItem(nodes);
                zones.get(index).nodeAdded(tipNode);
                nodes.remove(tipNode);
                nodes.forEach(node -> {
                     if (node.getZoneIndex() == -1)
                         if (RandomWizard.chance(50+50/(1+zones.get(index).getNodeCount())))
                         {
                             zones.get(index).nodeAdded(node);
                         }
                 }
                );
                nodes.removeIf(n -> n.getZoneIndex() == -1  );

                unallocated.removeAll(nodes);
                tipNodes[index] = tipNode;
                newAcquired = true;
            }
            if (!newAcquired) //no one has acquired new nodes
                break;
            if (unallocated.isEmpty())
                break;
        }

        try {
            checkNodesZoneIndices(zones);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
            checkNodesZoneIndices(zones);
        }

        graph.setZones(zones);
        for (LevelZone zone : zones) {
            main.system.auxiliary.log.LogMaster.log(1, zone + " has nodes: " + zone.getNodeCount());
        }
    }

    private void checkNodesZoneIndices(List<LevelZone> zones) {
        for (LevelGraphNode node : graph.getNodes()) {
            if (node.getZoneIndex() == -1) {
                //                graph.getAdjList().getVar(node)
                if (isLeastAssignedZoneForMissing()){
                  node.setZoneIndex(zones.stream().sorted(new SortMaster<LevelZone>().getSorterByExpression_(
                     z -> -z.getNodeCount()
                    )).findFirst().get().getIndex());
                } else {
                    LevelGraphNode closest = findClosestZoneAssignedNode(node, null);
                    if (closest==null || closest.getZoneIndex() == -1) {
                        node.setZoneIndex(0);
                    } else
                        node.setZoneIndex(closest.getZoneIndex());
                }
                zones.get(node.getZoneIndex()).nodeAdded(node);
            }
        }
    }

    private boolean isLeastAssignedZoneForMissing() {
        return true;
    }

    private LevelGraphNode findClosestZoneAssignedNode(LevelGraphNode node, LevelGraphNode prevNode) {

        List<LevelGraphNode> nodes = new ArrayList<>();
        for (LevelGraphEdge edge : graph.getAdjList().get(node)) {
            nodes.add(node == edge.getNodeOne() ? edge.getNodeTwo() : edge.getNodeOne());
        }
        for (LevelGraphNode graphNode : nodes) {
            if (graphNode.getZoneIndex() != -1)
                return graphNode;
        }
        nodes.removeIf(n -> n == prevNode);
        for (LevelGraphNode graphNode : nodes) {
            try {
                node = findClosestZoneAssignedNode(graphNode, node);
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
            if (node != null)
                return node;
        }
        return null;
    }

    private List<LevelGraphNode> getNodesForZone(LevelZone zone) {
        int radius = 4; //dijkstra?
        switch (zone.getType()) {
            case GeneratorEnums.ZONE_TYPE.BOSS_AREA:
            case GeneratorEnums.ZONE_TYPE.ENTRANCE:
            case GeneratorEnums.ZONE_TYPE.MAIN_AREA:
            case GeneratorEnums.ZONE_TYPE.OUTSKIRTS:
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
        int numberOfMainPaths = data.getIntValue(GeneratorEnums.LEVEL_VALUES.MAIN_PATHS);
        LevelGraphNode startNode = graph.addNode(ROOM_TYPE.ENTRANCE_ROOM);
        LevelGraphNode exitNode = graph.addNode(ROOM_TYPE.EXIT_ROOM);
        buildPaths(numberOfMainPaths, startNode, exitNode, true);
    }

    private void buildBonusPaths() {
        int numberOfTreasurePaths = data.getIntValue(GeneratorEnums.LEVEL_VALUES.BONUS_PATHS);
        GraphPath mainPath = getPaths().iterator().next();

        while (numberOfTreasurePaths > 0) {
            LevelGraphNode startNode =
             (checkBonusPathFromEntrance(numberOfTreasurePaths))
              ? getOrCreateNodeOfType(ROOM_TYPE.ENTRANCE_ROOM)
              : mainPath.getNodes().get(mainPath.getNodes().size() / 2);
            LevelGraphNode exitNode = getOrCreateNodeOfType(ROOM_TYPE.TREASURE_ROOM);

            buildPaths(1, startNode, exitNode, false);
            numberOfTreasurePaths--;
        }
    }

    private boolean checkBonusPathFromEntrance(int n) {
        return n == 0;
    }

    private void buildPaths(int numberOfPaths, LevelGraphNode startNode,
                            LevelGraphNode exitNode, boolean main) {

        while (numberOfPaths-- > 0) {//TODO can try reverse path too
            int steps = RandomWizard.getRandomIntBetween(66, 150) * data.getIntValue(
             main ? GeneratorEnums.LEVEL_VALUES.MAIN_PATH_LENGTH : GeneratorEnums.LEVEL_VALUES.BONUS_PATH_LENGTH) / 100;

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
             , main);
            if (path == null) {
                break;
            } else
                getPaths().add(path);
        }

    }


    private GraphPath createPath(LevelGraphNode startNode,
                                 LevelGraphNode endNode, int steps, boolean main) {
        LevelGraphNode tip1 = startNode;
        LevelGraphNode tip2 = endNode;

        LinkedHashMap nodes = new LinkedHashMap();
        nodes.put(0, startNode);
        boolean fromEnd = false;
        //what for? perhaps just from end?
        int i = 2;
        while (steps > i - 2) {

            //            fromEnd = !fromEnd; how to?
            LevelGraphNode node = (fromEnd) ? tip2 : tip1;
            LevelGraphNode node2 = getOrCreateLinkNode(node, steps, i - 2, main);
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
        return new GraphPath(nodes, startNode, endNode, graph, GeneratorEnums.PATH_TYPE.easy);
    }

    public Set<GraphPath> getPaths() {
        return graph.getPaths();
    }

    private void connectNodesRandomly() {
        List<LevelGraphNode> list = graph.getNodes().stream().filter(
         node -> !unconnected.contains(node) &&
          graph.getAdjList().get(node).size() <= 2).collect(Collectors.toList());
        unconnected.addAll(list);
        //create a path from start to exit in N steps
        unconnected.removeIf(n ->
         //         n.getRoomType() == ROOM_TYPE.ENTRANCE_ROOM ||
         n.getRoomType() == ROOM_TYPE.EXIT_ROOM);

        while (true) {
            LevelGraphEdge link = connectTwoRandomNodes();
            if (link == null)
                break;
        }
        //apply rules that connect arbitrary nodes
        //TODO if enter/exit room is unconnected, it's a fail!
        unconnected.removeIf(node -> ListMaster.isNotEmpty(graph.getAdjList().get(node)));
         unconnected.forEach(node -> graph.removeLevelGraphNode(node));
    }


    private LevelGraphEdge connectTwoRandomNodes() {
        if (unconnected.size() < 2) {
            //            getRandomLinkNode
            return null;
        }
        int n = RandomWizard.getRandomInt(unconnected.size());
        LevelGraphNode node = (LevelGraphNode) unconnected.toArray()[n];
        LevelGraphNode nodeTwo = chooseLinkNode(node, unconnected);
        return connect(node, nodeTwo);
    }

    private int getConnectPriority(LevelGraphNode to, LevelGraphNode node) {
        if (to == node)
            return 0;
        int val = RandomWizard.getRandomInt(10) + 5;

        val = val * getTypeConnectPriorityMod(to, node) / 100;
        return val;
    }

    private int getTypeConnectPriorityMod(LevelGraphNode to, LevelGraphNode node) {
        switch (node.getRoomType()) {
            case THRONE_ROOM:
                if (to.getRoomType()==ROOM_TYPE.ENTRANCE_ROOM)
                    return 0;
                return 200;
            case TREASURE_ROOM:
            case DEATH_ROOM:
                return 50;

            case EXIT_ROOM:
            case ENTRANCE_ROOM:
                return 20;

        }
        return 100;
    }

    private LevelGraphEdge connect(LevelGraphNode node, LevelGraphNode nodeTwo) {
        GeneratorEnums.LEVEL_GRAPH_LINK_TYPE type = GeneratorEnums.LEVEL_GRAPH_LINK_TYPE.NORMAL;
        LevelGraphEdge link = new LevelGraphEdge(type, node, nodeTwo);
        graph.addEdge(link);
        main.system.auxiliary.log.LogMaster.log(1, "Connected: " + node
         + " with " + nodeTwo);

        if (graph.getAdjList().get(node).size() >= getMaxLinksForNode(nodeTwo)
         || RandomWizard.chance(getNodeLinksEndChance(nodeTwo)))
            unconnected.remove(nodeTwo);
        if (graph.getAdjList().get(node).size() >= getMaxLinksForNode(node)
         || RandomWizard.chance(getNodeLinksEndChance(node)))
            unconnected.remove(node);
        return link;
    }

    private int getNodeLinksEndChance(LevelGraphNode node) {
        return 25 + graph.getAdjList().get(node).size() * 15;
    }

    private int getMaxLinksForNode(LevelGraphNode node) {
        switch (node.getRoomType()) {
            case EXIT_ROOM:
                return 2;
            case ENTRANCE_ROOM:
                return 3;
        }
        return 4;
    }

    private LevelGraphNode getOrCreateNodeOfType(ROOM_TYPE type) {
        LevelGraphNode node = unconnected.stream().filter(
         n -> n.getRoomType() == type).findFirst().orElse(null);
        if (node == null)
            node = graph.addNode(type);
        return node;
    }

    private LevelGraphNode getOrCreateLinkNode(LevelGraphNode node, int n, int i, boolean main) {
        if (n - i > 1 || !main)
            if (unconnected.size() > 0) {
                return unconnected.iterator().next();
            }
        ROOM_TYPE type = getLinkNodeType(node, n, i, main);
        return graph.addNode(type);
    }

    private ROOM_TYPE getLinkNodeType(LevelGraphNode node, int n, int i, boolean main) {
        if (main) {
            if (n - i <= 1) {
                return ROOM_TYPE.THRONE_ROOM;
            }
        }
        if (node.getRoomType() == ROOM_TYPE.COMMON_ROOM) {
            return RandomWizard.random() ? ROOM_TYPE.GUARD_ROOM
             : ROOM_TYPE.DEATH_ROOM;
        }
        return ROOM_TYPE.COMMON_ROOM;
    }

    private LevelGraphNode chooseLinkNode(LevelGraphNode to,
                                          Collection<LevelGraphNode> unconnected) {
        List<LevelGraphNode> list = new ArrayList<>(unconnected);
        new SortMaster<LevelGraphNode>().sortByExpression_(list,
         node -> getConnectPriority(node, to));
        return list.get(0);
    }

    private void applyRules(LevelGraph graph, LevelData data) {
        int n = RandomWizard.getRandomInt(10);
        while (n > 0) {
            n--;
            GeneratorEnums.GRAPH_RULE rule = getRandomRule();
            Object[] args = getRuleArgs(rule);
            new GraphTransformer().applyRule(rule, graph, args);
        }
    }

    private Object[] getRuleArgs(GeneratorEnums.GRAPH_RULE rule) {
        switch (rule) {

        }
        return new Object[0];
    }

    private GeneratorEnums.GRAPH_RULE getRandomRule() {
        return new EnumMaster<GeneratorEnums.GRAPH_RULE>().getRandomEnumConst(GeneratorEnums.GRAPH_RULE.class);
    }


}
