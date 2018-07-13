package main.game.logic.dungeon.generator.graph;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import main.game.logic.dungeon.generator.GeneratorEnums.GRAPH_NODE_ATTRIBUTE;

/**
 * Created by JustMe on 2/13/2018.
 */
public class LevelGraphNode {
    int index;
    ROOM_TYPE roomType;
    GRAPH_NODE_ATTRIBUTE[] appendices;

    public LevelGraphNode(ROOM_TYPE roomType, GRAPH_NODE_ATTRIBUTE... appendices) {
        this.roomType = roomType;
        this.appendices = appendices;
        index = LevelGraph.index++;
    }

    public int getIndex() {
        return index;
    }

    public ROOM_TYPE getRoomType() {
        return roomType;
    }

    public GRAPH_NODE_ATTRIBUTE[] getAppendices() {
        return appendices;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return roomType+" node #"+index;

    }
}
