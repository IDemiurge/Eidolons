package framework.entity.sub;

import elements.content.enums.types.EntityTypes;
import elements.exec.Executable;
import framework.entity.field.Unit;

import java.util.Map;

/**
 * Created by Alexander on 8/20/2023
 */
public class UnitAction extends UnitSubEntity {
    EntityTypes.ActionType type;
    private Executable executable;
    // ActiveAbility active;  encapsulate targeting, wrap in props.get(targeting)
    // Effects boostEffects; //modify, add trigger fx, etc
    // boostMode //how to apply fx

    public UnitAction(Map<String, Object> valueMap, Unit unit) {
        super(valueMap, unit);
        executable = elements.exec.ExecBuilder.initExecutable(this);
    }

    public Executable getExecutable() {
        return executable;
    }

    public boolean isSpell() {
        return false;
    }

    public boolean isAttack() {
        return getType() != EntityTypes.ActionType.Defense;
    }

    public EntityTypes.ActionType getType() {
        if (type==null) {
            type = getEnum("action_type", EntityTypes.ActionType.class);
        }
        return type;
    }

    public void executed() {
        unit.getActionSet().setLastAction(this);
    }
}
