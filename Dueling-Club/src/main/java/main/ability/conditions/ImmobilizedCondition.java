package main.ability.conditions;

import main.elements.conditions.MicroCondition;
import main.entity.obj.unit.Unit;

public class ImmobilizedCondition extends MicroCondition {

    @Override
    public boolean check() {
        Unit unit = (Unit) getRef().getMatchObj();
        return unit.isImmobilized();
    }

}
