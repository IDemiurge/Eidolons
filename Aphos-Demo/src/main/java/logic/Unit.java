package logic;

import logic.lane.LanePos;

import java.util.Map;

public class Unit {
    String name;
    LanePos pos;

    Map<String, Object> valueMap; // from yaml, xml, enum

    public Unit(Map<String, Object> valueMap) {
        this.valueMap = valueMap;
    }

    public Unit(LanePos pos, Map<String, Object> valueMap) {
        this.pos = pos;
        this.valueMap = valueMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LanePos getPos() {
        return pos;
    }

    public void setPos(LanePos pos) {
        this.pos = pos;
    }

    public Map<String, Object> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Map<String, Object> valueMap) {
        this.valueMap = valueMap;
    }
}
