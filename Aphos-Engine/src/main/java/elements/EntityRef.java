package elements;

import content.LinkedStringMap;
import framework.entity.Entity;

import java.util.Map;

/**
 * Created by Alexander on 8/21/2023
 * Transparent
 * Freeze to changes sometimes
 * Clone vs Modify - careful
 *
 * Wazzup with ID's? Maybe they're only needed for internal debugging?
 */
public class EntityRef {
    private final Map<String, Entity> map = new LinkedStringMap<>();
    private Integer valueInt;

    public EntityRef(Entity source) {
        set(source, ReferenceKey.Source);
    }

    public EntityRef set(Entity entity, String name) {
        map.put(name, entity);
        return this;
    }

    public EntityRef set(Entity entity, ReferenceKey reference) {
        map.put(reference.toString(), entity);
        for (String name : reference.altNames.split(",")) {
            map.put(name, entity);
        }
        return this;
    }

    public Entity get(String key) {
        return map.get(key);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public Integer getValueInt() {
        return valueInt;
    }

    public void setValueInt(Integer valueInt) {
        this.valueInt = valueInt;
    }

    public enum ReferenceKey {
        Source("Attacker,Source,Self"),
        Target("Attacked"),
        ;
        String altNames;

        ReferenceKey(String altNames) {
            this.altNames = altNames;
        }
    }
}
