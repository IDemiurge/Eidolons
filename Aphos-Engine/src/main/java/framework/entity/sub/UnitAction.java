package framework.entity.sub;

import elements.content.enums.types.EntityTypes;
import framework.entity.Entity;
import framework.entity.field.Unit;

import java.util.Map;

/**
 * Created by Alexander on 8/20/2023
 */
public class UnitAction extends UnitSubEntity {
    EntityTypes.ActionType type;
    // ActiveAbility active;  encapsulate targeting, wrap in props.get(targeting)
    // Effects boostEffects; //modify, add trigger fx, etc
    // boostMode //how to apply fx

    public UnitAction(Map<String, Object> valueMap, Unit unit) {
        super(valueMap, unit);
    }
}
