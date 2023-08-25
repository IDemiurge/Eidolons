package logic.execution.event.combat;

import elements.exec.EntityRef;

import java.util.Map;

/**
 * Created by Alexander on 8/22/2023
 * use a factory? chain up events?
 * RE-USE events?
 */
public class CombatEvent {
    private final CombatEventType type;
    private final Map argMap;
    private EntityRef ref;

    public CombatEvent(CombatEventType type, EntityRef ref, Map argMap) {
        this.type = type;
        this.argMap = argMap;
        setRef(ref);
    }

    public CombatEventType getType() {
        return type;
    }

    public EntityRef getRef() {
        return ref;
    }

    public void setRef(EntityRef ref) {
        this.ref = ref;
    }

    public Map getArgMap() {
        return argMap;
    }
}
