package eidolons.ability.conditions;

import eidolons.entity.obj.unit.Unit;
import main.elements.conditions.MicroCondition;
import main.entity.Ref;

public class ImmobilizedCondition extends MicroCondition {

    @Override
    public boolean check(Ref ref) {
        Unit unit = (Unit) ref.getMatchObj();
        return unit.isImmobilized();
    }

}
