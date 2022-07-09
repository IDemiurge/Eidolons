package logic.entity;

import content.LinkedStringMap;
import logic.content.AUnitEnums;

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
        return Integer.parseInt(o.toString());
    }

    public void setValue(String name, int val) {
        getValueMap().put(name, val);
    }

    @Override
    public String toString() {
        return name;
    }
}
