package elements.exec;

import content.LinkedStringMap;
import elements.content.enums.types.CombatTypes;
import elements.exec.targeting.TargetGroup;
import framework.entity.Entity;
import framework.entity.field.FieldEntity;
import framework.entity.field.Unit;
import framework.entity.sub.UnitAction;
import logic.execution.event.IdRef;

import java.util.Map;
import java.util.function.Consumer;

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
    private Unit source;
    private FieldEntity target;
    private CombatTypes.DamageType damageType;
    private FieldEntity prevTarget;
    private EntityRef eventRef;
    public static final String EVENT_PREFIX = "event_";

    public EntityRef(Unit source) {
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

    public UnitAction getAction() {
        return (UnitAction) get("action");
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

    public EntityRef reversed() {
        if (!(target instanceof Unit)) {
            //error
            return this;
        }
        EntityRef copy = copy();
        copy.reverse();
        return copy;
    }

    private void reverse() {
        FieldEntity buffered = source;
        source = (Unit) target;
        target = buffered;
    }
    //endregion


    //////////////////////region GETTERS
    public Entity getMatch() {
        return match;
    }

    public Unit getSource() {
        return source;
    }

    public FieldEntity getTarget() {
        return target;
    }

    public Entity get(String key) {
        if (key.startsWith(EVENT_PREFIX)) {
            return eventRef.get(key.substring((EVENT_PREFIX.length())));
        }
        return map.get(key);
    }


    public CombatTypes.DamageType getDamageType() {
        return damageType;
    }

    public Integer getValueInt() {
        return valueInt;
    }

    public void applyToTargets(Consumer<FieldEntity> toApply) {
        if (group != null)
            group.getTargets().forEach(toApply);
        toApply.accept(getTarget());
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


    public FieldEntity getPrevTarget() {
        return prevTarget;
    }

    public EntityRef getEventRef() {
        return eventRef;
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

    public void setEventRef(EntityRef eventRef) {
        this.eventRef = eventRef;
    }

    public void setPrevTarget(FieldEntity prevTarget) {
        this.prevTarget = prevTarget;
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

    public EntityRef setSource(Unit source) {
        this.source = source;
        set(ReferenceKey.Source, source);
        return this;
    }

    public EntityRef setTarget(FieldEntity target) {
        this.target = target;
        set(ReferenceKey.Target, target);
        return this;
    }

    public void setAction(UnitAction action) {
        set("action", action);
    }

    public void setDamageType(CombatTypes.DamageType damageType) {
        this.damageType = damageType;
    }
    //endregion
}
