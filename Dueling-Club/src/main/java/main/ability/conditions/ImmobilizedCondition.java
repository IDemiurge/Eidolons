package main.ability.conditions;

import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import main.entity.obj.unit.Unit;

public class ImmobilizedCondition extends MicroCondition {

    @Override
    public boolean check(Ref ref) {
        Unit unit = (Unit) getRef().getMatchObj();
        return unit.isImmobilized();
    }

}
