package eidolons.game.exploration.dungeon.generator.graph;

import eidolons.game.exploration.dungeon.generator.GeneratorEnums;

/**
 * Created by JustMe on 2/13/2018.
 */
public class LevelGraphEdge {
    GeneratorEnums.LEVEL_GRAPH_LINK_TYPE linkType;
    LevelGraphNode nodeOne;
    LevelGraphNode nodeTwo;

    public LevelGraphEdge(
     LevelGraphNode nodeOne, LevelGraphNode nodeTwo) {
        this(GeneratorEnums.LEVEL_GRAPH_LINK_TYPE.NORMAL, nodeOne, nodeTwo);
    }

    public LevelGraphEdge(GeneratorEnums.LEVEL_GRAPH_LINK_TYPE linkType, LevelGraphNode nodeOne, LevelGraphNode nodeTwo) {
        this.linkType = linkType;
        this.nodeOne = nodeOne;
        this.nodeTwo = nodeTwo;
    }

    public GeneratorEnums.LEVEL_GRAPH_LINK_TYPE getLinkType() {
        return linkType;
    }

    public LevelGraphNode getNodeOne() {
        return nodeOne;
    }

    public LevelGraphNode getNodeTwo() {
        return nodeTwo;
    }


    @Override
    public String toString() {
        return " link: "+nodeOne+" with "+nodeTwo;

    }

    public LevelGraphNode getOtherNode(LevelGraphNode node) {
        return getNodeOne() == node ? getNodeTwo() : getNodeOne();
    }
}
