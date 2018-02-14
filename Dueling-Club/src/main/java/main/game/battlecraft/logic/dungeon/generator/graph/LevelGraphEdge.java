package main.game.battlecraft.logic.dungeon.generator.graph;

import main.game.battlecraft.logic.dungeon.generator.graph.LevelGraph.LEVEL_GRAPH_LINK_TYPE;

/**
 * Created by JustMe on 2/13/2018.
 */
public class LevelGraphEdge {
    LEVEL_GRAPH_LINK_TYPE linkType;
    LevelGraphNode nodeOne;
    LevelGraphNode nodeTwo;

    public LevelGraphEdge(
     LevelGraphNode nodeOne, LevelGraphNode nodeTwo) {
        this(LEVEL_GRAPH_LINK_TYPE.NORMAL, nodeOne, nodeTwo);
    }

    public LevelGraphEdge(LEVEL_GRAPH_LINK_TYPE linkType, LevelGraphNode nodeOne, LevelGraphNode nodeTwo) {
        this.linkType = linkType;
        this.nodeOne = nodeOne;
        this.nodeTwo = nodeTwo;
    }

    public LEVEL_GRAPH_LINK_TYPE getLinkType() {
        return linkType;
    }

    public LevelGraphNode getNodeOne() {
        return nodeOne;
    }

    public LevelGraphNode getNodeTwo() {
        return nodeTwo;
    }
}
