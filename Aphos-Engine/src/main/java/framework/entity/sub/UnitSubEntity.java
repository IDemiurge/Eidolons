package framework.entity.sub;

import framework.entity.Entity;
import framework.entity.field.Unit;

import java.util.Map;

/**
 * Created by Alexander on 8/21/2023
 */
public class UnitSubEntity extends Entity {
    Unit unit;

    public UnitSubEntity(Map<String, Object> valueMap, Unit unit) {
        super(valueMap);
        this.unit = unit;
    }

    public Unit getUnit() {
        return unit;
    }
}
