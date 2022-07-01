package logic.entity;

import logic.content.AUnitEnums;

import java.util.Map;

public class Entity {
    protected String name;
    protected Map<String, Object> valueMap; // from yaml, xml, enum

    public Entity(Map<String, Object> valueMap) {
        this.valueMap = valueMap;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getValueMap() {
        return valueMap;
    }

    public String getImagePath() {
        return getValueMap().get(AUnitEnums.UnitVal.image.toString()).toString();
    }

    public int getInt(Object identifier) {
        Object o = getValueMap().get(identifier.toString());
        return Integer.parseInt(o.toString());
    }

    public void setValue(String name, int val) {
        getValueMap().put(name, val);
    }
}
