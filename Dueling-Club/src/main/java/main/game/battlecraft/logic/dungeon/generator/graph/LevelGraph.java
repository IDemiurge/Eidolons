package main.game.battlecraft.logic.dungeon.generator.graph;

import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.ROOM_TYPE;

import java.util.*;

/**
 * Created by JustMe on 2/13/2018.
 * <p>
 * node types
 */
public class LevelGraph {

    protected static int index = 0;
    private Set<LevelGraphNode> nodes;
    private Set<LevelGraphEdge> edges;
    private Map<LevelGraphNode, Set<LevelGraphEdge>> adjList;

    public LevelGraph() {
        index = 0;
        nodes = new HashSet<>();
        edges = new HashSet<>();
        adjList = new HashMap<>();
    }

    public LevelGraphNode addNode(ROOM_TYPE roomType) {
        LevelGraphNode node = new LevelGraphNode(roomType);
        addNode(node);
        return node;

    }

    public void addNodes(ROOM_TYPE roomType, int n) {
        while(n>0){
            n--;
            addNode(roomType);
        }
    }

    public LevelGraphNode getNodeById(int i) {
        int n=0;
        for (LevelGraphNode sub : nodes) {
            if (n==i)
                return sub;
            n++;
        }
        return null ;
    }

    public enum GRAPH_NODE_APPENDIX {
        LOCK,
    }

    public enum LEVEL_GRAPH_LINK_TYPE {
        NORMAL, AMBUSH, LONG, LOCKED,
    }

    public boolean addNode(LevelGraphNode v) {
        return nodes.add(v);
    }

    public boolean addNodes(Collection<LevelGraphNode> vertices) {
        return this.nodes.addAll(vertices);
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
