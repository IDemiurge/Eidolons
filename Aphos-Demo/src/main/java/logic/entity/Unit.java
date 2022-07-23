package logic.entity;

import logic.lane.LanePos;

import java.util.Map;

public class Unit extends Entity {
    LanePos pos;
    public Unit(LanePos pos, Map<String, Object> valueMap) {
        super(valueMap);
        this.pos = pos;
    }

    public LanePos getPos() {
        return pos;
    }

    public void setPos(LanePos pos) {
        this.pos = pos;
    }

    public boolean isLeftSide() {
        return pos.leftSide;
    }
    @Override
    public String toString() {
        return "Unit - " + name ;
    }
}
