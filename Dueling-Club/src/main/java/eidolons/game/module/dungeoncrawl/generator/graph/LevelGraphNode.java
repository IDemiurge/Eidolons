package eidolons.game.module.dungeoncrawl.generator.graph;

import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder.ROOM_TYPE;
import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.GRAPH_NODE_ATTRIBUTE;

/**
 * Created by JustMe on 2/13/2018.
 */
public class LevelGraphNode {
    int index;
    ROOM_TYPE roomType;
    GRAPH_NODE_ATTRIBUTE[] appendices;
    private int zoneIndex;

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

    public int getZoneIndex() {
        return zoneIndex;
    }

    public void setZoneIndex(int zoneIndex) {
        this.zoneIndex = zoneIndex;
    }
}
