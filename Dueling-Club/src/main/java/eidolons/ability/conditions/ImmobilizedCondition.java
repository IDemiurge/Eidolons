package eidolons.ability.conditions;

import main.elements.conditions.MicroCondition;
import main.entity.Ref;
import eidolons.entity.obj.unit.Unit;

public class ImmobilizedCondition extends MicroCondition {

    @Override
    public boolean check(Ref ref) {
        Unit unit = (Unit) ref.getMatchObj();
        return unit.isImmobilized();
    }

}
