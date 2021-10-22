package eidolons.game.exploration.dungeons.generator.graph;


import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.exploration.dungeons.struct.LevelZone;

import java.util.*;

/**
 * Created by JustMe on 2/13/2018.
 * <p>
 * node types
 */
public class LevelGraph {

    protected static int index = 0;
    Set<GraphPath> paths;
    private final Set<LevelGraphNode> nodes;
    private final Set<LevelGraphEdge> edges;
    private final Map<LevelGraphNode, Set<LevelGraphEdge>> adjList;
    private List<LevelZone> zones;

    public LevelGraph() {
        index = 0;
        nodes = new LinkedHashSet<>();
        edges = new LinkedHashSet<>();
        adjList = new HashMap<>();

        paths = new HashSet<>();
    }

    @Override
    public String toString() {
        return "LevelGraph: \n" + paths.size() + " paths: " + paths +
         "\n" + nodes.size() + " nodes: " + nodes +
         "\n" + edges.size() + " edges: " + edges +
         "\n";
    }

    public Set<GraphPath> getPaths() {
        return paths;
    }

    public LevelGraphNode addNode(ROOM_TYPE roomType) {
        LevelGraphNode node = new LevelGraphNode(roomType);
        addNode(node);
        return node;

    }

    public void addNodes(ROOM_TYPE roomType, int n) {
        while (n > 0) {
            n--;
            addNode(roomType);
        }
    }

    public LevelGraphNode getNodeById(int i) {
        for (LevelGraphNode sub : nodes) {
            if (sub.getIndex() == i)
                return sub;
        }
        return null;
    }

    public boolean addNode(LevelGraphNode v) {
        adjList.putIfAbsent(v, new HashSet<>());
        return nodes.add(v);
    }

    public boolean addNodes(Collection<LevelGraphNode> vertices) {
         vertices.forEach(this::addNode);
        return true;
    }


    public boolean removeLevelGraphNode(LevelGraphNode v) {
        return nodes.remove(v);
    }

    public boolean addEdge(LevelGraphEdge e) {
        if (!edges.add(e)) return false;

        adjList.putIfAbsent(e.nodeOne, new HashSet<>());
        adjList.putIfAbsent(e.nodeTwo, new HashSet<>());

        adjList.get(e.nodeOne).add(e);
        adjList.get(e.nodeTwo).add(e);

        return true;
    }

    public Set<LevelGraphNode> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    public Set<LevelGraphEdge> getEdges() {
        return Collections.unmodifiableSet(edges);
    }

    public Map<LevelGraphNode, Set<LevelGraphEdge>> getAdjList() {
        return Collections.unmodifiableMap(adjList);
    }

    public List<LevelZone> getZones() {
        return zones;
    }

    public void setZones(List<LevelZone> zones) {
        this.zones = zones;
    }

    public LevelGraphNode findFirstNodeOfType(ROOM_TYPE type) {
        return getNodes().stream().filter(
         n -> n.getRoomType() == type).findFirst().orElse(null);
    }


    //     String TREASURE_ROOM = "T";
    //     String THRONE_ROOM = "M";
    //     String DEATH_ROOM = "D";
    //     String GUARD_ROOM = "G";
    //     String COMMON_ROOM = "O";
    //     String ENTRANCE_ROOM = "E";
    //     String EXIT_ROOM = "X";
    //     String SECRET_ROOM = "S";
    //     String BATTLEFIELD     =     "B";
    //     String CORRIDOR =     "R";
}
