package main.game.battlecraft.logic.dungeon.generator.graph;

import main.game.battlecraft.logic.dungeon.generator.graph.LevelGraphMaster.PATH_TYPE;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 2/14/2018.
 */
public class GraphPath {
    LinkedHashSet<LevelGraphNode> nodes;
    //links
    LevelGraphNode  startNode;
    LevelGraphNode  endNode;
    LevelGraph graph;
    PATH_TYPE type;

    public GraphPath(LinkedHashSet<LevelGraphNode> nodes, LevelGraphNode startNode, LevelGraphNode endNode, LevelGraph graph, PATH_TYPE type) {
        this.nodes = nodes;
        this.startNode = startNode;
        this.endNode = endNode;
        this.graph = graph;
        this.type = type;
    }

    public GraphPath(LinkedHashSet<LevelGraphNode> nodes, PATH_TYPE type) {
        this.nodes = nodes;
        this.type = type;
        sortNodes();
        node = startNode;
        while (n < nodes.size()) {
            n++;
            node = findLinked(node, nodes);
        }
    }

    private void sortNodes() {
        nodes = new LinkedHashSet<>(nodes.stream().sorted(getComparator()).collect(Collectors.toSet()));
    }

    private Comparator<? super LevelGraphNode> getComparator() {
        return (n1, n2)->{


          return 1;
        };
    }
}
