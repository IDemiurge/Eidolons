package framework.entity;

import content.LinkedStringMap;
import elements.content.enums.EnumFinder;
import elements.content.stats.Property;
import main.system.auxiliary.NumberUtils;
import main.system.auxiliary.StringMaster;

import java.util.Map;

/**
 * Created by Alexander on 6/10/2023
 */
public abstract class Entity {
    protected String name;
    protected Map<String, Object> valueMap; // from yaml, xml, enum
    protected Map<String, Object> baseValueMap;

    public Entity(Map<String, Object> valueMap) {
        this.valueMap = new LinkedStringMap<>();
        this.valueMap.putAll(valueMap);
        this.baseValueMap.putAll(valueMap);


        this.name = valueMap.get(Property.Name).toString();
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        Object o = valueMap.get(Property.Image);
        if (o == null) {
            throw new RuntimeException(this + " has no image!");
        }
        return o.toString();
    }

    public Object getValue(String name) {
        return valueMap.get(name);
    }

    public void setValue(Object key, Object val) {
        valueMap.put(key.toString(), val);
    }

    @Override
    public String toString() {
        return name;
    }

    public int getInt(Object identifier) {
        Object o = valueMap.get(identifier.toString());
        if (o == null)
            return 0;
        return NumberUtils.getIntParse(o.toString());
    }

    public <T> T getEnum(String name, Class<T> className) {
        Object value = valueMap.get(name);
        if (StringMaster.isEmpty(value))
            return null;
        return EnumFinder.get(className, value);
    }
}
