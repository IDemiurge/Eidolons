package eidolons.dungeons.generator.graph;

import eidolons.dungeons.generator.GeneratorEnums;

import java.util.Comparator;
import java.util.LinkedHashMap;

/**
 * Created by JustMe on 2/14/2018.
 */
public class GraphPath {
    LinkedHashMap<Integer, LevelGraphNode> nodes;
    //links
    LevelGraphNode  startNode;
    LevelGraphNode  endNode;
    LevelGraph graph;
    GeneratorEnums.PATH_TYPE type;

    public GraphPath(LinkedHashMap<Integer, LevelGraphNode> nodes, LevelGraphNode startNode, LevelGraphNode endNode, LevelGraph graph, GeneratorEnums.PATH_TYPE type) {
        this.nodes = nodes;
        this.startNode = startNode;
        this.endNode = endNode;
        this.graph = graph;
        this.type = type;
    }

    private void sortNodes() {
//        nodes = new LinkedHashSet<>(nodes.stream().sorted(getComparator()).collect(Collectors.toSet()));
//        while (n < nodes.size()) {
//            n++;
//            node = findLinked(node, nodes);
//        }
    }

    public LinkedHashMap<Integer, LevelGraphNode> getNodes() {
        return nodes;
    }

    public LevelGraphNode getStartNode() {
        return startNode;
    }

    public LevelGraphNode getEndNode() {
        return endNode;
    }

    public LevelGraph getGraph() {
        return graph;
    }

    public GeneratorEnums.PATH_TYPE getType() {
        return type;
    }

    private Comparator<? super LevelGraphNode> getComparator() {
        return (n1, n2)->{


          return 1;
        };
    }
}
