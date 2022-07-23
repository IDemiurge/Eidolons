package logic.entity;

import logic.content.AUnitEnums;
import logic.lane.LanePos;
import main.system.auxiliary.EnumMaster;

import java.util.Map;

import static logic.content.AUnitEnums.*;

public class Unit extends Entity {
    LanePos pos;
    private UnitType type;

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
    public int getLane() {
        return pos.lane;
    }

    @Override
    public String toString() {
        return "Unit - " + name;
    }

    public boolean isOnAtb() {
        return pos.cell == 0;
    }

    public UnitType getType() {
        if (type == null) {
            Object value = getValue(TYPE);
            if (value instanceof UnitType)
                type = (UnitType) value;
            else if (value != null)
                type = new EnumMaster<UnitType>().retrieveEnumConst(UnitType.class, value.toString());
        }
        return type;
    }

}
