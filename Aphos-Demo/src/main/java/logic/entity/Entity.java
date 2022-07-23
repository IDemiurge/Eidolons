package logic.entity;

import content.LinkedStringMap;
import logic.content.AUnitEnums;
import logic.core.Aphos;
import logic.lane.LanePos;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.data.MapMaster;

import java.util.Map;

public class Entity {
    protected String name;
    protected Map<String, Object> valueMap; // from yaml, xml, enum

    public Entity(Map<String, Object> valueMap) {
        if (valueMap instanceof LinkedStringMap) {
            this.valueMap = valueMap;
        } else {
            this.valueMap = new LinkedStringMap<>();
            this.valueMap.putAll(valueMap);
        }

        ////TODO refactor
        this.name = valueMap.get(AUnitEnums.NAME).toString();
        Aphos.game.add(this);
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getValueMap() {
        return valueMap;
    }

    public String getImagePath() {
        Object o = getValueMap().get(AUnitEnums.IMAGE);
        if (o == null) {
            throw new RuntimeException(this + " has no image!");
        }
        return o.toString();
    }

    public int getInt(Object identifier) {
        Object o = getValueMap().get(identifier.toString());
        if (o == null)
            return 0;
        return NumberUtils.getIntParse(o.toString());

    }

    public void setValue(String name, Object val) {
        getValueMap().put(name, val);
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isLeftSide() {
        return false;
    }

    public Object getValue(String s) {
        return valueMap.get(s);
    }

    public boolean isPlayerControlled() {
        return false;
    }

    public boolean isOnAtb() {
        return false;
    }

    public void killed(Entity source) {
        Aphos.game.getController().getDeathLogic().killed(this, source);
    }

    public void modVal(String key, int n) {
        MapMaster.addToIntegerMap(valueMap, key, n);
    }

    public float getFloat(String atb) {
        Object o = valueMap.get(atb);
        if (o == null) {
            return 0f;
        }
        return Float.parseFloat(o.toString());
    }

    public int getLane() {
        return 0;
    }
}
