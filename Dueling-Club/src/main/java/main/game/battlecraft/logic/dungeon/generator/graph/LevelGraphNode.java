package main.game.battlecraft.logic.dungeon.generator.graph;

import main.game.battlecraft.logic.dungeon.building.DungeonBuilder.ROOM_TYPE;
import main.game.battlecraft.logic.dungeon.generator.GeneratorEnums.GRAPH_NODE_APPENDIX;

/**
 * Created by JustMe on 2/13/2018.
 */
public class LevelGraphNode {
    int index;
    ROOM_TYPE roomType;
    GRAPH_NODE_APPENDIX[] appendices;

    public LevelGraphNode(ROOM_TYPE roomType, GRAPH_NODE_APPENDIX... appendices) {
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

    public GRAPH_NODE_APPENDIX[] getAppendices() {
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
