package elements.exec;

import combat.Battle;
import content.LinkedStringMap;
import framework.entity.Entity;
import logic.execution.event.IdRef;

import java.util.Map;

/**
 * Created by Alexander on 8/21/2023 Transparent Freeze to changes sometimes Clone vs Modify - careful
 * <p>
 * Wazzup with ID's? Maybe they're only needed for internal debugging?
 *
 * This should exist ONLY for the duration of a single action execution! No 'passive ref' that may be
 * modified at any time and then is used to apply(ref)!
 */
public class EntityRef {
    private final Map<String, Entity> map = new LinkedStringMap<>();
    //data map object?
    private Integer valueInt=null;
    private Boolean valueBool=null;
    private String valueString=null;

    public EntityRef(Entity source) {
        set(ReferenceKey.Source, source);
    }

    //TODO
    public EntityRef(IdRef idRef) {
        // idRef.getMap().keySet().for
        // Battle.current.getById(id)
    }
    public EntityRef() {
    }

    public EntityRef set(String name, Entity entity) {
        map.put(name, entity);
        return this;
    }

    public EntityRef set(ReferenceKey reference, Entity entity) {
        map.put(reference.toString(), entity);
        for (String name : reference.altNames.split(",")) {
            map.put(name, entity);
        }
        return this;
    }

    @Override
    protected EntityRef clone() {
        EntityRef ref = new EntityRef();
        ref.map.putAll(map);
        ref.valueInt = valueInt;
        ref.valueBool = valueBool;
        ref.valueString = valueString;
        return ref;
    }

    public EntityRef copy() {
        return clone();
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

    public boolean isValueBool() {
        if (valueBool == null) {
            return false;
        }
        return valueBool;
    }

    public String getValueString() {
        return valueString;
    }
}
