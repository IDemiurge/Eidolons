package elements.exec;

import content.LinkedStringMap;
import elements.exec.targeting.TargetGroup;
import framework.entity.Entity;
import logic.execution.event.IdRef;

import java.util.Map;

/**
 * Created by Alexander on 8/21/2023 Transparent Freeze to changes sometimes Clone vs Modify - careful
 * <p>
 * Wazzup with ID's? Maybe they're only needed for internal debugging?
 * <p>
 * This should exist ONLY for the duration of a single action execution! No 'passive ref' that may be modified at any
 * time and then is used to apply(ref)!
 */
public class EntityRef {
    private final Map<String, Entity> map = new LinkedStringMap<>();
    //data map object?
    private Integer valueInt = null;
    private Boolean valueBool = null;
    private String valueString = null;
    private TargetGroup group;
    private Entity match;
    private Entity source;
    private Entity target;

    public EntityRef(Entity source) {
        set(ReferenceKey.Source, source);
        setSource(source);
    }

    //TODO
    public EntityRef(IdRef idRef) {
        // idRef.getMap().keySet().for
        // Battle.current.getById(id)
    }

    public EntityRef() {
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

    //////////////////////region METHODS

    public EntityRef copy() {
        return clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected EntityRef clone() {
        EntityRef ref = new EntityRef();
        ref.map.putAll(map);
        ref.valueInt = valueInt;
        ref.valueBool = valueBool;
        ref.valueString = valueString;

        ref.match = match;
        ref.source = source;
        ref.target = target;
        return ref;
    }

    //endregion


    //////////////////////region GETTERS
    public Entity getMatch() {
        return match;
    }

    public Entity getSource() {
        return source;
    }

    public Entity getTarget() {
        return target;
    }

    public Entity get(String key) {
        return map.get(key);
    }


    public Integer getValueInt() {
        return valueInt;
    }

    public TargetGroup getGroup() {
        return group;
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

    //endregion

    //////////////////////region SETTERS
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

    public EntityRef setValueInt(Integer valueInt) {
        this.valueInt = valueInt;
        return this;
    }

    public EntityRef setGroup(TargetGroup group) {
        this.group = group;
        return this;
    }

    public EntityRef setValueBool(Boolean valueBool) {
        this.valueBool = valueBool;
        return this;
    }

    public EntityRef setValueString(String valueString) {
        this.valueString = valueString;
        return this;
    }

    public EntityRef setMatch(Entity match) {
        this.match = match;
        // set(ReferenceKey.Source, source);
        return this;
    }

    public EntityRef setSource(Entity source) {
        this.source = source;
        set(ReferenceKey.Source, source);
        return this;
    }

    public EntityRef setTarget(Entity target) {
        this.target = target;
        set(ReferenceKey.Target, target);
        return this;
    }
    //endregion
}
